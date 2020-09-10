package com.aryupay.helpingapp.ui.addBlog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.fragments.activity.DetailBlogsActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogPreviewSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout btnNext, btnBack;
    String id;
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    String token, location;
    CheckBox cb_show_contact, cb_show_chat, cb_enable_comment, cb_show_comment;
    CircleImageView ivProfile;
    TextView tv_name, tv_contact, tv_email;
    ImageView ivClose;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_preview_settings);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        loginModel = PrefUtils.getUser(BlogPreviewSettingsActivity.this);
        token = loginModel.getData().getToken() + "";
        Intent i = getIntent();
        id = i.getStringExtra("id");
        ivClose = findViewById(R.id.ivClose);
        btnNext = findViewById(R.id.btnDone);
        btnBack = findViewById(R.id.btnBack);
        cb_show_contact = findViewById(R.id.cb_show_contact);
        cb_show_chat = findViewById(R.id.cb_show_chat);
        cb_enable_comment = findViewById(R.id.cb_enable_comment);
        cb_show_comment = findViewById(R.id.cb_show_comment);

        tv_name = findViewById(R.id.tv_name);
        tv_contact = findViewById(R.id.tv_contact);
        tv_email = findViewById(R.id.tv_email);
        ivProfile = findViewById(R.id.ivProfile);

        tv_name.setText(loginModel.getData().getUser().getName() + "");
        tv_contact.setText(loginModel.getData().getUser().getUserDetail().getContact() + "");
        tv_email.setText(loginModel.getData().getUser().getEmail() + "");
        if (loginModel.getData().getUser().getUserDetail().getPhoto() != null) {
            Glide.with(this)
                    .load(BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage"))
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(ivProfile);
        }
        cb_show_contact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("show_contact", "1");
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).update_blog(id, hashMap, "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {

                                Toast.makeText(BlogPreviewSettingsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();


                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(BlogPreviewSettingsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(BlogPreviewSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            }
        });
        cb_show_chat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("chat_enable", "1");
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).update_blog(id, hashMap, "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {

                                Toast.makeText(BlogPreviewSettingsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();


                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(BlogPreviewSettingsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(BlogPreviewSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            }
        });
        cb_enable_comment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("comment_on_post", "1");
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).update_blog(id, hashMap, "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {

                                Toast.makeText(BlogPreviewSettingsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();


                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(BlogPreviewSettingsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(BlogPreviewSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            }
        });
        cb_show_comment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("show_comment", "1");
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).update_blog(id, hashMap, "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {

                                Toast.makeText(BlogPreviewSettingsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();


                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(BlogPreviewSettingsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(BlogPreviewSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            }
        });

        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivClose.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDone:
                Intent i = new Intent(BlogPreviewSettingsActivity.this, DetailBlogsActivity.class);
                i.putExtra("blogid", id + "");
                startActivity(i);
                finish();
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.ivClose:
                onBackPressed();
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