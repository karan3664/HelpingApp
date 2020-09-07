package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.ui.MobileRegisterActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivClose;
    LinearLayout llChangePassword;
    CheckBox cbShowContact, cbShowMailid, cbEnablePersoncalChat, cbEnableCommentPost, cbShowPostComment;
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        loginModel = PrefUtils.getUser(PrivacyActivity.this);
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        ivClose = findViewById(R.id.ivClose);
        llChangePassword = findViewById(R.id.llChangePassword);
        cbShowContact = findViewById(R.id.cbShowContact);
        cbShowMailid = findViewById(R.id.cbShowMailid);
        cbEnablePersoncalChat = findViewById(R.id.cbEnablePersoncalChat);
        cbEnableCommentPost = findViewById(R.id.cbEnableCommentPost);
        cbShowPostComment = findViewById(R.id.cbShowPostComment);


        ivClose.setOnClickListener(this);
        llChangePassword.setOnClickListener(this);

        cbShowContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("show_contact", 1 + "");


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

                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                    hashMap.put("show_contact", 0 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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


        cbShowMailid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put("show_mail_id", 1 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                    hashMap.put("show_mail_id", 0 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        cbEnablePersoncalChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put("enable_personal_chat", 1 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                    hashMap.put("enable_personal_chat", 0 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

        cbEnableCommentPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put("enable_comment_on_post", 1 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                    hashMap.put("enable_comment_on_post", 0 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        cbShowPostComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put("show_post_comment_to_all", 1 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                    hashMap.put("show_post_comment_to_all", 0 + "");


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
                                    Toast.makeText(PrivacyActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(PrivacyActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(PrivacyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivClose:
                onBackPressed();
                break;

            case R.id.llChangePassword:
                Intent changePassword = new Intent(PrivacyActivity.this, ChangePasswordMobileNumberActivity.class);
                startActivity(changePassword);
                break;


        }
    }


    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}