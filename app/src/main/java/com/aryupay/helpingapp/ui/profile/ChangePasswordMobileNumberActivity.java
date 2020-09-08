package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.changePassword.otp.OTPModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.phonesignup.PhoneSignupModel;
import com.aryupay.helpingapp.ui.HomeActivity;

import com.aryupay.helpingapp.ui.RegisterActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordMobileNumberActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_mobno;
    OtpView otp_view;
    Button btnResend, btnVerifyOTP, btn_sumbitOtp;
    LoginModel loginModel;
    String otp, mobile, name, password, token;
    Boolean one = false;
    protected ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_mobile_number);

        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);

        et_mobno = findViewById(R.id.et_mobno);
        otp_view = findViewById(R.id.otp_view);
        btnResend = findViewById(R.id.btnResend);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        btn_sumbitOtp = findViewById(R.id.btn_sumbitOtp);

        btnResend.setOnClickListener(this);
        btnVerifyOTP.setOnClickListener(this);
        btn_sumbitOtp.setOnClickListener(this);

        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp_s) {
                if (otp_s.matches(otp)) {
                    Toast.makeText(ChangePasswordMobileNumberActivity.this, "OTP Verified...", Toast.LENGTH_SHORT).show();
                    btnVerifyOTP.setEnabled(true);
                } else {
                    Toast.makeText(ChangePasswordMobileNumberActivity.this, "OTP Invalid!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sumbitOtp:
                GetOTP();
                break;
            case R.id.btnResend:
                GetOTP();
                break;
            case R.id.btnVerifyOTP:
                VerifyOtpCall();
                break;
        }
    }


    public void VerifyOtpCall() {
        Call<JsonObject> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).verifyotp("Bearer " + token);
        postCodeModelCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                final JsonObject object = response.body();


                if (object != null) {

                    if (response.isSuccessful()) {
                        Intent regIntent = new Intent(ChangePasswordMobileNumberActivity.this, ChangePasswordActivity.class);
                        regIntent.putExtra("mobile", et_mobno.getText().toString() + "");
                        startActivity(regIntent);
                    } else {

                    }


                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                t.printStackTrace();
                Log.e("Postcode_Response", t.getMessage() + "");
            }
        });
    }

    public void GetOTP() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mobile", et_mobno.getText().toString() + "");
        showProgressDialog();
        Call<OTPModel> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).OTPModel(hashMap);
        postCodeModelCall.enqueue(new Callback<OTPModel>() {
            @Override
            public void onResponse(@NonNull Call<OTPModel> call, @NonNull Response<OTPModel> response) {
                final OTPModel object = response.body();
                hideProgressDialog();

                if (object != null) {
                    Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
                    if (response.isSuccessful()) {
                        Toast.makeText(ChangePasswordMobileNumberActivity.this, object.getMessage() + "", Toast.LENGTH_SHORT).show();
                        otp = object.getData().getOtp() + "";
//                        token = object.getData().getToken() + "";
                    } else {
                        Toast.makeText(ChangePasswordMobileNumberActivity.this, object.getMessage() + "", Toast.LENGTH_SHORT).show();

                    }

                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<OTPModel> call, @NonNull Throwable t) {

                hideProgressDialog();
                t.printStackTrace();
                Log.e("Postcode_Response", t.getMessage() + "");
            }
        });

    }

    public void ResendOTP() {

    }


    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}