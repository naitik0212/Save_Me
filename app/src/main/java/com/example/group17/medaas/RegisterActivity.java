package com.example.group17.medaas;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.token.TokenPost;
import com.example.group17.medaas.API.token.callback.OnPostTokenResponseSuccess;
import com.example.group17.medaas.API.user.UserPost;
import com.example.group17.medaas.API.user.UserPut;
import com.example.group17.medaas.API.user.callback.OnPostUserResponseSuccess;
import com.example.group17.medaas.API.user.callback.OnPutUserResponseSuccess;
import com.example.group17.medaas.Properties;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by naitikshah on 4/16/18.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "RegisterActivity";

    //Declaring all inputs
    private EditText firstname;
    private EditText lastname;
    private EditText Age;
    private EditText Address;
    private EditText Email;
    private EditText Password;
    private EditText contact;
    private EditText emergencycontact;

    //Location
    Location mLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        //finding in view
        final Button submitButton = (Button) findViewById(R.id.submit_button);
        firstname = (EditText) findViewById(R.id.firstnameText);
        lastname = (EditText) findViewById(R.id.lastnametext);
        Age = (EditText) findViewById(R.id.agetext);
        Address = (EditText) findViewById(R.id.addresstext);
        Email = (EditText) findViewById(R.id.loginText);
        Password = (EditText) findViewById(R.id.password_input);
        contact = (EditText) findViewById(R.id.phonenumber);
        emergencycontact = (EditText) findViewById(R.id.emergencyphonenumber);

        //update location
        fetchLocation();

        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                RadioGroup profession = (RadioGroup) findViewById(R.id.radioGroup);
                RadioButton button = (RadioButton) profession.findViewById(profession.getCheckedRadioButtonId());

                final String firstName = firstname.getText().toString();
                final String lastName = lastname.getText().toString();
                final String age = Age.getText().toString();
                final String address = Address.getText().toString();
                final String phoneNumber = contact.getText().toString();
                final String emergencyNumber = emergencycontact.getText().toString();
                final String email = Email.getText().toString();
                final String password = Password.getText().toString();
                final String userType = button.getText().toString().toLowerCase();


                //to write code
                String sLocation = "0.0 0.0";
                if (mLocation != null) {
                    sLocation = Double.toString(mLocation.getLatitude()) + " " + Double.toString(mLocation.getLongitude());
                }


                //Hide Keyboard
                InputMethodManager hideKeyboard = (InputMethodManager) getSystemService(RegisterActivity.INPUT_METHOD_SERVICE);
                hideKeyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                //Validation check of all parameters if input is correct and entered

                if (firstName.matches("") || lastName.matches("") || age.matches("") || address.matches("") || phoneNumber.matches("") || emergencyNumber.matches("") || userType.matches("") || email.matches("") || password.matches("")) {

                    Toast.makeText(RegisterActivity.this,
                            "Please enter parameters", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                        User user = new User();
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setAge(Integer.parseInt(age));
                        user.setEmail(email);
                        user.setPhoneNumber(phoneNumber);
                        user.setEmergencyNumber(emergencyNumber);
                        user.setAddress(address);
                        user.setLocation(sLocation);
                        user.setUserType(userType);
                        user.setPassword(password);

                        UserPost userPost = new UserPost();
                        userPost.request(getApplicationContext(), user, new OnPostUserResponseSuccess() {
                            @Override
                            public void afterPostResponseSuccess(User user) {
                                if (user == null) {
                                    // error with post request
                                    Log.d(TAG, "afterPostResponseSuccess: Null user returned");
                                } else {
                                    Log.d(TAG, "afterPostResponseSuccess: " + user.getFirstName() + " " +user.getLastName() + " " + Long.toString(user.getId()));
                                    Toast.makeText(getApplicationContext(), user.getFirstName() + " " +user.getLastName() + " " + Long.toString(user.getId()), Toast.LENGTH_LONG).show();

                                    // save to file
                                    Gson gson = new Gson();
                                    String json = gson.toJson(user);
                                    saveToFile(json);

                                    Properties.user = user;

                                    // get device token from firebase
                                    String myToken = FirebaseInstanceId.getInstance().getToken();
                                    Log.d(TAG,myToken);

                                    TokenPost tokenPost = new TokenPost();
                                    tokenPost.request(getApplicationContext(), user.getId(), myToken, new OnPostTokenResponseSuccess() {
                                        @Override
                                        public void afterPostResponseSuccess(JSONObject response) {
                                            // go to main screen
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            RegisterActivity.this.startActivity(intent);
                                            finish();
                                        }
                                    });


                                }
                            }
                        });

//                    Properties.RegistrationParameters param = new Properties.RegistrationParameters(firstName, lastName, age, address, phoneNumber, emergencyNumber, email, password, userType);

                    //                    new RegisterRequest().execute(param);


                    //validation check
//                Toast.makeText(RegisterActivity.this,
//                        i + " and " + k+ " and " + s, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    private void fetchLocation() {
        Location _mLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permissions required!", Toast.LENGTH_LONG).show();
        }

        LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLocation = location;
                        } else {
                            Log.e(TAG, "Location is NULL");
                            mLocation = null;
                        }
                    }
                });
    }

    class RegisterRequest extends AsyncTask<User, Void, Void> {
        private boolean registered = false;

        @Override
        protected Void doInBackground(User... params) {

            if(registered) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (registered) {
            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    private void saveToFile(String json) {
        // create parameter string to write
        String fcontent = json;

        // save parameters to a file locally
        File directory = new File(Properties.credDir);
        File file = new File(Properties.credFile);
        if (!directory.exists()) {
            Log.i("", "saveParameters: directory created" + directory.exists());

            directory.mkdirs();
            Log.i("", "saveParameters: directory created" + directory.exists());
        }

        // If file does not exist, then create it
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fcontent);
            bw.close();
            Log.d("Success", "Success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean scheduleLocationUpdates(User user) {
        final ComponentName name = new ComponentName(this, LocationUpdateService.class);

        // create user bundle
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("user", userJson);

        // create JobInfo Object
        final JobInfo jobInfo = new JobInfo.Builder(123, name)
                .setMinimumLatency(1000)
                .setOverrideDeadline(3000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setExtras(bundle)
                .build();

        // schedule
        final JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result;
        if (jobScheduler != null) {
            result = jobScheduler.schedule(jobInfo);
        } else {
            Log.d("", "scheduleLocationUpdates: Job Scheduler Null");
            return false;
        }

        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d("", "scheduleLocationUpdates: Scheduled Job Successfully");
            return true;
        } else {
            Log.d("", "scheduleLocationUpdates: Job Scheduling failed");
            return false;
        }
    }

    public void stopLocationUpdates() {
        final JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
        Log.d("", "stopLocationUpdates: All Jobs stopped");
    }
}
