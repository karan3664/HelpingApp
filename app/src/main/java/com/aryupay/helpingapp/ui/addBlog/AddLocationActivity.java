package com.aryupay.helpingapp.ui.addBlog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;

import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_location;
    RelativeLayout btnNext, btnBack;
    String id;
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    SharedPreferences preferences;
    String token, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        tv_location = findViewById(R.id.tv_location);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        loginModel = PrefUtils.getUser(AddLocationActivity.this);
        token = loginModel.getData().getToken() + "";
        Intent i = getIntent();
        id = i.getStringExtra("id");
        preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        location = preferences.getString("location", "");
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        tv_location.setText(location);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                AddLocationCall();
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }


    public void AddLocationCall() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("location", tv_location.getText().toString() + "");
        showProgressDialog();
        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).update_blog(id, hashMap,"Bearer " + token);
        marqueCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {

                    Toast.makeText(AddLocationActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(AddLocationActivity.this, BlogPreviewSettingsActivity.class);
                    i.putExtra("id", id+"");
                    startActivity(i);
                    finish();

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(AddLocationActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(AddLocationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
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