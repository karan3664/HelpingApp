package com.aryupay.helpingapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.profile.ChangePasswordMobileNumberActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText etUsername, etPassword;
    TextView tvForgotPassword;
    Button btnLogin, btn_phone, btn_facebook, btn_google;
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    CallbackManager callbackManager;
    LoginButton loginButton;
    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    //Tag for the logs optional
    private static final String TAG = "simplifiedcoding";

    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;

    //And also a Firebase Auth object
    FirebaseAuth mAuth;
    String tokenFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setReadPermissions("email");
        loginModel = PrefUtils.getUser(LoginActivity.this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        //first we intialized the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            tokenFirebase = task.getResult().getToken();
                        } else {

                        }
                    }
                });
        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btn_phone = findViewById(R.id.btn_phone);
        btn_facebook = findViewById(R.id.btn_facebook);
        btn_google = findViewById(R.id.btn_google);

        tvForgotPassword.setOnClickListener(this);

        btnLogin.setOnClickListener(this);
        btn_phone.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        btn_google.setOnClickListener(this);

        try {
            if (loginModel.getStatus().matches("success")) {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvForgotPassword:
                Intent forgotIntent = new Intent(this, ChangePasswordMobileNumberActivity.class);
                startActivity(forgotIntent);
                break;

            case R.id.btnLogin:
                LoginCall();
                break;

            case R.id.btn_phone:
                Intent mobileIntent = new Intent(this, MobileRegisterActivity.class);
                startActivity(mobileIntent);
                break;

            case R.id.btn_facebook:
                loginButton.performClick();
                break;

            case R.id.btn_google:
                signIn();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
//        if (mAuth.getCurrentUser() != null) {
//            finish();
//            startActivity(new Intent(this, HomeActivity.class));
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent reg = new Intent(LoginActivity.this, RegisterActivity.class);
                    reg.putExtra("name", user.getDisplayName() + "");
                    reg.putExtra("email", user.getEmail() + "");
                    startActivity(reg);

//                    Toast.makeText(LoginActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                }


            }
        });
    }


    //this method is called on click
    private void signIn() {
        //getting the google signin intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void LoginCall() {

        final String email_id = etUsername.getText().toString().trim();
        final String pass_word = etPassword.getText().toString().trim();


        if (email_id.isEmpty()) {
            etUsername.setError("Username Required...");
            etUsername.requestFocus();
            return;
        } else if (pass_word.isEmpty()) {
            etPassword.setError("Password Required...");
            etPassword.requestFocus();
            return;
        } else {
            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put("name", email_id + "");
            hashMap.put("password", pass_word + "");

            showProgressDialog();
            Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LoginModel(hashMap);
            loginModelCall.enqueue(new Callback<LoginModel>() {

                @Override
                public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                    LoginModel object = response.body();
                    Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));

                    hideProgressDialog();

                    if (response.isSuccessful()) {
                        PrefUtils.setUser(object, LoginActivity.this);
                        String androidVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
                        HashMap<String, String> map = new HashMap<>();
                        map.put("registration_id", tokenFirebase);
                        map.put("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                        map.put("device_os", "Android " + androidVersion);
                        Log.d(tokenFirebase + " ", "jdgfhrugf");
                        Log.e("Hasp", map + "");
                        Call<JsonObject> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).updateAppInfo(map, "Bearer " + object.getData().getToken());
                        loginModelCall.enqueue(new Callback<JsonObject>() {

                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                JsonObject object = response.body();
                                Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));

                                hideProgressDialog();

                                if (response.isSuccessful()) {


                                    Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginIntent);
                                    finish();
                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(LoginActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                hideProgressDialog();
                                t.printStackTrace();
                                Log.e("Login_Response", t.getMessage() + "");
                            }
                        });


                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(LoginActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                    hideProgressDialog();
                    t.printStackTrace();
                    Log.e("Login_Response_Fail", t.getMessage() + "");
                }
            });
        }
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}