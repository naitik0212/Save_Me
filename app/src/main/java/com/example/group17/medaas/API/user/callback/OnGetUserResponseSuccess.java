package com.example.group17.medaas.API.user.callback;


import com.example.group17.medaas.API.model.User;

public interface OnGetUserResponseSuccess {
    void afterGetResponseSuccess(User user, int tokenId);
}
