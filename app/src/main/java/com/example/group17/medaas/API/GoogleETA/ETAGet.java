package com.example.group17.medaas.API.GoogleETA;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.group17.medaas.API.GoogleETA.callback.OnGetETAResponseSuccess;
import com.example.group17.medaas.API.model.MyRequestQueue;
import com.example.group17.medaas.API.model.User;
import com.example.group17.medaas.API.save.callback.OnGetSaveMeResponseSuccess;
import com.example.group17.medaas.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Samarth on 5/3/2018.
 */

public class ETAGet {
    public void request(Context ctx, String originLocation, String destinationLocation, final OnGetETAResponseSuccess postResponse) {

        // define url
        String origin = originLocation.split(" ")[0] + "," + originLocation.split(" ")[1];
        String dest = destinationLocation.split(" ")[0] + "," + destinationLocation.split(" ")[1];

        String url = " https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "" +
                "&destination=" + dest +"&mode=driving&key=AIzaSyBMbXYDBkrp8ci94dBhZO4HRqntfa3ut2M";

        // define request
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        public void onResponse(JSONObject response) {
                            JSONArray locationJson = null;
                            int ETA = Properties.ETA_NULL;
                            try {
                                locationJson = (JSONArray) response.getJSONArray("routes");
                                JSONArray legs = locationJson.getJSONObject(0).getJSONArray("legs");
                                JSONObject duration = legs.getJSONObject(0).getJSONObject("duration");
                                ETA = duration.getInt("value");
                            } catch(JSONException e) {
                                Log.d("ETA error", "could not extract ETA");
                                ETA = Properties.ETA_NULL;
                            }
                            postResponse.afterGetResponseSuccess(ETA);
                        }
                    },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "onErrorResponse: " + "get-request error");
                            error.printStackTrace();
                            postResponse.afterGetResponseSuccess(Properties.ETA_NULL);
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
