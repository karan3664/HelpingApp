package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.changePassword.otp.OTPModel;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    String mobile;

    EditText contactNumberEt, et_password, et_confirmpassword;
    Button btnSavePassword;
    protected ViewDialog viewDialog;
    ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Intent i = getIntent();
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);

        contactNumberEt = findViewById(R.id.contactNumberEt);
        et_password = findViewById(R.id.et_password);
        et_confirmpassword = findViewById(R.id.et_confirmpassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
        btnSavePassword.setOnClickListener(this);
        ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);

        if (i != null) {
            mobile = i.getStringExtra("mobile");
            contactNumberEt.setText(mobile);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSavePassword:
                ChangePasswordCall();
                break;
            case R.id.ivClose:
                onBackPressed();
                break;

        }
    }

    public void ChangePasswordCall() {
        if (et_password.getText().toString().isEmpty()) {
            et_password.setError("Password Required...");
            et_password.requestFocus();
        } else if (et_confirmpassword.getText().toString().isEmpty()) {
            et_confirmpassword.setError("Confirm Password Required...");
            et_confirmpassword.requestFocus();
        } else {


            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("mobile", contactNumberEt.getText().toString() + "");
            hashMap.put("password", et_password.getText().toString() + "");
            hashMap.put("c_password", et_confirmpassword.getText().toString() + "");

            showProgressDialog();
            Call<JsonObject> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).reset(hashMap);
            postCodeModelCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    final JsonObject object = response.body();
                    hideProgressDialog();

                    if (object != null) {
                        Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
                        if (response.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                            PrefUtils.clearCurrentUser(ChangePasswordActivity.this);
                            Intent i = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(ChangePasswordActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {

                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                    hideProgressDialog();
                    t.printStackTrace();
                    Log.e("Postcode_Response", t.getMessage() + "");
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