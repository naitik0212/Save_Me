package com.example.group17.medaas;

import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.token.TokenPost;
import com.example.group17.medaas.API.token.TokenPut;
import com.example.group17.medaas.API.token.callback.OnPostTokenResponseSuccess;
import com.example.group17.medaas.API.token.callback.OnPutTokenResponseSuccess;
import com.example.group17.medaas.API.user.UserGet;
import com.example.group17.medaas.API.user.UserPost;
import com.example.group17.medaas.API.user.callback.OnGetUserResponseSuccess;
import com.example.group17.medaas.API.user.callback.OnPostUserLoginResponseSuccess;
import com.example.group17.medaas.helpers.InputValidation;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by naitikshah on 4/16/18.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ComponentName nameUpdateService;

    private final AppCompatActivity activity = LoginActivity.this;
    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private InputValidation inputValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        nameUpdateService = new ComponentName(this, LocationUpdateService.class);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);

        textViewLinkRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(activityChangeIntent);
            }
        });

        appCompatButtonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = textInputEditTextEmail.getText().toString().trim();
                String password = textInputEditTextPassword.getText().toString().trim();

                UserPost userPost = new UserPost();
                userPost.requestLogin(getApplicationContext(), email, password,
                        new OnPostUserLoginResponseSuccess() {
                            @Override
                            public void afterPostResponseSuccess(User user) {
                                if (user == null) {
                                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
                                    // error with post request
                                    Log.d("", "afterPostResponseSuccess: Null user returned");

                                    return;
                                }

                                // save to file
                                Gson gson = new Gson();
                                String json = gson.toJson(user);
                                Properties.saveToFile(json, Properties.credDir, Properties.credFile);

                                Properties.user = user;
                                Properties.clientDoctorSession = new ClientDoctorSession();
                                Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession).toString(),Properties.credDir,Properties.activeSessionFile);

                                // get device token from firebase
                                final String myToken = FirebaseInstanceId.getInstance().getToken();
                                Log.d("token: ",myToken);

                                // start location update service
                                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                                LocationUpdateService.scheduleLocationUpdates(user, nameUpdateService, jobScheduler);

                                // get user's tokenId from database
                                UserGet userGet = new UserGet();
                                Log.d("", "afterPostResponseSuccess: requesting token id from server...");
                                userGet.request(getApplicationContext(), user.getId(),
                                        new OnGetUserResponseSuccess() {
                                            @Override
                                            public void afterGetResponseSuccess(User user, int tokenId) {
                                                if (tokenId == Properties.TOKEN_ID_NULL) {
                                                    // POST Token request
                                                    TokenPost tokenPost = new TokenPost();
                                                    tokenPost.request(getApplicationContext(), user.getId(), myToken, new OnPostTokenResponseSuccess() {
                                                        @Override
                                                        public void afterPostResponseSuccess(JSONObject response) {
                                                            Log.d("", "afterPutResponseSuccess: " + response);
                                                            // go to main screen
                                                            Intent intent = null;
                                                            if (Properties.user.getUserType().equals("client")) {
                                                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            } else if (Properties.user.getUserType().equals("doctor")){
                                                                intent = new Intent(LoginActivity.this, MainActivityDoctor.class);
                                                            } else {
                                                                return;
                                                            }
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            LoginActivity.this.startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    // PUT Token request
                                                    TokenPut tokenPut = new TokenPut();
                                                    tokenPut.request(getApplicationContext(), user.getId(), tokenId, myToken,
                                                            new OnPutTokenResponseSuccess() {
                                                        @Override
                                                        public void afterPutResponseSuccess(JSONObject response) {
                                                            Log.d("", "afterPutResponseSuccess: " + response);
                                                            // go to main screen
                                                            Intent intent = null;
                                                            if (Properties.user.getUserType().equals("client")) {
                                                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            } else if (Properties.user.getUserType().equals("doctor")){
                                                                intent = new Intent(LoginActivity.this, MainActivityDoctor.class);
                                                            }
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            LoginActivity.this.startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
