package com.example.group17.medaas;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by naitikshah on 4/29/18.
 */

public class MyfirebaseInstanceIdService extends FirebaseInstanceIdService {


    private static final String Reg_Token = "REG_TOKEN";
    public void onTokenRefresh(){
        String recent_token = FirebaseInstanceId.getInstance().getToken();


        Log.e(Reg_Token,recent_token);
        Log.w(Reg_Token,recent_token);
        Log.i(Reg_Token,recent_token);
        Log.d(Reg_Token,recent_token);
        Log.v(Reg_Token,recent_token);
        Log.d(Reg_Token,recent_token);
        Log.v(Reg_Token,recent_token);

        Log.d("Reg_Token", recent_token);

    }


}
