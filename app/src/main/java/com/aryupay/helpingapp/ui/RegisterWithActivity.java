package com.aryupay.helpingapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterWithActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout signUpwithPhoneLl, signUpwithFacebookLl, signUpwithGoogleLl, signUpwithGmailLl;
    Button btnCreate;
    TextView tvAlreadyAc;
    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    //Tag for the logs optional
    private static final String TAG = "simplifiedcoding";

    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;

    //And also a Firebase Auth object
    FirebaseAuth mAuth;

    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_with);
        callbackManager = CallbackManager.Factory.create();
        signUpwithPhoneLl = findViewById(R.id.signUpwithPhoneLl);
        signUpwithFacebookLl = findViewById(R.id.signUpwithFacebookLl);
        signUpwithGmailLl = findViewById(R.id.signUpwithGmailLl);
        signUpwithGoogleLl = findViewById(R.id.signUpwithGoogleLl);
        btnCreate = findViewById(R.id.btnCreate);
        tvAlreadyAc = findViewById(R.id.tvAlreadyAc);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        signUpwithPhoneLl.setOnClickListener(this);
        signUpwithFacebookLl.setOnClickListener(this);
        signUpwithGmailLl.setOnClickListener(this);
        signUpwithGoogleLl.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        tvAlreadyAc.setOnClickListener(this);
        //first we intialized the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();

        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
            case R.id.signUpwithPhoneLl:
                Intent mobileIntent = new Intent(this, MobileRegisterActivity.class);
                startActivity(mobileIntent);
                break;
            case R.id.signUpwithFacebookLl:
                loginButton.performClick();
                break;
            case R.id.signUpwithGoogleLl:
                signIn();
                break;
            case R.id.signUpwithGmailLl:
                Intent reg = new Intent(this, RegisterActivity.class);
                startActivity(reg);
                break;
            case R.id.btnCreate:
                Intent regIntent = new Intent(this, RegisterActivity.class);
                startActivity(regIntent);
                break;
            case R.id.tvAlreadyAc:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
        }
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
                Toast.makeText(RegisterWithActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    Intent reg = new Intent(RegisterWithActivity.this, RegisterActivity.class);
                    reg.putExtra("name", user.getDisplayName() + "");
                    reg.putExtra("email", user.getEmail() + "");
                    startActivity(reg);

                    Toast.makeText(RegisterWithActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(RegisterWithActivity.this, "Authentication failed.",
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

}