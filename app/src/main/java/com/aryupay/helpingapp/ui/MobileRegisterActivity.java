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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_mobno;
    OtpView otp_view;
    Button btnResend, btnVerifyOTP, btn_sumbitOtp;

    String otp, mobile, name, password, token, email, user_id;
    Boolean one = false;
    protected ViewDialog viewDialog;
    //firebase auth object
    private FirebaseAuth mAuth;
    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;
    String code;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_register);
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        Intent i = getIntent();

        auth = FirebaseAuth.getInstance();
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
            email = i.getStringExtra("email");
            user_id = i.getStringExtra("user_id");
            one = i.getBooleanExtra("one", false);

            et_mobno.setText(mobile);
            if (mobile != null) {
                sendVerificationCode(mobile);
            }


        }

        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp_s) {
                if (otp_s.matches(code)) {
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

                if (response.isSuccessful()) {
                    if (one == true) {


                        auth.createUserWithEmailAndPassword(email + "", password + "")
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.e("TASK", task + "");

                                        if (task.isSuccessful()) {
                                            FirebaseUser firebaseUser = auth.getCurrentUser();
                                            String userid = firebaseUser.getUid();
                                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("id", userid);
                                            hashMap.put("user_id", user_id + "");
                                            hashMap.put("username", name + "");
                                            hashMap.put("imageURL", "default");
                                            hashMap.put("status", "offline");
                                            hashMap.put("search", name.toLowerCase());
                                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
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

                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(MobileRegisterActivity.this, "You can't register with this email and password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                    } else {
                        Intent regIntent = new Intent(MobileRegisterActivity.this, RegisterActivity.class);
                        regIntent.putExtra("mobile", et_mobno.getText().toString() + "");
                        startActivity(regIntent);
                    }

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(MobileRegisterActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
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

                if (response.isSuccessful()) {
                    Toast.makeText(MobileRegisterActivity.this, object.getMessage() + "", Toast.LENGTH_SHORT).show();
//                        otp = object.getData().getOtp() + "";
                    token = object.getData().getToken() + "";
                    sendVerificationCode(et_mobno.getText().toString());
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(MobileRegisterActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
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
            Toast.makeText(MobileRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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