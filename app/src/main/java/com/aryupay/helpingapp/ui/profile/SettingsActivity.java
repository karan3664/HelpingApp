package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.profile.followers.FollowersModel;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.ui.profile.ui.FollowingFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.facebook.login.Login;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llPrivacy, llLogout, llDeactivate, llNotification;
    ImageView ivClose;
    LoginModel loginModel;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();
        llPrivacy = findViewById(R.id.llPrivacy);
        llNotification = findViewById(R.id.llNotification);
        llLogout = findViewById(R.id.llLogout);
        llDeactivate = findViewById(R.id.llDeactivate);
        ivClose = findViewById(R.id.ivClose);

        ivClose.setOnClickListener(this);
        llPrivacy.setOnClickListener(this);
        llNotification.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llDeactivate.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llPrivacy:
                Intent changePassword = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(changePassword);
                break;
            case R.id.llNotification:
                Intent noti = new Intent(SettingsActivity.this, NotificationsActivity.class);
                startActivity(noti);
                break;
            case R.id.llLogout:
                LogoutCall();
                break;
            case R.id.llDeactivate:
                DeactivateAccount();
                break;
            case R.id.ivClose:
                onBackPressed();
                break;
        }
    }

    public void LogoutCall() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You want to Logout");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).logout("Bearer " + token);
                marqueCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        JsonObject object = response.body();

                        Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                        if (response.isSuccessful()) {

                            PrefUtils.clearCurrentUser(SettingsActivity.this);
                            Intent log = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(log);
                            finish();


                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(SettingsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        t.printStackTrace();

                        Log.e("ChatV_Response", t.getMessage() + "");
                    }
                });
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void DeactivateAccount() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You want to Deactivate your account");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).deactivate("Bearer " + token);
                marqueCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        JsonObject object = response.body();

                        Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                        if (response.isSuccessful()) {

                            PrefUtils.clearCurrentUser(SettingsActivity.this);
                            Intent log = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(log);
                            finish();


                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(SettingsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        t.printStackTrace();

                        Log.e("ChatV_Response", t.getMessage() + "");
                    }
                });
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}