package com.example.group17.medaas.API.token;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.token.callback.OnGetTokenResponseSuccess;
import com.example.group17.medaas.API.token.callback.OnPutTokenResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Samarth on 4/30/2018.
 */

public class TokenGet {
    private static final String endpoint = "token";
    private static final String param0 = "userId";


    public void request(Context ctx, int userId, final OnGetTokenResponseSuccess postResponse) {

        // define url
        String url = "http://" + Properties.ip + "/" + endpoint + "?" + param0 + "=" + Integer.toString(userId);

        // define request
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        postResponse.afterGetResponseSuccess(response);
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
