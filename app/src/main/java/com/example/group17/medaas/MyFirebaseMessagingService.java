package com.example.group17.medaas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.UserGet;
import com.example.group17.medaas.API.user.callback.OnGetUserResponseSuccess;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by naitikshah on 4/29/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String DOCTOR_RESPONSE = "Hang in there. Take help from people around till then.";
    private static String CLIENT_REQUEST = "Save me doctor!";
    private static String CLIENT_CANCELLED = "Patient has cancelled the request";
    private static String DOCTOR_CANCELLED = "Doctor seems to be busy, other doctors notified";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        Log.d("body: ", "onMessageReceived: " + remoteMessage.getNotification().getBody());
        Log.d("click action: ", "onMessageReceived: " + remoteMessage.getNotification().getClickAction());
        Map<String, String> map= remoteMessage.getData();
        JSONObject json = new JSONObject(map);
        Log.d("json", "onMessageReceived: " + json.toString());

        final String messageBody = remoteMessage.getNotification().getBody();
        Long id = Long.parseLong(map.get("id"));

        Properties.retriveSessionFromFile();
        Properties.retriveUserFromFile();

        if (messageBody.equals(CLIENT_REQUEST)) {
            if (Properties.user == null || !Properties.user.getUserType().equals("doctor") || Properties.clientDoctorSession == null || !Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_READY)) return;
            // request client details
            new UserGet().request(getApplicationContext(), id,
                    new OnGetUserResponseSuccess() {
                        @Override
                        public void afterGetResponseSuccess(User user, int tokenId) {
                            // start client doctor session
                            Properties.clientDoctorSession = new ClientDoctorSession();
                            Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_CLIENT_REQUESTED);
                            Properties.clientDoctorSession.setClientUser(user);
                            Properties.clientDoctorSession.setDoctorUser(Properties.user);
                            Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);


                            // notification handling
                            Intent intent = new Intent(getApplicationContext(), MainActivityDoctor.class);
                            createNotification(intent, messageBody);
                        }
                    }
            );
        } else if (messageBody.equals(DOCTOR_RESPONSE)) {
            if (Properties.user == null || Properties.clientDoctorSession == null ||
                    (Properties.user.getUserType().equals("client") && !Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_CLIENT_REQUESTED)) ||
                    (Properties.user.getUserType().equals("doctor") && !Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_CLIENT_REQUESTED_OUTGOING))
                    ) return;

            // request doctor's details
            new UserGet().request(getApplicationContext(), id,
                    new OnGetUserResponseSuccess() {
                        @Override
                        public void afterGetResponseSuccess(User user, int tokenId) {
                            // start client doctor session
                                Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_DOCTOR_RESPONDED);
                            Properties.clientDoctorSession.setClientUser(Properties.user);
                            Properties.clientDoctorSession.setDoctorUser(user);
                            Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);

                            // notification handling
                            Intent intent = null;
                            if (Properties.user.getUserType().equals("client")) {
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                            } else if (Properties.user.getUserType().equals("doctor")) {
                                intent = new Intent(getApplicationContext(), MainActivityDoctor.class);
                            } else {
                                return;
                            }
                            createNotification(intent, messageBody);
                        }
                    }
            );

        } else if (messageBody.equals(CLIENT_CANCELLED)) {
            if (Properties.user == null || !Properties.user.getUserType().equals("doctor") || Properties.clientDoctorSession == null || Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_READY)) return;

            // start client doctor session
            Properties.clientDoctorSession = new ClientDoctorSession();
            Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);

            // notification handling
            Intent intent = null;
            if (Properties.user.getUserType().equals("client")) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            } else if (Properties.user.getUserType().equals("doctor")){
                intent = new Intent(getApplicationContext(), MainActivityDoctor.class);
            } else {
                return;
            }
            createNotification(intent, messageBody);

        } else if (messageBody.equals(DOCTOR_CANCELLED)) {
            if (Properties.user == null || Properties.clientDoctorSession == null || Properties.clientDoctorSession.getStatus().equals(ClientDoctorSession.STATUS_READY)) return;

            // update client doctor session
            Properties.clientDoctorSession.setStatus(ClientDoctorSession.STATUS_DOCTOR_CANCELLED);
            Properties.saveToFile(new Gson().toJson(Properties.clientDoctorSession), Properties.credDir, Properties.activeSessionFile);

            // notification handling
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            createNotification(intent, messageBody);
        }
    }

    private void createNotification(Intent intent, String messageBody) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("SaveMe: ");
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.save_me_logo);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }
}
