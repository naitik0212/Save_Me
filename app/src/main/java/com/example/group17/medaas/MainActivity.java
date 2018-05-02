package com.example.group17.medaas;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group17.medaas.API.save.SaveMeGet;
import com.example.group17.medaas.API.save.SaveMePost;
import com.example.group17.medaas.API.save.callback.OnGetSaveMeResponseSuccess;
import com.example.group17.medaas.API.save.callback.OnPostSaveMeResponseSuccess;
import com.example.group17.medaas.API.model.User;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Thread handler
    private Handler mHandler;
    private String currentStatus = "force update screen";
    private boolean updateUI = true;
    private Thread updateTracker;
    private final int SCREEN_REFRESH_RATE_IN_MILLIS = 300;

    private TextView docListTV = null;
    private Button saveMe;
    private Button cancelSaveMe;
    private Button logout;
    private Button completedSaveMe;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing handler here attaches it to this thread
        mHandler = new Handler();

        // retrive active session if it exists
        Properties.retriveSessionFromFile();

        saveMe = (Button) findViewById(R.id.SaveMeUser);
        cancelSaveMe = (Button) findViewById(R.id.CancelSaveMeUser);
        completedSaveMe = (Button) findViewById(R.id.CompleteSaveMeUser);
        logout = (Button) findViewById(R.id.LogoutUser);

        docListTV = (TextView) findViewById(R.id.DocList);

        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // stop location update service
                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                LocationUpdateService.stopLocationUpdates(jobScheduler);

                // delete cred file
                File credFile = new File(Properties.credFile);
                if (credFile.exists()) credFile.delete();
                Log.d("", "Logout: user details deleted!");

                // delete active session file if exists
                Properties.clientDoctorSession = null;
                File activeSessionFile = new File(Properties.activeSessionFile);
                if (activeSessionFile.exists()) activeSessionFile.delete();

                // go back to Register/Login screen
                Intent activityChangeIntent = new Intent(MainActivity.this, Register.class);
                MainActivity.this.startActivity(activityChangeIntent);
                finish();
            }
        });

        cancelSaveMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveMePost saveMePost = new SaveMePost();
                saveMePost.requestCancelAsClient(getApplicationContext(), Properties.user.getUserType(), Properties.user.getId(),
                        new OnPostSaveMeResponseSuccess() {
                            @Override
                            public void afterPostResponseSuccess(JSONObject response) {
                                Log.d("", "afterPostResponseSuccess: request cancelled with response: " + response.toString());
                                Toast.makeText(getApplicationContext(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                                Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_READY);
                                Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                            }
                        });
            }
        });

        completedSaveMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveMePost saveMePost = new SaveMePost();
                saveMePost.requestCompleteAsClient(getApplicationContext(), Properties.user.getUserType(), Properties.user.getId(),
                        new OnPostSaveMeResponseSuccess() {
                            @Override
                            public void afterPostResponseSuccess(JSONObject response) {
                                Log.d("", "afterPostResponseSuccess: request cancelled with response: " + response.toString());
                                Toast.makeText(getApplicationContext(), "Request Completed", Toast.LENGTH_SHORT).show();
                                Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_READY);
                                Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                            }
                        });
            }
        });

        saveMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                if (Properties.user != null) {
                    Long id = Properties.user.getId();

                    SaveMeGet saveMeGet = new SaveMeGet();
                    saveMeGet.request(getApplicationContext(), id,
                            new OnGetSaveMeResponseSuccess() {
                                @Override
                                public void afterGetResponseSuccess(User[] users) {
                                    Properties.clientDoctorSession = new ClientDoctorSession();
                                    Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_CLIENT_REQUESTED);
                                    Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                                    Properties.clientDoctorSession.setDoctorsList(users);

                                    if (users == null || users.length == 0)
                                        Toast.makeText(MainActivity.this, "No doctors available in this area. \n Please cancel request and try again in some time \n or call 911 now.", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        // Start thread to check status and update screen accordingly
        updateTracker = new Thread(new Runnable() {
            @Override
            public void run() {
                while(updateUI) {
                    String prevStatus = currentStatus;
                    String newStatus = null;
                    if (Properties.clientDoctorSession == null) {
                        newStatus = ClientDoctorSession.STATUS_READY;
                    } else {
                        newStatus = Properties.clientDoctorSession.getStatus();
                    }

                    // detect change in status and update
                    if (!newStatus.equals(prevStatus)) {
                        if (newStatus.equals(ClientDoctorSession.STATUS_READY)) {
                            // call ui update for status ready
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI_STATUS_READY();
                                }
                            });
                        } else if (newStatus.equals(ClientDoctorSession.STATUS_CLIENT_REQUESTED)) {
                            // call ui update for status client requested
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI_CLIENT_REQUESTED();
                                }
                            });
                        } else if (newStatus.equals(ClientDoctorSession.STATUS_DOCTOR_RESPONDED)) {
                            // call ui update for status doctor responded
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI_DOCTOR_RESPONDED();
                                }
                            });
                        } else if (newStatus.equals(ClientDoctorSession.STATUS_DOCTOR_CANCELLED)) {
                            // call ui update for status doctor cancelled
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI_DOCTOR_CANCELLED();
                                }
                            });
                        }
                        currentStatus = newStatus;
                    }
                    // check if doctor's list is received...
                    if (newStatus.equals(ClientDoctorSession.STATUS_CLIENT_REQUESTED) && Properties.clientDoctorSession.getDoctorsList() != null) {
                        // call ui update for showing list of doctors
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateUI_DOCTOR_LIST();
                            }
                        });
                    }

                    try {
                        Thread.sleep(SCREEN_REFRESH_RATE_IN_MILLIS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateTracker.start();
    }

    private void updateUI_STATUS_READY() {
        saveMe.setVisibility(View.VISIBLE);
        cancelSaveMe.setVisibility(View.GONE);
        completedSaveMe.setVisibility(View.GONE);
        docListTV.setText("Medical emergency? Click on SAVE ME button now!");
    }

    private void updateUI_CLIENT_REQUESTED() {
        saveMe.setVisibility(View.GONE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.GONE);
        docListTV.setText("Searching doctors in vicinity...\n\n ...click Cancel to cancel the request.");
    }

    private void updateUI_DOCTOR_RESPONDED() {
        saveMe.setVisibility(View.GONE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.VISIBLE);

        String text = "Following doctor will attend you: \n\n";
        text += Properties.clientDoctorSession.getDoctorUser().getFirstName() + " " +
                Properties.clientDoctorSession.getDoctorUser().getLastName() + " " +
                Properties.clientDoctorSession.getDoctorUser().getPhoneNumber() + "\n";
        text += "...click Cancel to cancel the request, or Completed to finish the request.";
        docListTV.setText(text);
    }

    private void updateUI_DOCTOR_CANCELLED() {
        saveMe.setVisibility(View.GONE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.GONE);
        docListTV.setText("Doctor is not available.\nNotifying other doctors in vicinity...");
    }

    private void updateUI_DOCTOR_LIST() {
        if (Properties.clientDoctorSession.getDoctorsList() != null) {
            String text = "Doctors near you...\n";
            for (User doc : Properties.clientDoctorSession.getDoctorsList()) {
                text += doc.getFirstName() + " " +
                        doc.getLastName() + " " +
                        doc.getPhoneNumber() + "\n";
            }
            text += "... please wait until someone responds.\n\n";
            text += "...click Cancel to cancel the request.";
            docListTV.setText(text);
        }
    }

    @Override
    public void onBackPressed() {
        updateUI = false;
        try {
            updateTracker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Properties.clientDoctorSession != null) {
            // save to file
            Gson gson = new Gson();
            String json = gson.toJson(Properties.clientDoctorSession);
            Properties.saveToFile(json,Properties.credDir,Properties.activeSessionFile);
        }
        super.onBackPressed();
    }
}
