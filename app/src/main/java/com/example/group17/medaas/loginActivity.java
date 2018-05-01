package com.example.group17.medaas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.group17.medaas.API.user.UserPost;
import com.example.group17.medaas.API.user.callback.OnPostUserLoginResponseSuccess;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by naitikshah on 4/16/18.
 */

public class loginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = loginActivity.this;
    private EditText email_et = null;
    private EditText password_et = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final Button Register = (Button) findViewById(R.id.Register);
        final Button Login = (Button) findViewById(R.id.submit_button);
        email_et = (EditText) findViewById(R.id.loginText);
        password_et = (EditText) findViewById(R.id.password_input);

        Register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(loginActivity.this, RegisterActivity.class);

                loginActivity.this.startActivity(activityChangeIntent);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = email_et.getText().toString();
                String password = password_et.getText().toString();

                UserPost userPost = new UserPost();
                userPost.requestLogin(getApplicationContext(), email, password,
                        new OnPostUserLoginResponseSuccess() {
                            @Override
                            public void afterPostResponseSuccess(JSONObject response) {
                                String loginFailed = "Login Failed";
                                String loginSuccess = "Login Success";
                                try {
                                    if (response.getString("status").equals(loginSuccess)) {
                                        // login success. proceed.

                                    } else {
                                        Log.d("", "afterPostResponseSuccess: login failed");
                                        Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                // Perform action on click
//                Intent activityChangeIntent = new Intent(Register.this, loginActivity.class);

                // currentContext.startActivity(activityChangeIntent);

            }
        });


    }


    @Override
    public void onClick(View v) {

    }
}

