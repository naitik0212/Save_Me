package com.example.group17.medaas.API.save;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.save.callback.OnPostSaveMeResponseSuccess;
import com.example.group17.medaas.API.user.callback.OnPostUserResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Samarth on 4/30/2018.
 */

public class SaveMePost {
    private static final String endpointDocResponse = "saveme/docresponse";
    private static final String endpointCancel = "saveme/cancel";
    private static final String endpointComplete = "saveme/complete";

    public void requestDocResponse(Context ctx, final Long clientId, final Long doctorId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointDocResponse;

        // post paramters
        Map<String, String> params = new HashMap<>();
        params.put("doctorId", Long.toString(doctorId));
        params.put("clientId", Long.toString(clientId));

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

    public void requestCancelAsClient(Context ctx, final String requester, final Long clientId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointCancel;

        // post paramters
        Map<String, String> params = new HashMap<>();
        if (requester.equals("client")) {
            params.put("requester", requester);
            params.put("clientId", Long.toString(clientId));
        } else {
            Log.e(TAG, "requestCancelAsClient: invalid requester: " + requester);
            return;
        }

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

    public void requestCancelAsDoctor(Context ctx, final String requester, final Long clientId, final Long doctorId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointCancel;

        // post paramters
        Map<String, String> params = new HashMap<>();
        if (requester.equals("doctor")) {
            params.put("requester", requester);
            params.put("clientId", Long.toString(clientId));
            params.put("doctorId", Long.toString(doctorId));
        } else {
            Log.e(TAG, "requestCancelAsClient: invalid requester: " + requester);
            return;
        }

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

    public void requestCompleteAsClient(Context ctx, final String requester, final Long clientId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointComplete;

        // post paramters
        Map<String, String> params = new HashMap<>();
        if (requester.equals("client")) {
            params.put("requester", requester);
            params.put("clientId", Long.toString(clientId));
        } else {
            Log.e(TAG, "requestCancelAsClient: invalid requester: " + requester);
            return;
        }

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

    public void requestCompleteAsDoctor(Context ctx, final String requester, final Long clientId, final Long doctorId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointComplete;

        // post paramters
        Map<String, String> params = new HashMap<>();
        if (requester.equals("doctor")) {
            params.put("requester", requester);
            params.put("clientId", Long.toString(clientId));
            params.put("doctorId", Long.toString(doctorId));
        } else {
            Log.e(TAG, "requestCancelAsClient: invalid requester: " + requester);
            return;
        }

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
