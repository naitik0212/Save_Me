package com.example.group17.medaas;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.group17.medaas.API.user.callback.OnPutUserResponseSuccess;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.UserPut;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button mapTrigger = (Button) findViewById(R.id.mapTrigger);

//        scheduleLocationUpdates();



//        new UserPost().requestLogin(this, "samarth1818@gmail.com", "159357", new OnPostUserResponseSuccess() {
//            public void afterPostResponseSuccess(JSONObject response) {
//            }
//        });
//        new UserPost().requestLogin(this, "samarth1818@gmail.com", "159351", new OnPostUserResponseSuccess() {
//            public void afterPostResponseSuccess(JSONObject response) {
//            }
//        });

        User user = new User();
        user.setId(33L);
        user.setLocation("33.4235981 -111.9395366");
        new UserPut().request(this, user, new OnPutUserResponseSuccess() {
            @Override
            public void afterPostResponseSuccess(JSONObject response) {

            }
        });

//        User user = new User();
//        user.setFirstName("Samarth");
//        user.setLastName("Shah");
//        user.setAge(24);
//        user.setEmail("samarth1818@gmail.com");
//        user.setPhoneNumber("425-589-8872");
//        user.setEmergencyNumber("425-589-8872");
//        user.setAddress("1500 E Broadway Rd, Tempe, AZ 85282");
//        user.setLocation("0.0 0.0");
//        user.setUserType("client");
//        user.setPassword("159357");
//
//        UserPost userPost = new UserPost();
//        userPost.request(this, user, new OnPostUserResponseSuccess() {
//            @Override
//            public void afterPostResponseSuccess(JSONObject response) {
//            }
//        });

        mapTrigger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });


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
