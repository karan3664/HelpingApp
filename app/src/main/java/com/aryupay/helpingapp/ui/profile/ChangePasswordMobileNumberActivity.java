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

import com.aryupay.helpingapp.ui.MobileRegisterActivity;
import com.aryupay.helpingapp.ui.RegisterActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
    private FirebaseAuth mAuth;
    String code;
    private String mVerificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_mobile_number);
        mAuth = FirebaseAuth.getInstance();
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
                if (otp_s.matches(code)) {
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
                        sendVerificationCode(et_mobno.getText().toString());
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

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            code = phoneAuthCredential.getSmsCode();
            Log.e("code", phoneAuthCredential.getSmsCode() + "");
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
//                otp = code;
                otp_view.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ChangePasswordMobileNumberActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(MobileRegisterActivity.this, "Otp Verified...", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                } else {
                    String message = "Somthing is wrong, we will fix it soon...";

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered...";
                    }
                }
            }
        });
    }



    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}