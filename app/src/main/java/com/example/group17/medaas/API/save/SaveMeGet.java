package com.example.group17.medaas.API.save;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.save.callback.OnGetSaveMeResponseSuccess;
import com.example.group17.medaas.Properties;
import com.google.android.gms.common.util.CrashUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Samarth on 4/29/2018.
 */

public class SaveMeGet {
    private static final String endpoint = "saveme";
    private static final String param0 = "userId";


    public void request(Context ctx, Long userId, final OnGetSaveMeResponseSuccess postResponse) {

        // define url
        String url = "http://" + Properties.ip + "/" + endpoint + "?" + param0 + "=" + Long.toString(userId);

        // define request
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        int len = response.length();
                        Log.d(TAG, "onResponse: " + Integer.toString(len) + " doctors found: " + response.toString());
                        User[] users = new User[len];
                        for (int i = 0; i < len; i++) {
                            User user = null;
                            try {
                                user = new User(response.getJSONObject(i));
                            } catch(JSONException e) {
                                user = null;
                                e.printStackTrace();
                            } finally {
                                users[i] = user;
                            }
                        }
                        postResponse.afterGetResponseSuccess(users);

                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: " + "get-request error");
                        error.printStackTrace();
                    }
                });

        req.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }
}
