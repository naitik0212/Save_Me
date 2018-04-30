package com.example.group17.medaas.API.save;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.save.callback.OnGetSaveMeResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONArray;
import org.json.JSONException;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Samarth on 4/29/2018.
 */

public class SaveMeGet {
    private static final String endpoint = "saveme";
    private static final String param0 = "userId";


    public void request(Context ctx, int userId, final OnGetSaveMeResponseSuccess postResponse) {

        // define url
        String url = "http://" + Properties.ip + "/" + endpoint + "?" + param0 + "=" + Integer.toString(userId);

        // define request
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        int len = response.length();
                        User[] users = new User[len];
                        for (int i = 0; i < len; i++) {
                            User user = null;
                            try {
                                user = new User(response.getJSONObject(i));
                            } catch(JSONException e) {
                                user = null;
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

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }

}
