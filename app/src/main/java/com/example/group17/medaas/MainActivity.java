package com.example.group17.medaas;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group17.medaas.API.save.SaveMeGet;
import com.example.group17.medaas.API.save.SaveMePost;
import com.example.group17.medaas.API.save.callback.OnGetSaveMeResponseSuccess;
import com.example.group17.medaas.API.save.callback.OnPostSaveMeResponseSuccess;
import com.example.group17.medaas.API.user.callback.OnPutUserResponseSuccess;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.UserPut;
import com.google.android.gms.common.util.CrashUtils;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView docListTV = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button mapTrigger = (Button) findViewById(R.id.mapTrigger);
        final Button Services = (Button) findViewById(R.id.Services);
        final Button saveMe = (Button) findViewById(R.id.SaveMe);
        final Button cancelSaveMe = (Button) findViewById(R.id.CancelSaveMe);
        docListTV = (TextView) findViewById(R.id.DocList);

        Services.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(MainActivity.this, serviceActivity.class);

                // currentContext.startActivity(activityChangeIntent);

                MainActivity.this.startActivity(activityChangeIntent);
            }
        });

        cancelSaveMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveMePost saveMePost = new SaveMePost();
                saveMePost.requestCancelAsClient(getApplicationContext(), Properties.user.getUserType(), Properties.user.getId(),
                        new OnPostSaveMeResponseSuccess() {
                            @Override
                            public void afterPostResponseSuccess(JSONObject response) {
                                Log.d("", "afterPostResponseSuccess: request cancelled with response: " + response.toString());
                                Toast.makeText(getApplicationContext(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                                docListTV.setText("");
                            }
                        });
            }
        });

        saveMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                if (Properties.user != null) {
                    Long id = Properties.user.getId();

                    SaveMeGet saveMeGet = new SaveMeGet();
                    saveMeGet.request(getApplicationContext(), id,
                            new OnGetSaveMeResponseSuccess() {
                                @Override
                                public void afterGetResponseSuccess(User[] users) {
                                    String docList = "";
                                    for (User user: users) {
                                        docList += user.getFirstName() + " " + user.getPhoneNumber() + "\n";
                                    }
                                    final String docList_ = docList;
                                    docListTV.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            docListTV.setText(docList_);
                                            docListTV.setMovementMethod(new ScrollingMovementMethod());
                                        }
                                    });
                                }
                            });
                }
            }
        });

//        new UserPost().requestLogin(this, "samarth1818@gmail.com", "159357", new OnPostUserResponseSuccess() {
//            public void afterPostResponseSuccess(JSONObject response) {
//            }
//        });
//        new UserPost().requestLogin(this, "samarth1818@gmail.com", "159351", new OnPostUserResponseSuccess() {
//            public void afterPostResponseSuccess(JSONObject response) {
//            }
//        });

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
    @Override
    public void onBackPressed() {
        finish();
    }
}
