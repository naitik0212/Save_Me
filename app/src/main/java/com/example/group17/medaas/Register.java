package com.example.group17.medaas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;


import com.example.group17.medaas.API.model.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Register extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        final Button Register = (Button) findViewById(R.id.Register);
        final Button Login = (Button) findViewById(R.id.login_button);

        Register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(Register.this, RegisterActivity.class);
                Register.this.startActivity(activityChangeIntent);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(Register.this, loginActivity.class);
                Register.this.startActivity(activityChangeIntent);
            }
        });

        checkLocationPermission();

        if (checkCredentials()) {
            // check if any session is active...
            checkActiveSession();

            Log.d("Register", "onCreate: " + Properties.user.getUserType());

            // navigate
            Intent intent;
            if (Properties.user.getUserType().equals("client")) {
                intent = new Intent(Register.this, MainActivity.class);
            } else if (Properties.user.getUserType().equals("doctor")) {
                intent = new Intent(Register.this, MainActivityDoctor.class);
            } else {
                return;
            }
            Register.this.startActivity(intent);
            finish();
        } else {
            Log.d("Register", "onCreate: checkCredentials failed!");
        }
    }

    public boolean checkCredentials() {
        File credFile = new File(Properties.credFile);

        if (!credFile.exists()) {
            Log.d("", "checkCredentials: Cred file does not exist");
            return false;
        }

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(credFile));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            Gson gson = new Gson();
            User user = gson.fromJson(text.toString(), User.class);
            Properties.user = user;
            return true;
        } catch (IOException e) {
            Log.d("", "checkCredentials: IOException: " + e.getMessage());
            return false;
        }
    }

    private void checkActiveSession() {
        Properties.retriveSessionFromFile();
        if (Properties.clientDoctorSession == null)
            Properties.clientDoctorSession = new ClientDoctorSession();
            Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession).toString(),Properties.credDir,Properties.activeSessionFile);
    }


    @Override
    public void onClick(View v) {}


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Register.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();



            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;


        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("Location granted","proceed");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE )
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("Storage granted","proceed");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("On Pause location","proceed");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("On Pause storage","proceed");
        }
    }

    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);

            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        checkPermissions();

                        //Request location updates:
                        Log.d("Permission Granted", "Enjoy");
                    }

                } else {


                    new AlertDialog.Builder(this)
                            .setTitle("Sorry")
                            .setMessage("App Wont work normally unless you provide location access")
                            .setPositiveButton("Provide Location Access", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(Register.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            })
                            .setNegativeButton("exit App",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                            .create()
                            .show();
                }
                return;
            }

        }

        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Storage granted","proceed");
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Sorry Please provide access")
                        .setMessage("Not good for your safety")
                        .setPositiveButton("Provide Storage Access", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Register.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        100);
                            }
                        })
                        .setNegativeButton("exit App",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                System.exit(0);
                            }
                        })
                        .create()
                        .show();

            }
            return;
        }
    }

}
