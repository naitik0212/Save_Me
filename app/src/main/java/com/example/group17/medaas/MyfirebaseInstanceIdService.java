package com.example.group17.medaas;


import android.util.Log;

import com.example.group17.medaas.API.model.Token;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;


/**
 * Created by naitikshah on 4/29/18.
 */

public class MyfirebaseInstanceIdService extends FirebaseInstanceIdService {


    private static final String Reg_Token = "REG_TOKEN";
    public void onTokenRefresh(){
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(Reg_Token,recent_token);
    }


}
