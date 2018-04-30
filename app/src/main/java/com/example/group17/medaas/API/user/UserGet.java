package com.example.group17.medaas.API.user;


import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.callback.OnGetUserResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONArray;
import org.json.JSONException;

import static com.android.volley.VolleyLog.TAG;

public class UserGet {
    private static final String endpoint = "user";
    private static final String param0 = "userId";


    public void request(Context ctx, int userId, final OnGetUserResponseSuccess postResponse) {

        // define url
        String url = "http://" + Properties.ip + "/" + endpoint + "?" + param0 + "=" + Integer.toString(userId);

        // define request
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        User user = null;
                        try {
                            user = new User(response.getJSONObject(0));
                        } catch(JSONException e) {
                            user = null;
                        } finally {
                            postResponse.afterGetResponseSuccess(user);
                        }
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: " + "get-request error");
                        error.printStackTrace();
                    }
                });

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }
}
