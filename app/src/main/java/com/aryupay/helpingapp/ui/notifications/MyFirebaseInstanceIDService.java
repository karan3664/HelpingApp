package com.aryupay.helpingapp.ui.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

     //   storeToken(refreshedToken);

    }

 /*   private void storeToken(String token) {
        SharedData.getInstance(getApplicationContext()).storeToken(token);
        Log.d("token_store",""+token);
    }*/

}