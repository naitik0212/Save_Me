package com.example.group17.medaas;

import android.app.job.JobScheduler;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group17.medaas.API.GoogleETA.ETAGet;
import com.example.group17.medaas.API.GoogleETA.callback.OnGetETAResponseSuccess;
import com.example.group17.medaas.API.save.SaveMeGet;
import com.example.group17.medaas.API.save.SaveMePost;
import com.example.group17.medaas.API.save.callback.OnGetSaveMeResponseSuccess;
import com.example.group17.medaas.API.save.callback.OnPostSaveMeResponseSuccess;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.UserGet;
import com.example.group17.medaas.API.user.callback.OnGetUserResponseSuccess;
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

    // update thread for ETA
    private Thread threadETA;
    private int maxETA = 0;
    private boolean continueETA = false;

    private TextView docListTV = null;
    private ImageButton saveMe;
    private ImageButton call911;
    private Button cancelSaveMe;
    private Button logout;
    private Button completedSaveMe;
    private ProgressBar etaBar;
    private TextView etaText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing handler here attaches it to this thread
        mHandler = new Handler();

        // retrive active session if it exists
        Properties.retriveSessionFromFile();

        saveMe = (ImageButton) findViewById(R.id.SaveMeUser);
        call911 = (ImageButton) findViewById(R.id.call911);
        cancelSaveMe = (Button) findViewById(R.id.CancelSaveMeUser);
        completedSaveMe = (Button) findViewById(R.id.CompleteSaveMeUser);
        logout = (Button) findViewById(R.id.LogoutUser);
        etaBar = (ProgressBar) findViewById(R.id.ETApbUser);
        etaText = (TextView) findViewById(R.id.ETAUser);

        docListTV = (TextView) findViewById(R.id.DocList);
        docListTV.setMovementMethod(new ScrollingMovementMethod());

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

        final Context context = this;

        call911.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:2138338030"));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
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
                                    continueETA = true;
                                    Log.d("ETA start", "run: starting ETA thread");
                                    threadETA.start();
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
        Log.d("UI refreshing ON:", "starting updateTracker");
        updateTracker.start();

        // define ETA update thread
        threadETA = new Thread(new Runnable() {
            @Override
            public void run() {
                while(continueETA && Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_DOCTOR_RESPONDED)) {

                    // update locations
                    Log.d("inside ETA thread", "run: updating client user");
                    new UserGet().request(getApplicationContext(), Properties.clientDoctorSession.getDoctorUser().getId(),
                            new OnGetUserResponseSuccess() {
                                @Override
                                public void afterGetResponseSuccess(User user, int tokenId) {
                                    Log.d("inside ETA thread", "run: updated client user");
                                    Properties.clientDoctorSession.setDoctorUser(user);
                                }
                            }
                    );
                    Log.d("inside ETA thread", "run: updating doctor user");
                    new UserGet().request(getApplicationContext(), Properties.clientDoctorSession.getClientUser().getId(),
                            new OnGetUserResponseSuccess() {
                                @Override
                                public void afterGetResponseSuccess(User user, int tokenId) {
                                    Log.d("inside ETA thread", "run: updated doctor user");
                                    Properties.clientDoctorSession.setClientUser(user);
                                    Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                                }
                            }
                    );

                    // update ETA
                    Log.d("after updating users:", "run: getting ETA from Google API");
                    ETAGet etaGet = new ETAGet();
                    etaGet.request(getApplicationContext(), Properties.clientDoctorSession.getDoctorUser().getLocation(), Properties.clientDoctorSession.getClientUser().getLocation(),
                            new OnGetETAResponseSuccess() {
                                @Override
                                public void afterGetResponseSuccess(int ETA) {
                                    if (ETA != Properties.ETA_NULL) {
                                        Log.d("ETA response:", Integer.toString(ETA));
                                        Properties.clientDoctorSession.setETA(ETA);
                                        Log.d("ETA success:", "afterGetResponseSuccess: posting ETA updates to UI");
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (Properties.clientDoctorSession.getETA() == Properties.ETA_NULL) return;
                                                String etaString = "ETA: ";
                                                etaString += Integer.toString((int) Properties.clientDoctorSession.getETA() / 60) + " min ";
                                                etaString += Integer.toString((int) Properties.clientDoctorSession.getETA() % 60) + " s" ;
                                                etaText.setText(etaString);
                                                if (maxETA < Properties.clientDoctorSession.getETA()) {
                                                    maxETA = Properties.clientDoctorSession.getETA();
                                                    etaBar.setMax(maxETA);
                                                }
                                                etaBar.setProgress(Properties.clientDoctorSession.getETA());
                                                Log.d("ETA UI updated:", "afterGetResponseSuccess: progress bar and text view updated");
                                            }
                                        });
                                    } else {
                                        Log.d("ETA response:", "null eta reported from google API");
                                    }
                                }
                            });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                continueETA = false;
            }
        });
    }

    private void updateUI_STATUS_READY() {

        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(true);
        etaText.setVisibility(View.INVISIBLE);
        etaText.setText("");
        etaBar.setVisibility(View.INVISIBLE);
        cancelSaveMe.setVisibility(View.INVISIBLE);
        completedSaveMe.setVisibility(View.INVISIBLE);
        docListTV.setText("Hello " + Properties.user.getFirstName() + " " + Properties.user.getLastName() + "\nWe are here for your safety");
    }

    private void updateUI_CLIENT_REQUESTED() {
        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(false);
        etaText.setVisibility(View.INVISIBLE);
        etaText.setText("");
        etaBar.setVisibility(View.INVISIBLE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.INVISIBLE);
        docListTV.setText("Searching doctors in vicinity...");
    }

    private void updateUI_DOCTOR_RESPONDED() {
        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(false);
        etaText.setVisibility(View.VISIBLE);
        etaBar.setVisibility(View.VISIBLE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.VISIBLE);

        String text = "Following doctor will attend you: \n\n";
        text += Properties.clientDoctorSession.getDoctorUser().getFirstName() + " " +
                Properties.clientDoctorSession.getDoctorUser().getLastName() + " " +
                Properties.clientDoctorSession.getDoctorUser().getPhoneNumber();
        docListTV.setText(text);
    }

    private void updateUI_DOCTOR_CANCELLED() {
        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(false);
        etaText.setVisibility(View.INVISIBLE);
        etaText.setText("");
        etaBar.setVisibility(View.INVISIBLE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.INVISIBLE);
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
            text += "\n... please wait until someone responds.\n\n";
            docListTV.setText(text);
        }
    }

    @Override
    public void onBackPressed() {
        updateUI = false;
        continueETA = false;
        try {
            updateTracker.join();
            threadETA.join();
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
