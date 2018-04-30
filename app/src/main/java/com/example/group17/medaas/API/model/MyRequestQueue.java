package com.example.group17.medaas.API.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyRequestQueue {
    private static MyRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private MyRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}