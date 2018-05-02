package com.example.group17.medaas.API.user;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.callback.OnPutUserResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class UserPut {
    private static final String endpoint = "user";
    private User newUser;


    public void request(Context ctx, final User newUser, final OnPutUserResponseSuccess postResponse) {
        this.newUser = newUser;

        // define url
        String url = "http://" + Properties.ip + "/" + endpoint;

        // define post parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", Long.toString(newUser.getId()));
        params.put("location", newUser.getLocation());

        // define request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(params),
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
                        postResponse.afterPostResponseSuccess(null);
                    }
                });

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }
}
