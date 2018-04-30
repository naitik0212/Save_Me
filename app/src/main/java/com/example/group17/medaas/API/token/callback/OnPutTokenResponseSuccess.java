package com.example.group17.medaas.API.token.callback;

import org.json.JSONObject;

/**
 * Created by Samarth on 4/30/2018.
 */

public interface OnPutTokenResponseSuccess {
    void afterPutResponseSuccess(JSONObject response);
}
