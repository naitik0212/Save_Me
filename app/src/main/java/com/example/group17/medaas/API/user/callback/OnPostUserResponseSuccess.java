package com.example.group17.medaas.API.user.callback;

import org.json.JSONObject;

/**
 * Created by Samarth on 4/29/2018.
 */

public interface OnPostUserResponseSuccess {
    void afterPostResponseSuccess(JSONObject response);
}
