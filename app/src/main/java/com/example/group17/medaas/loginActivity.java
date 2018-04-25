package com.example.group17.medaas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by naitikshah on 4/16/18.
 */

public class loginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = loginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }


    @Override
    public void onClick(View v) {

    }
}

