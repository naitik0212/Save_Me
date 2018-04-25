package com.example.group17.medaas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * Created by naitikshah on 4/19/18.
 */

public class Register extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        final Button Register = (Button) findViewById(R.id.Register);

        Register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(Register.this, RegisterActivity.class);

                // currentContext.startActivity(activityChangeIntent);

                Register.this.startActivity(activityChangeIntent);
            }
        });

        if (checkCredentials()) {
            Intent intent = new Intent(Register.this, MainActivity.class);
            Register.this.startActivity(intent);
            finish();
        }
    }

    public boolean checkCredentials() {
        File credFile = new File(Properties.credFile);
        return credFile.exists();
    }

    @Override
    public void onClick(View v) {


    }


}
