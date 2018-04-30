package com.example.group17.medaas.API.user;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.callback.OnPostUserResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class UserPost {
    private static final String endpoint = "user";
    private static final String endpointLogin = "user/login";
    private User newUser;


    public void request(Context ctx, final User newUser, final OnPostUserResponseSuccess postResponse) {
        this.newUser = newUser;

        // define url
        String url = "http://" + Properties.ip + "/" + endpoint;

        // define post parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("firstName", newUser.getFirstName());
        params.put("lastName", newUser.getLastName());
        params.put("age", Integer.toString(newUser.getAge()));
        params.put("address", newUser.getAddress());
        params.put("phoneNumber", newUser.getPhoneNumber());
        params.put("emergencyNumber", newUser.getEmergencyNumber());
        params.put("userType", newUser.getUserType());
        params.put("location", newUser.getLocation());
        params.put("email", newUser.getEmail());
        params.put("password", newUser.getPassword());


        // define request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        postResponse.afterPostResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        error.printStackTrace();
                    }
                });

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }

    public void requestLogin(Context ctx, String email, String password, final OnPostUserResponseSuccess postResponse) {

        // define url
        String url = "http://" + Properties.ip + "/" + endpointLogin;

        // define login hash map
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("email", email);
        loginParams.put("password", password);

        // define request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(loginParams),
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response.toString());

                        postResponse.afterPostResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        error.printStackTrace();
                    }
                });

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }
}
