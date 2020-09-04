package com.aryupay.helpingapp.utils;

import android.content.Context;

import com.aryupay.helpingapp.modal.login.LoginModel;


public class PrefUtils {

    public static LoginModel getUser(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        LoginModel currentUser = complexPreferences.getObject("status", LoginModel.class);
        return currentUser;
    }

    public static void setUser(LoginModel currentUser, Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        complexPreferences.putObject("status", currentUser);
        complexPreferences.commit();
    }


    public static void clearCurrentUser(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }

}
