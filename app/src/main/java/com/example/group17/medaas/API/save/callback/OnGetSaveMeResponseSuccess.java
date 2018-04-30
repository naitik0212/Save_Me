package com.example.group17.medaas.API.save.callback;

import com.example.group17.medaas.API.model.User;

/**
 * Created by Samarth on 4/29/2018.
 */

public interface OnGetSaveMeResponseSuccess {
    void afterGetResponseSuccess(User[] users);
}
