package com.example.group17.medaas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group17.medaas.Properties;
/**
 * Created by naitikshah on 4/16/18.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText name;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        final Button submitButton = (Button) findViewById(R.id.submit_button);

        name = (EditText) findViewById(R.id.nameText);
        email = (EditText) findViewById(R.id.loginText);
        password = (EditText) findViewById(R.id.password_input);


        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final String i = name.getText().toString();
                final String k = email.getText().toString();
                final String s = password.getText().toString();

                Properties.RegistrationParameters param = new Properties.RegistrationParameters(i, k, s, false);
                new RegisterRequest().execute(param);

                //validation check
//                Toast.makeText(RegisterActivity.this,
//                        i + " and " + k+ " and " + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    class RegisterRequest extends AsyncTask<Properties.RegistrationParameters, Void, Void> {
        private boolean registered = false;

        @Override
        protected Void doInBackground(Properties.RegistrationParameters... params) {
            //submit a user/doctor registration request to the server and wait for Response OK

            // 1. submit request using api-endpoint
            // If Response error, set registered = false

            // 2. On ResponseOK...
            registered = true;


            // save parameters locally
            if(registered) {
                params[0].saveParameters();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (registered) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                RegisterActivity.this.startActivity(intent);
                finish();
            }
        }
    }

}
