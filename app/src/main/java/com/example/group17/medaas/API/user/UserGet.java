package com.example.group17.medaas.API.user;


import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.user.callback.OnGetUserResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;

public class UserGet {
    private static final String endpoint = "user";
    private static final String param0 = "userId";


    public void request(Context ctx, Long userId, final OnGetUserResponseSuccess postResponse) {

        // define url
        String url = "http://" + Properties.ip + "/" + endpoint + "?" + param0 + "=" + Long.toString(userId);

        // define request
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        JSONObject userJson = null;
                        User user = null;
                        int tokenId = Properties.TOKEN_ID_NULL;

                        // prepare User object
                        try {
                            userJson = response.getJSONObject(0);
                            user = new User(userJson);
                        } catch(JSONException e) {
                            user = null;
                        }

                        // get token if available
                        if (user != null) {
                            try {
                                Log.d(TAG, "onResponse: " + userJson.toString());
                                JSONObject tokenJson = userJson.getJSONObject("token");
                                Log.d(TAG, "onResponse: " + tokenJson.toString());
                                tokenId = tokenJson.getInt("id");
                            } catch (JSONException e) {
                                Log.d(TAG, "onResponse: tokenId not found!!!");
                                e.printStackTrace();
                                tokenId = Properties.TOKEN_ID_NULL;
                            }
                        }
                        postResponse.afterGetResponseSuccess(user, tokenId);
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
