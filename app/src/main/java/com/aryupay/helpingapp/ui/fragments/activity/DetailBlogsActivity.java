package com.aryupay.helpingapp.ui.fragments.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterViewFlipper;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.adapter.FlipperAdapter;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.blogdetails.BlogDetailsModel;
import com.aryupay.helpingapp.modal.blogdetails.Image;
import com.aryupay.helpingapp.modal.bloglist.BlogListModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.ui.fragments.HomeFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBlogsActivity extends AppCompatActivity {

    AdapterViewFlipper adapterViewFlipper;
    TextView tvName, catName, tvTime, tvSubHeading, tvHeading, tvTotalView, tvTotalLikes, tvTotalComment, tvLocation;
    RelativeLayout btnComment, btnChat;
    String token, blogid;
    LoginModel loginModel;
    ImageView userProfile;

    protected ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_blogs);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        loginModel = PrefUtils.getUser(DetailBlogsActivity.this);
        token = loginModel.getData().getToken() + "";
        Intent i = getIntent();
        blogid = i.getStringExtra("blogid");

        adapterViewFlipper = findViewById(R.id.adapterViewFlipper);
        tvName = findViewById(R.id.tvName);
        catName = findViewById(R.id.catName);
        tvTime = findViewById(R.id.tvTime);
        tvSubHeading = findViewById(R.id.tvSubHeading);
        tvHeading = findViewById(R.id.tvHeading);
        tvTotalView = findViewById(R.id.tvTotalView);
        tvTotalLikes = findViewById(R.id.tvTotalLikes);
        tvTotalComment = findViewById(R.id.tvTotalComment);
        tvLocation = findViewById(R.id.tvLocation);
        btnComment = findViewById(R.id.btnComment);
        btnChat = findViewById(R.id.btnChat);
        userProfile = findViewById(R.id.userProfile);

        BlogDetails();
    }

    private void BlogDetails() {
        showProgressDialog();
        Call<BlogDetailsModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).BlogDetailsModel(blogid, "Bearer " + token);
        marqueCall.enqueue(new Callback<BlogDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<BlogDetailsModel> call, @NonNull Response<BlogDetailsModel> response) {
                BlogDetailsModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    tvName.setText(object.getData().getName() + "");
                    tvHeading.setText(object.getData().getHeading() + "");
                    tvSubHeading.setText(object.getData().getDescription() + "");
                    tvTime.setText(object.getData().getTime() + "");
                    tvLocation.setText(object.getData().getLocation() + "");
                    tvTotalComment.setText(object.getData().getComments() + "");
                    tvTotalLikes.setText(object.getData().getLikes() + "");
                    tvTotalView.setText(object.getData().getViews() + "");
                    catName.setText(object.getData().getCategory() + "");

                    ArrayList<Image> heros = response.body().getData().getImages();
                    FlipperAdapter adapter = new FlipperAdapter(DetailBlogsActivity.this, heros);

                    //adding it to adapterview flipper
                    adapterViewFlipper.setAdapter(adapter);
                    adapterViewFlipper.setFlipInterval(2000);
                    adapterViewFlipper.startFlipping();

                    if (object.getData().getUserdetail() != null) {
                        Glide.with(DetailBlogsActivity.this)
                                .load(BuildConstants.Main_Image + object.getData().getUserdetail().getPhoto().replace("public", "storage"))
                                .centerCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(userProfile);
                    }


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(DetailBlogsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(DetailBlogsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BlogDetailsModel> call, @NonNull Throwable t) {
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