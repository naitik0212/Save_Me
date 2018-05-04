package com.example.group17.medaas;

import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.token.TokenPost;
import com.example.group17.medaas.API.token.callback.OnPostTokenResponseSuccess;
import com.example.group17.medaas.API.user.UserPost;
import com.example.group17.medaas.API.user.callback.OnPostUserResponseSuccess;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by naitikshah on 4/16/18.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "RegisterActivity";
    private ComponentName nameUpdateService;

    //Declaring all inputs
    private EditText firstname;
    private EditText lastname;
    private EditText Age;
    private EditText Address;
    private EditText Email;
    private EditText Password;
    private EditText contact;
    private EditText emergencycontact;
    private AppCompatButton appCompatButtonRegister;

    private AppCompatTextView textViewLinkLogin;
    //Location
    Location mLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);
        nameUpdateService = new ComponentName(this, LocationUpdateService.class);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);
        firstname = (EditText) findViewById(R.id.textInputEditTextFirstName);
        lastname = (EditText) findViewById(R.id.textInputEditTextLastName);
        Age = (EditText) findViewById(R.id.textInputEditTextAge);
        Address = (EditText) findViewById(R.id.textInputEditTextAddress);
        Email = (EditText) findViewById(R.id.textInputEditTextEmail);
        Password = (EditText) findViewById(R.id.textInputEditTextPassword);
        contact = (EditText) findViewById(R.id.textInputEditTextPhone);
        emergencycontact = (EditText) findViewById(R.id.textInputEditTextEmergencyContact);
        textViewLinkLogin = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);

        textViewLinkLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(activityChangeIntent);
            }
        });

        //update location
        fetchLocation();

        appCompatButtonRegister.setOnClickListener(new View.OnClickListener() {

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
                                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                                    // error with post request
                                    Log.d(TAG, "afterPostResponseSuccess: Null user returned");
                                } else {
                                    // save to file
                                    Gson gson = new Gson();
                                    String json = gson.toJson(user);
                                    Properties.saveToFile(json, Properties.credDir, Properties.credFile);

                                    Properties.user = user;
                                    Properties.clientDoctorSession = new ClientDoctorSession();
                                    Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession).toString(),Properties.credDir,Properties.activeSessionFile);

                                    // get device token from firebase
                                    String myToken = FirebaseInstanceId.getInstance().getToken();
                                    Log.d(TAG,myToken);

                                    // start location update service
                                    JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                                    LocationUpdateService.scheduleLocationUpdates(user, nameUpdateService, jobScheduler);

                                    // Send token
                                    TokenPost tokenPost = new TokenPost();
                                    tokenPost.request(getApplicationContext(), user.getId(), myToken, new OnPostTokenResponseSuccess() {
                                        @Override
                                        public void afterPostResponseSuccess(JSONObject response) {
                                            // go to main screen
                                            Intent intent = null;
                                            if (Properties.user.getUserType().equals("client")) {
                                                intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            } else if (Properties.user.getUserType().equals("doctor")){
                                                intent = new Intent(RegisterActivity.this, MainActivityDoctor.class);
                                            } else {
                                                return;
                                            }
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            RegisterActivity.this.startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            }
                        });

                }
            }
        });

    }

    @Override
    public void onClick(View v) {
    }

    private void fetchLocation() {
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
