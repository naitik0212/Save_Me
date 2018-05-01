package com.example.group17.medaas.API.token;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.token.callback.OnPostTokenResponseSuccess;
import com.example.group17.medaas.API.user.callback.OnPostUserResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Samarth on 4/30/2018.
 */

public class TokenPost {
    private static final String endpoint = "token";
    private static final String param0 = "userId";

    public void request(Context ctx, final Long userId, final String deviceToken, final OnPostTokenResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpoint + "?" + param0 + "=" + Long.toString(userId);

        // define post parameters
        Map<String, String> params = new HashMap<>();
        params.put("deviceToken", deviceToken);

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
}
