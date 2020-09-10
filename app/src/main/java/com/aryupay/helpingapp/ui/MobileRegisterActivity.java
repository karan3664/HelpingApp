package com.aryupay.helpingapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.city.CityModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.phonesignup.PhoneSignupModel;
import com.aryupay.helpingapp.modal.profession.ProfessionModel;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_mobno;
    OtpView otp_view;
    Button btnResend, btnVerifyOTP, btn_sumbitOtp;

    String otp, mobile, name, password, token;
    Boolean one = false ;
    protected ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_register);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        Intent i = getIntent();


        et_mobno = findViewById(R.id.et_mobno);
        otp_view = findViewById(R.id.otp_view);
        btnResend = findViewById(R.id.btnResend);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        btn_sumbitOtp = findViewById(R.id.btn_sumbitOtp);

        btnResend.setOnClickListener(this);
        btnVerifyOTP.setOnClickListener(this);
        btn_sumbitOtp.setOnClickListener(this);


        if (i != null) {
            otp = i.getStringExtra("otp");
            name = i.getStringExtra("name");
            password = i.getStringExtra("password");
            mobile = i.getStringExtra("mobile");
            token = i.getStringExtra("token");
            one = i.getBooleanExtra("one", false);

            et_mobno.setText(mobile);
        }

        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp_s) {
                if (otp_s.matches(otp)) {
                    Toast.makeText(MobileRegisterActivity.this, "OTP Verified...", Toast.LENGTH_SHORT).show();
                    btnVerifyOTP.setEnabled(true);
                } else {
                    Toast.makeText(MobileRegisterActivity.this, "OTP Invalid!!!", Toast.LENGTH_SHORT).show();
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
                        if (one == true) {
                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("name", name + "");
                            hashMap.put("password", password + "");

                            showProgressDialog();
                            Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LoginModel(hashMap);
                            loginModelCall.enqueue(new Callback<LoginModel>() {

                                @Override
                                public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                                    LoginModel object = response.body();
                                    Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));

                                    hideProgressDialog();

                                    if (response.isSuccessful()) {
                                        PrefUtils.setUser(object, MobileRegisterActivity.this);
                                        Intent loginIntent = new Intent(MobileRegisterActivity.this, HomeActivity.class);
                                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(loginIntent);
                                    } else {
                                        try {
                                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                                            Toast.makeText(MobileRegisterActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                                    hideProgressDialog();
                                    t.printStackTrace();
                                    Log.e("Login_Response", t.getMessage() + "");
                                }
                            });
                        } else {
                            Intent regIntent = new Intent(MobileRegisterActivity.this, RegisterActivity.class);
                            regIntent.putExtra("mobile", et_mobno.getText().toString() + "");
                            startActivity(regIntent);
                        }

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
        hashMap.put("contact", et_mobno.getText().toString() + "");
        showProgressDialog();
        Call<PhoneSignupModel> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).PhoneSignupModel(hashMap);
        postCodeModelCall.enqueue(new Callback<PhoneSignupModel>() {
            @Override
            public void onResponse(@NonNull Call<PhoneSignupModel> call, @NonNull Response<PhoneSignupModel> response) {
                final PhoneSignupModel object = response.body();
                hideProgressDialog();

                if (object != null) {
                    Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
                    if (response.isSuccessful()) {
                        Toast.makeText(MobileRegisterActivity.this, object.getMessage() + "", Toast.LENGTH_SHORT).show();
                        otp = object.getData().getOtp() + "";
                        token = object.getData().getToken() + "";
                    }
                    else {
                        Toast.makeText(MobileRegisterActivity.this, object.getMessage() + "", Toast.LENGTH_SHORT).show();

                    }

                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<PhoneSignupModel> call, @NonNull Throwable t) {

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