package com.example.group17.medaas.API.save;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.save.callback.OnPostSaveMeResponseSuccess;
import com.example.group17.medaas.API.user.callback.OnPostUserResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONException;
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
    private static final String param0 = "clientId";
    private static final String param1 = "doctorId";
    private static final String param2 = "requester";

    public void requestDocResponse(Context ctx, final Long clientId, final Long doctorId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointDocResponse + "?" + param0 + "=" + Long.toString(clientId) + "&" + param1 + "=" + Long.toString(doctorId);

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
        req.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }

    public void requestCancelAsClient(Context ctx, final String requester, final Long clientId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointCancel;

        if (requester.equals("client")) {
            url += "?" + param0 + "=" + Long.toString(clientId) + "&" + param2 + "=" + requester;
        } else {
            Log.e(TAG, "requestCancelAsClient: invalid requester: " + requester);
            return;
        }

        // define request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: Save Me Cancel " + response.toString());
                        postResponse.afterPostResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        error.printStackTrace();
                        JSONObject errorJson = new JSONObject();
                        try {
                            errorJson.put("error",error.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        postResponse.afterPostResponseSuccess(errorJson);
                    }
                });
        req.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }

    public void requestCancelAsDoctor(Context ctx, final String requester, final Long clientId, final Long doctorId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointCancel;

        if (requester.equals("doctor")) {
            url += "?" + param0 + "=" + Long.toString(clientId) + "&" + param1 + "=" + Long.toString(doctorId) + "&" + param2 + "=" + requester;
        } else {
            Log.e(TAG, "requestCancelAsClient: invalid requester: " + requester);
            return;
        }

        // define request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: Save Me Cancel " + response.toString());
                        postResponse.afterPostResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        error.printStackTrace();
                        JSONObject errorJson = new JSONObject();
                        try {
                            errorJson.put("error",error.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        postResponse.afterPostResponseSuccess(errorJson);
                    }
                });
        req.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }

    public void requestCompleteAsClient(Context ctx, final String requester, final Long clientId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointComplete;

        if (requester.equals("client")) {
            url += "?" + param0 + "=" + Long.toString(clientId) + "&" + param2 + "=" + requester;
        } else {
            Log.e(TAG, "requestCancelAsClient: invalid requester: " + requester);
            return;
        }

        // define request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: Save Me Cancel " + response.toString());
                        postResponse.afterPostResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        error.printStackTrace();
                        JSONObject errorJson = new JSONObject();
                        try {
                            errorJson.put("error",error.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        postResponse.afterPostResponseSuccess(errorJson);
                    }
                });
        req.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // schedule request
        MyRequestQueue.getInstance(ctx).addToRequestQueue(req);
    }

    public void requestCompleteAsDoctor(Context ctx, final String requester, final Long clientId, final Long doctorId, final OnPostSaveMeResponseSuccess postResponse) {
        // define url
        String url = "http://" + Properties.ip + "/" + endpointComplete;

        if (requester.equals("doctor")) {
            url += "?" + param0 + "=" + Long.toString(clientId) + "&" + param1 + "=" + Long.toString(doctorId) + "&" + param2 + "=" + requester;
        } else {
            Log.e(TAG, "requestCancelAsClient: invalid requester: " + requester);
            return;
        }

        // define request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: Save Me Cancel " + response.toString());
                        postResponse.afterPostResponseSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        error.printStackTrace();
                        JSONObject errorJson = new JSONObject();
                        try {
                            errorJson.put("error",error.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        postResponse.afterPostResponseSuccess(errorJson);
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
