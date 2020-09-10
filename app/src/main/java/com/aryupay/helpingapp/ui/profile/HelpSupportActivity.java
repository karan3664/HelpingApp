package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.Tools;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpSupportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivClose;
    LinearLayout llHelpCenter, llRateUs, llInvite, llDeactivate;
    LoginModel loginModel;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();
        llHelpCenter = findViewById(R.id.llHelpCenter);
        llRateUs = findViewById(R.id.llRateUs);
        llInvite = findViewById(R.id.llInvite);
        llDeactivate = findViewById(R.id.llDeactivate);
        ivClose = findViewById(R.id.ivClose);

        ivClose.setOnClickListener(this);
        llHelpCenter.setOnClickListener(this);
        llRateUs.setOnClickListener(this);
        llInvite.setOnClickListener(this);
        llDeactivate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llHelpCenter:
                Intent i = new Intent(HelpSupportActivity.this, HelpAndSupportActivity.class);
                startActivity(i);
                break;
            case R.id.llRateUs:
                Tools.rateAction(HelpSupportActivity.this);
                break;
            case R.id.llInvite:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + this.getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.llDeactivate:
                DeactivateAccount();
                break;

            case R.id.ivClose:
                onBackPressed();
                break;
        }
    }

    public void DeactivateAccount() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HelpSupportActivity.this, R.style.AlertDialogTheme);
        alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
        alertDialogBuilder.setIcon(R.drawable.eyu);
        alertDialogBuilder
                .setMessage("Are you sure, You want to Deactivate your account ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).deactivate("Bearer " + token);
                        marqueCall.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                JsonObject object = response.body();

                                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                if (response.isSuccessful()) {

                                    PrefUtils.clearCurrentUser(HelpSupportActivity.this);
                                    Intent log = new Intent(HelpSupportActivity.this, LoginActivity.class);
                                    startActivity(log);
                                    finish();


                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(HelpSupportActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(HelpSupportActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }
}