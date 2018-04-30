package com.example.group17.medaas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group17.medaas.Properties;
/**
 * Created by naitikshah on 4/16/18.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    //Declaring all inputs
    private EditText firstname;
    private EditText lastname;
    private EditText Age;
    private EditText Address;
    private EditText Email;
    private EditText Password;
    private EditText contact;
    private EditText emergencycontact;


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
                final String location;


                //Hide Keyboard
                InputMethodManager hideKeyboard = (InputMethodManager) getSystemService(RegisterActivity.INPUT_METHOD_SERVICE);
                hideKeyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                //Validation check of all parameters if input is correct and entered

                if(firstName.matches("")||lastName.matches("")||age.matches("")||address.matches("")||phoneNumber.matches("")||emergencyNumber.matches("")||userType.matches("")||email.matches("")||password.matches("")) {

                    Toast.makeText(RegisterActivity.this,
                            "Please enter parameters", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Properties.RegistrationParameters param = new Properties.RegistrationParameters(firstName, lastName, age, address, phoneNumber, emergencyNumber, email, password, userType);
                    new RegisterRequest().execute(param);


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
    @Override
    public void onBackPressed() {
        finish();
    }

}
