package com.example.group17.medaas;

import android.*;
import android.app.job.JobScheduler;
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
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.save.SaveMeGet;
import com.example.group17.medaas.API.save.SaveMePost;
import com.example.group17.medaas.API.save.callback.OnGetSaveMeResponseSuccess;
import com.example.group17.medaas.API.save.callback.OnPostSaveMeResponseSuccess;
import com.example.group17.medaas.API.user.UserGet;
import com.example.group17.medaas.API.user.callback.OnGetUserResponseSuccess;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

public class MainActivityDoctor extends AppCompatActivity {
    // Thread handler
    private Handler mHandler;
    private String currentStatus;
    private boolean updateUI = true;
    private Thread updateTracker;
    private final int SCREEN_REFRESH_RATE_IN_MILLIS = 300;

    // update thread for ETA
    private Thread threadETA;
    private int maxETA = 0;


    private TextView reqDetailTV = null;
    private ImageButton saveMe;
    private ImageButton call911;
    private Button cancelSaveMe;
    private Button logout;
    private Button completedSaveMe;
    private Button acceptSaveMe;
    private Button denySaveMe;
    private ProgressBar etaBar;
    private TextView etaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_doctor);

        // Initializing handler here attaches it to this thread
        mHandler = new Handler();

        currentStatus = "force update ui";

        // retrive active session if it exists
        Properties.retriveSessionFromFile();

        saveMe = (ImageButton) findViewById(R.id.SaveMeDoctor);
        cancelSaveMe = (Button) findViewById(R.id.CancelSaveMeDoctor);
        call911 = (ImageButton) findViewById(R.id.call911);
        completedSaveMe = (Button) findViewById(R.id.CompleteSaveMeDoctor);
        logout = (Button) findViewById(R.id.Logout);
        acceptSaveMe = (Button) findViewById(R.id.Accept);
        denySaveMe = (Button) findViewById(R.id.Deny);
        etaBar = (ProgressBar) findViewById(R.id.ETApbDoctor);
        etaText = (TextView) findViewById(R.id.ETADoctor);

        reqDetailTV = (TextView) findViewById(R.id.ReqDetail);
        reqDetailTV.setMovementMethod(new ScrollingMovementMethod());

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
                Intent activityChangeIntent = new Intent(MainActivityDoctor.this, Register.class);
                MainActivityDoctor.this.startActivity(activityChangeIntent);
                finish();
            }
        });

        final Context context = this;

        call911.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:2138338030"));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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


        cancelSaveMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveMePost saveMePost = new SaveMePost();
                if (Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_CLIENT_REQUESTED_OUTGOING)) {
                    saveMePost.requestCancelAsClient(getApplicationContext(), "client", Properties.user.getId(),
                            new OnPostSaveMeResponseSuccess() {
                                @Override
                                public void afterPostResponseSuccess(JSONObject response) {
                                    Log.d("", "afterPostResponseSuccess: request cancelled with response: " + response.toString());
                                    Toast.makeText(getApplicationContext(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                                    Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_READY);
                                    Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                                }
                            });
                } else {
                    saveMePost.requestCancelAsDoctor(getApplicationContext(), Properties.user.getUserType(), Properties.clientDoctorSession.getClientUser().getId(),Properties.user.getId(),
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
            }
        });

        completedSaveMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveMePost saveMePost = new SaveMePost();
                saveMePost.requestCompleteAsDoctor(getApplicationContext(), Properties.user.getUserType(), Properties.clientDoctorSession.getClientUser().getId(),Properties.user.getId(),
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
                                    Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_CLIENT_REQUESTED_OUTGOING);
                                    Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                                    Properties.clientDoctorSession.setDoctorsList(users);

                                    if (users == null || users.length == 0)
                                        Toast.makeText(MainActivityDoctor.this, "No doctors available in this area. \n Please cancel request and try again in some time \n or call 911 now.", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        acceptSaveMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SaveMePost saveMePost = new SaveMePost();
                saveMePost.requestDocResponse(getApplicationContext(), Properties.clientDoctorSession.getClientUser().getId(),Properties.user.getId(),
                        new OnPostSaveMeResponseSuccess() {
                            @Override
                            public void afterPostResponseSuccess(JSONObject response) {
                                Log.d("", "afterPostResponseSuccess: request accepted with response: " + response.toString());
                                Toast.makeText(getApplicationContext(), "Patient will be notified with your details", Toast.LENGTH_SHORT).show();
                                Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_DOCTOR_RESPONDED_OUTGOING);
                                Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                            }
                        });
            }
        });

        denySaveMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_READY);
                Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                Toast.makeText(getApplicationContext(), "Request denied", Toast.LENGTH_SHORT).show();
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
                        } else if (newStatus.equals(ClientDoctorSession.STATUS_CLIENT_REQUESTED_OUTGOING)) {
                            // call ui update for status doctor requested
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI_CLIENT_REQUESTED_OUTGOING();
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
                            threadETA.start();
                        } else if (newStatus.equals(ClientDoctorSession.STATUS_DOCTOR_RESPONDED_OUTGOING)) {
                            // call ui update for status doctor responded
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI_DOCTOR_RESPONDED_OUTGOING();
                                }
                            });
                            threadETA.start();
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
                    if (newStatus.equals(ClientDoctorSession.STATUS_CLIENT_REQUESTED_OUTGOING) && Properties.clientDoctorSession.getDoctorsList() != null) {
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

        // define ETA update thread
        threadETA = new Thread(new Runnable() {
            @Override
            public void run() {
                while(Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_DOCTOR_RESPONDED_OUTGOING) ||
                        Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_DOCTOR_RESPONDED )) {

                    // update locations
                    new UserGet().request(getApplicationContext(), Properties.clientDoctorSession.getDoctorUser().getId(),
                            new OnGetUserResponseSuccess() {
                                @Override
                                public void afterGetResponseSuccess(User user, int tokenId) {
                                    Properties.clientDoctorSession.setDoctorUser(user);
                                }
                            }
                    );
                    new UserGet().request(getApplicationContext(), Properties.clientDoctorSession.getClientUser().getId(),
                            new OnGetUserResponseSuccess() {
                                @Override
                                public void afterGetResponseSuccess(User user, int tokenId) {
                                    Properties.clientDoctorSession.setClientUser(user);
                                    Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);
                                }
                            }
                    );

                    // update ETA
                    ETAGet etaGet = new ETAGet();
                    etaGet.request(getApplicationContext(), Properties.clientDoctorSession.getDoctorUser().getLocation(), Properties.clientDoctorSession.getClientUser().getLocation(),
                            new OnGetETAResponseSuccess() {
                                @Override
                                public void afterGetResponseSuccess(int ETA) {
                                    if (ETA != Properties.ETA_NULL) {
                                        Properties.clientDoctorSession.setETA(ETA);
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
                                            }
                                        });
                                    }
                                }
                            });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
        acceptSaveMe.setVisibility(View.INVISIBLE);
        denySaveMe.setVisibility(View.INVISIBLE);
        Log.d("", "updateUI_STATUS_READY: updating text view...");
        reqDetailTV.setText("Hello Dr. " + Properties.user.getFirstName() + " " + Properties.user.getLastName() + "\nYou will receive patient emergencies here.\n\n\"Medical emergency? Click on SAVE ME button now!");
    }

    private void updateUI_CLIENT_REQUESTED_OUTGOING() {
        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(false);
        etaText.setVisibility(View.INVISIBLE);
        etaText.setText("");
        etaBar.setVisibility(View.INVISIBLE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.INVISIBLE);
        acceptSaveMe.setVisibility(View.INVISIBLE);
        denySaveMe.setVisibility(View.INVISIBLE);
        reqDetailTV.setText("Searching doctors in vicinity...");
    }

    private void updateUI_DOCTOR_RESPONDED() {
        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(false);
        etaText.setVisibility(View.VISIBLE);
        etaBar.setVisibility(View.VISIBLE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.VISIBLE);
        acceptSaveMe.setVisibility(View.INVISIBLE);
        denySaveMe.setVisibility(View.INVISIBLE);

        String text = "Following doctor will attend you: \n\n";
        text += Properties.clientDoctorSession.getDoctorUser().getFirstName() + " " +
                Properties.clientDoctorSession.getDoctorUser().getLastName() + " " +
                Properties.clientDoctorSession.getDoctorUser().getPhoneNumber() + "\n";
        reqDetailTV.setText(text);
    }


    private void updateUI_CLIENT_REQUESTED() {
        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(false);
        etaText.setVisibility(View.INVISIBLE);
        etaText.setText("");
        etaBar.setVisibility(View.INVISIBLE);
        cancelSaveMe.setVisibility(View.INVISIBLE);
        completedSaveMe.setVisibility(View.INVISIBLE);
        acceptSaveMe.setVisibility(View.VISIBLE);
        denySaveMe.setVisibility(View.VISIBLE);

        String text = "Medical emergency requested by...: \n\n";
        text += Properties.clientDoctorSession.getClientUser().getFirstName() + " " +
                Properties.clientDoctorSession.getClientUser().getLastName() + " " +
                Properties.clientDoctorSession.getClientUser().getPhoneNumber() + "\n";

        reqDetailTV.setText(text);
    }

    private void updateUI_DOCTOR_RESPONDED_OUTGOING() {
        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(false);
        etaText.setVisibility(View.VISIBLE);
        etaBar.setVisibility(View.VISIBLE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.VISIBLE);
        acceptSaveMe.setVisibility(View.INVISIBLE);
        denySaveMe.setVisibility(View.INVISIBLE);

        String text = "You are attending this patient...: \n\n";
        text += Properties.clientDoctorSession.getClientUser().getFirstName() + " " +
                Properties.clientDoctorSession.getClientUser().getLastName() + " " +
                Properties.clientDoctorSession.getClientUser().getPhoneNumber() + "\n\n";

        reqDetailTV.setText(text);
    }

    private void updateUI_DOCTOR_CANCELLED() {
        saveMe.setVisibility(View.VISIBLE);
        saveMe.setEnabled(false);
        etaText.setVisibility(View.INVISIBLE);
        etaText.setText("");
        etaBar.setVisibility(View.INVISIBLE);
        cancelSaveMe.setVisibility(View.VISIBLE);
        completedSaveMe.setVisibility(View.INVISIBLE);
        acceptSaveMe.setVisibility(View.INVISIBLE);
        denySaveMe.setVisibility(View.INVISIBLE);
        reqDetailTV.setText("Doctor is not available.\nNotifying other doctors in vicinity...");
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
            reqDetailTV.setText(text);
        }
    }

    @Override
    public void onBackPressed() {
        updateUI = false;
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
