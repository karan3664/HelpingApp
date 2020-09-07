package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

public class NotificationsActivity extends AppCompatActivity {

    ImageView ivClose;
    CheckBox cbMuteNotification, cbNotification;
    LoginModel loginModel;
    String token;
    protected ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        loginModel = PrefUtils.getUser(NotificationsActivity.this);
        token = loginModel.getData().getToken();

        ivClose = findViewById(R.id.ivClose);
        cbMuteNotification = findViewById(R.id.cbMuteNotification);
        cbNotification = findViewById(R.id.cbNotification);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cbNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("notification", 1 + "");


                    showProgressDialog();
                    Call<JsonObject> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).notification_setting("Bearer " + token, hashMap);
                    postCodeModelCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            final JsonObject object = response.body();
                            hideProgressDialog();

                            if (object != null) {
                                Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
                                if (response.isSuccessful()) {

                                    Toast.makeText(NotificationsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(NotificationsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(NotificationsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                } else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("notification", 0 + "");


                    showProgressDialog();
                    Call<JsonObject> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).myprofile_settings("Bearer " + token, hashMap);
                    postCodeModelCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            final JsonObject object = response.body();
                            hideProgressDialog();

                            if (object != null) {
                                Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
                                if (response.isSuccessful()) {

                                    Toast.makeText(NotificationsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(NotificationsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(NotificationsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        });
        cbMuteNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("mute_notification", 1 + "");


                    showProgressDialog();
                    Call<JsonObject> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).myprofile_settings("Bearer " + token, hashMap);
                    postCodeModelCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            final JsonObject object = response.body();
                            hideProgressDialog();

                            if (object != null) {
                                Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
                                if (response.isSuccessful()) {

                                    Toast.makeText(NotificationsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(NotificationsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(NotificationsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                } else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("mute_notification", 0 + "");


                    showProgressDialog();
                    Call<JsonObject> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).myprofile_settings("Bearer " + token, hashMap);
                    postCodeModelCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            final JsonObject object = response.body();
                            hideProgressDialog();

                            if (object != null) {
                                Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
                                if (response.isSuccessful()) {

                                    Toast.makeText(NotificationsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(NotificationsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(NotificationsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        });
    }
    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}