package com.example.group17.medaas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.UserPut;
import com.example.group17.medaas.API.user.callback.OnPutUserResponseSuccess;
import com.google.android.gms.common.util.CrashUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONObject;

public class LocationUpdateService extends JobService {
    private static final String TAG = "LocationUpdateService";

    @Override
    public boolean onStartJob(final JobParameters params) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                // prepare location string
                                String sLocation = Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude());

                                String userJson = params.getExtras().getString("user");
                                Gson gson = new Gson();
                                User newUser = gson.fromJson(userJson, User.class);

                                Log.d(TAG, "onSuccess: new location " + sLocation + " for " + newUser.getFirstName());

                                newUser.setLocation(sLocation);

                                // update user location details in the backend database
                                new UserPut().request(getApplicationContext(), newUser,
                                        new OnPutUserResponseSuccess() {
                                            @Override
                                            public void afterPostResponseSuccess(JSONObject response) {
                                                Log.d(TAG, "afterPostResponseSuccess: " + response.toString());
                                                jobFinished(params, true);
                                            }
                                        });

                            } else {
                                Log.e(TAG, "No updates possible: Location is NULL");
                            }
                        }
                    });
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}