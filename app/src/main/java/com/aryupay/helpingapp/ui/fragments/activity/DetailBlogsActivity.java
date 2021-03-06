package com.aryupay.helpingapp.ui.fragments.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.adapter.FlipperAdapter;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.blogdetails.BlogDetailsModel;
import com.aryupay.helpingapp.modal.blogdetails.CommentsBlogModel;
import com.aryupay.helpingapp.modal.blogdetails.Datum;
import com.aryupay.helpingapp.modal.blogdetails.Image;
import com.aryupay.helpingapp.modal.chats.chatDetails.ChatDetailModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.other_user.OtherUserProfileModel;

import com.aryupay.helpingapp.ui.HomeActivity;
import com.aryupay.helpingapp.ui.chats.ChatListActivity;
import com.aryupay.helpingapp.ui.chats.Model.User;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBlogsActivity extends AppCompatActivity implements View.OnClickListener {

    AdapterViewFlipper adapterViewFlipper;
    TextView tvName, catName, tvTime, tvSubHeading, tvHeading, tvTotalView, tvTotalLikes, tvTotalComment, tvLocation;
    RelativeLayout btnComment, btnChat, rlCategory;
    String token, blogid, id, image_path, catname;
    LoginModel loginModel;
    ImageView userProfile, ivLike, ivFav, ivUnFav, ivCall, ivCallP;
    CircleImageView ivProfileImage;
    TextView tv_name, tvBiotxt, tv_bio, tvAgetxt, tv_gender, tv_location, profession,
            tvEmailId, tvMobileNo, tvFollowers, tvFollowing, tvHelping, tvReviewNo;
    ImageView iv_editprofile, ivOptionBlog, ivShare, ivOption, ivCloseProfile, ivClose;
    Button btnFollow;
    LinearLayout llClose;
    RecyclerView rvReviews;
    LinearLayout llFollowers, llFollowing, llHelping;
    private List<User> mUsers;
    protected ViewDialog viewDialog;
    LinearLayout llDetailContain, llBottom;
    String report = "";
    // Comment Section
    RecyclerView rvCommnetList;
    LinearLayout llCommentSection, llAddComment;
    CircleImageView userProfileComment;
    CardView cvOpenComment;
    private MyCustomAdapter myCustomAdapter;
    private MyCustomAdapterOther myCustomAdapterOther;
    ArrayList<Datum> datumArrayList = new ArrayList<>();

    EditText edtComment;
    Button btnCommentSave;
    String calls;
    NestedScrollView nesDetailBlog, nesProfile;
    String userid, name;
    ArrayList<com.aryupay.helpingapp.modal.other_user.Comment> commentArrayLis = new ArrayList<>();
    LinearLayout llRating;
    private AlertDialog.Builder alertDialogBuilder;

    ArrayList<String> timeArrayList = new ArrayList<>();
    ArrayList<Boolean> commentBooleanArrayList = new ArrayList<>();

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
        catname = i.getStringExtra("catname");
        mUsers = new ArrayList<>();
        adapterViewFlipper = findViewById(R.id.adapterViewFlipper);
        llDetailContain = findViewById(R.id.llDetailContain);
        llBottom = findViewById(R.id.llBottom);
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
        ivLike = findViewById(R.id.ivLike);
        ivFav = findViewById(R.id.ivFav);
        ivUnFav = findViewById(R.id.ivUnFav);
        ivCall = findViewById(R.id.ivCall);
        ivCallP = findViewById(R.id.ivCallP);
        nesDetailBlog = findViewById(R.id.nsvDetailBlog);
        nesProfile = findViewById(R.id.nesProfile);
        ivOptionBlog = findViewById(R.id.ivOptionBlog);
        ivClose = findViewById(R.id.ivClose);
        rlCategory = findViewById(R.id.rlCategory);

        //Comment Section

        llCommentSection = findViewById(R.id.llCommentSection);
        llAddComment = findViewById(R.id.llAddComment);
        rvCommnetList = findViewById(R.id.rvCommnetList);
        userProfileComment = findViewById(R.id.userProfileComment);
        cvOpenComment = findViewById(R.id.cvOpenComment);
        edtComment = findViewById(R.id.edtComment);
        btnCommentSave = findViewById(R.id.btnCommentSave);
        llRating = findViewById(R.id.llRating);

        LinearLayoutManager layoutManager = new LinearLayoutManager(DetailBlogsActivity.this);
        rvCommnetList.setLayoutManager(layoutManager);
        rvCommnetList.setHasFixedSize(true);

        ivLike.setOnClickListener(this);
        ivFav.setOnClickListener(this);
        ivUnFav.setOnClickListener(this);
        ivCall.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        cvOpenComment.setOnClickListener(this);
        btnCommentSave.setOnClickListener(this);
        ivOptionBlog.setOnClickListener(this);
        llRating.setOnClickListener(this);

        BlogDetails();
        CommentsList();

        // other user profile
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tv_name = findViewById(R.id.tv_name);
        tvBiotxt = findViewById(R.id.tvBiotxt);
        tv_bio = findViewById(R.id.tv_bio);
        tvAgetxt = findViewById(R.id.tvAgetxt);
        tv_gender = findViewById(R.id.tv_gender);
        tv_location = findViewById(R.id.tv_location);
        profession = findViewById(R.id.profession);
        tvEmailId = findViewById(R.id.tvEmailId);
        tvMobileNo = findViewById(R.id.tvMobileNo);
        btnFollow = findViewById(R.id.btnFollow);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        tvHelping = findViewById(R.id.tvHelping);
        tvReviewNo = findViewById(R.id.tvReviewNo);
        iv_editprofile = findViewById(R.id.iv_editprofile);
        llClose = findViewById(R.id.llClose);
        ivCloseProfile = findViewById(R.id.ivCloseProfile);
        ivShare = findViewById(R.id.ivShare);
        ivOption = findViewById(R.id.ivOption);
        rvReviews = findViewById(R.id.rvReviews);
        llFollowers = findViewById(R.id.llFollowers);
        llFollowing = findViewById(R.id.llfollowing);
        llFollowing.setOnClickListener(this);
        llFollowers.setOnClickListener(this);

        llHelping = findViewById(R.id.llHelping);
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nesDetailBlog.setVisibility(View.GONE);
                nesProfile.setVisibility(View.VISIBLE);
                ivLike.setVisibility(View.GONE);
            }
        });
        ivOption.setOnClickListener(this);
        LinearLayoutManager layoutManagesr = new LinearLayoutManager(DetailBlogsActivity.this);
        rvReviews.setLayoutManager(layoutManagesr);
        rvReviews.setHasFixedSize(true);
        llHelping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailBlogsActivity.this, ViewOtherBlogsPingActivity.class);
                i.putExtra("userid", userid + "");
                i.putExtra("name", name + "");
                startActivity(i);

            }
        });
        ivCloseProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailBlogsActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                finish();
            }
        });
        llClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailBlogsActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                finish();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onBackPressed();
                Intent i = new Intent(DetailBlogsActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                finish();
//                Intent intent = new Intent();
//                intent.putExtra("catname", catname + "");
//                setResult(RESULT_OK, intent);
//                finish();
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).followunfollo(userid + "", "Bearer " + token);
                marqueCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        JsonObject object = response.body();
                        hideProgressDialog();
                        Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                        if (object != null) {
                            btnFollow.setText("Following");
                            Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
//                            FollowerList();

                        } else {

//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
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
        });


        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + getApplication().getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


//        if (datum.getFollow() == false) {
//            holder.rlCategory.setBackgroundResource(R.drawable.btn_follow_bg);
//            holder.rlCategory.setText("Follow");
//        } else {
//            holder.rlCategory.setBackgroundResource(R.drawable.btn_back_bg);
//            holder.rlCategory.setText("Following");
//        }
    }

    private void BlogDetails() {
        showProgressDialog();
        Call<BlogDetailsModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).BlogDetailsModel(blogid, "Bearer " + token);
        marqueCall.enqueue(new Callback<BlogDetailsModel>() {
            @SuppressLint("SetTextI18n")
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
                    if (object.getData().getCategory().matches("information")) {
                        rlCategory.setBackgroundResource(R.drawable.information_cat_bg);
                    } else if (object.getData().getCategory().matches("urgent")) {
                        rlCategory.setBackgroundResource(R.drawable.urgent_cat_bg);

                    } else {

                    }
                    if (object.getData().getUserdetail() != null) {
                        calls = object.getData().getUserdetail().getContact() + "";
                    }

                    ArrayList<Image> heros = response.body().getData().getImages();
                    FlipperAdapter adapter = new FlipperAdapter(DetailBlogsActivity.this, heros);
                    if (object.getData().getFav() == true) {
                        ivFav.setImageResource(R.drawable.favourite_star);
                        ivFav.setVisibility(View.VISIBLE);
                        ivUnFav.setVisibility(View.GONE);
                    }
                    //adding it to adapterview flipper
                    adapterViewFlipper.setAdapter(adapter);
                    adapterViewFlipper.setFlipInterval(2000);
                    adapterViewFlipper.startFlipping();
                    userid = object.getData().getUserId() + "";
                    name = object.getData().getName() + "";

                    if (object.getData().getCommentOnPost() == 0) {
                        btnComment.setVisibility(View.GONE);
                    }
                    if (object.getData().getChatEnable() == 0) {
                        btnChat.setVisibility(View.GONE);
                    }
                    if (object.getData().getShowComment() == 0) {
                        rvCommnetList.setVisibility(View.GONE);
                    }
                    if (object.getData().getShowContact() == 1) {
                        ivCall.setVisibility(View.VISIBLE);
                        ivCallP.setVisibility(View.GONE);
//                        ivCall.setBackgroundResource(R.drawable.call_phone);
                    } else {
                        ivCallP.setVisibility(View.VISIBLE);
                        ivCall.setVisibility(View.GONE);
                    }

                    if (userid.matches(loginModel.getData().getUser().getId() + "")) {
                        ivOptionBlog.setVisibility(View.GONE);
                    }
                    if (userid != null) {
                        ProfileCall();
                    }
                    if (object.getData().getUserdetail().getPhoto() != null) {
                        image_path = object.getData().getUserdetail().getPhoto();
                        Glide.with(DetailBlogsActivity.this)
                                .load(BuildConstants.Main_Image + object.getData().getUserdetail().getPhoto().replace("public", "storage"))
//                                .centerCrop()
                                .placeholder(R.drawable.place_holder)
                                .into(userProfile);

                        Glide.with(DetailBlogsActivity.this)
                                .load(BuildConstants.Main_Image + object.getData().getUserdetail().getPhoto().replace("public", "storage"))
//                                .centerCrop()
                                .placeholder(R.drawable.place_holder)
                                .into(userProfileComment);
                    }


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
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

    private void CommentsList() {
        showProgressDialog();
        Call<CommentsBlogModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).CommentsBlogModel(blogid, "Bearer " + token);
        marqueCall.enqueue(new Callback<CommentsBlogModel>() {
            @Override
            public void onResponse(@NonNull Call<CommentsBlogModel> call, @NonNull Response<CommentsBlogModel> response) {
                CommentsBlogModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {

                    datumArrayList = object.getData();
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    rvCommnetList.setAdapter(myCustomAdapter);


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommentsBlogModel> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    private void AddComments() {
        if (edtComment.getText().toString().isEmpty()) {
            edtComment.setError("Add Comment...");
            edtComment.requestFocus();
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("comment", edtComment.getText().toString() + "");
            showProgressDialog();
            Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).comment_blog(blogid, "Bearer " + token, hashMap);
            marqueCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    JsonObject object = response.body();
                    hideProgressDialog();
                    Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                    if (response.isSuccessful()) {
                        BlogDetails();
                        edtComment.setText("");
                        CommentsList();
                        llCommentSection.setVisibility(View.VISIBLE);
                        llAddComment.setVisibility(View.GONE);
                        Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
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

    public void LikeBlog() {

        showProgressDialog();
        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).like_blog(blogid, "Bearer " + token);
        marqueCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    BlogDetails();
                    Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
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

    public void AddFavBlog() {
        ivUnFav.setVisibility(View.VISIBLE);
        ivFav.setVisibility(View.GONE);
        showProgressDialog();
        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).favourite(blogid, "Bearer " + token);
        marqueCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    BlogDetails();
                    Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
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

    public void RemoveFavBlog() {
        ivFav.setVisibility(View.VISIBLE);
        ivUnFav.setVisibility(View.GONE);
        showProgressDialog();
        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).favourite(blogid, "Bearer " + token);
        marqueCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    BlogDetails();
                    Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnComment:
                nesDetailBlog.setVisibility(View.VISIBLE);
                nesProfile.setVisibility(View.GONE);

                llDetailContain.setVisibility(View.GONE);
                llBottom.setVisibility(View.GONE);
                llCommentSection.setVisibility(View.VISIBLE);
                break;
            case R.id.btnChat:

                Call<ChatDetailModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ChatDetailModel(userid, "Bearer " + token);
                marqueCall.enqueue(new Callback<ChatDetailModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ChatDetailModel> call, @NonNull Response<ChatDetailModel> response) {
                        ChatDetailModel object = response.body();
                        Log.e("TAG", "ChatReceive_Response : " + new Gson().toJson(response.body()));

                        if (response.isSuccessful()) {
                            assert object != null;
                            assert response.body() != null;
                            if (response.body().getMessage().equalsIgnoreCase("User is Blocked!")) {
                                alertDialogBuilder = new AlertDialog.Builder(DetailBlogsActivity.this, R.style.AlertDialogTheme);
                                alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
                                alertDialogBuilder.setIcon(R.drawable.eyu);
                                alertDialogBuilder
                                        .setMessage(object.getMessage() + "")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                            } else {
                                Intent i = new Intent(DetailBlogsActivity.this, ChatListActivity.class);

                                startActivity(i);
                            }

                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ChatDetailModel> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        Log.e("ChatReceive_Response", t.getMessage() + "");
//                Toast.makeText(ChatDetailsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


                break;

            case R.id.cvOpenComment:
                llCommentSection.setVisibility(View.GONE);
                llAddComment.setVisibility(View.VISIBLE);

                break;

            case R.id.btnCommentSave:
                AddComments();

                break;
            case R.id.ivLike:
                LikeBlog();
                break;
            case R.id.ivFav:
                AddFavBlog();
                break;
            case R.id.ivUnFav:
                RemoveFavBlog();
                break;
            case R.id.ivCall:
                if (calls != null) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + calls));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No Contact Avaiable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ivOptionBlog:
                ReportBlog();
                break;
            case R.id.ivOption:
                ReportOption();
                break;
            case R.id.llRating:
                RatingOption();
                break;

            case R.id.llFollowers:
                Intent fol = new Intent(DetailBlogsActivity.this, OtherFollowFollowingActivity.class);
                fol.putExtra("frag1", 0);
                fol.putExtra("user_id", userid + "");
                fol.putExtra("name", name + "");
                startActivity(fol);

                break;

            case R.id.llfollowing:
                Intent foll = new Intent(DetailBlogsActivity.this, OtherFollowFollowingActivity.class);
                foll.putExtra("frag1", 1);
                foll.putExtra("user_id", userid + "");
                foll.putExtra("name", name + "");
                startActivity(foll);
                break;
        }
    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<Datum> moviesList;

        public MyCustomAdapter(ArrayList<Datum> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_list, parent, false);

            return new MyViewHolder(itemView);
        }

        public void clear() {
            int size = this.moviesList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.moviesList.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final Datum datum = moviesList.get(position);


//            Uri uri = Uri.parse(BuildConstants.Main_Image + datum.getThmbnailImage());
//            holder.imageView_Spotlight.setImageURI(uri);

            holder.tvName.setText(datum.getFullname() + "");
            if (datum.getComments() != null) {
                holder.tvHeading.setText(datum.getComments() + "");
            } else {
                holder.tvHeading.setText("");
            }

            holder.tvTime.setText(datum.getCreatedAt() + "");

//            holder.tvTotalComment.setText(Comments.get(position) + "");
//            holder.tvTotalView.setText(Views.get(position) + "");
//            holder.tvTotalLikes.setText(datum.get+ "");
            if (datum.getPhoto() != null) {
                if (datum.getPhoto().getPhoto() != null) {


                    Glide.with(DetailBlogsActivity.this)
                            .load(BuildConstants.Main_Image + datum.getPhoto().getPhoto().replace("public", "storage"))
//                        .centerCrop()
                            .placeholder(R.drawable.place_holder)
                            .into(holder.ivEmployee);
                }
            }
            holder.rlReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llCommentSection.setVisibility(View.GONE);
                    llAddComment.setVisibility(View.VISIBLE);

                }
            });
            holder.llReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog dialogs = new Dialog(Objects.requireNonNull(DetailBlogsActivity.this));
                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                    dialogs.setContentView(R.layout.dialog_rating_comment);
                    dialogs.setCancelable(true);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(Objects.requireNonNull(dialogs.getWindow()).getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    Button btnSendReport = dialogs.findViewById(R.id.btnSendReport);

                    ChipGroup chipGroup = dialogs.findViewById(R.id.chipReportCommentGroup);
                    chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(ChipGroup chipGroup, int i) {
                            Chip chip = chipGroup.findViewById(i);
                            report = chip.getText().toString();

                        }
                    });


                    btnSendReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (report.isEmpty()) {
                                Toast.makeText(DetailBlogsActivity.this, "Please Select Reason", Toast.LENGTH_SHORT).show();
                            } else {
                                HashMap<String, String> hashMap = new HashMap<>();

                                hashMap.put("report", report + "");
                                showProgressDialog();
                                Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).report_comment(datum.getId() + "", "Bearer " + token, hashMap);
                                marqueCall.enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                        JsonObject object = response.body();
                                        hideProgressDialog();
                                        Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                        if (response.isSuccessful()) {
                                            dialogs.dismiss();

                                            Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                                        } else {
                                            try {
                                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
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


                    dialogs.show();
                    dialogs.getWindow().setAttributes(lp);
                }
            });
            if (datum.getCommentLike().booleanValue() == false) {
//                holder.rvLikeComment.setBackgroundResource(R.drawable.add_ping_edittext_bg);
                holder.rvLikeComment.setVisibility(View.VISIBLE);
                holder.rvUnLikeComment.setVisibility(View.GONE);
            } else {

            }
            holder.rvLikeComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.rvLikeComment.setVisibility(View.GONE);
                    holder.rvUnLikeComment.setVisibility(View.VISIBLE);
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).like_comment(datum.getId() + "", "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {

                                BlogDetails();
                                ProfileCall();
                                Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
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
            });
            holder.rvUnLikeComment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    holder.rvLikeComment.setVisibility(View.VISIBLE);
                    holder.rvUnLikeComment.setVisibility(View.GONE);
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).like_comment(datum.getId() + "", "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {

                                BlogDetails();
                                ProfileCall();
                                Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
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
            });

        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CircleImageView ivEmployee;
            TextView tvName, tvTime, tvHeading, tvTotalLikes;
            RelativeLayout rlReply, rvLikeComment, rvUnLikeComment;
            LinearLayout llReport;

            public MyViewHolder(View view) {
                super(view);


                ivEmployee = view.findViewById(R.id.ivEmployee);
                tvName = view.findViewById(R.id.tvName);

                tvTime = view.findViewById(R.id.tvTime);
                tvHeading = view.findViewById(R.id.tvHeading);

                tvTotalLikes = view.findViewById(R.id.tvTotalLikes);
                rlReply = view.findViewById(R.id.rlReply);
                llReport = view.findViewById(R.id.llReport);
                rvLikeComment = view.findViewById(R.id.rvLikeComment);
                rvUnLikeComment = view.findViewById(R.id.rvUnLikeComment);


            }

        }

    }

    private void ProfileCall() {


        Call<OtherUserProfileModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).OtherUserProfileModel(userid + "", "Bearer " + token);
        marqueCall.enqueue(new Callback<OtherUserProfileModel>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<OtherUserProfileModel> call, @NonNull Response<OtherUserProfileModel> response) {
                OtherUserProfileModel object = response.body();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    commentArrayLis = object.getData().getComments();
                    tvEmailId.setText(object.getData().getEmail() + "");
                    tv_name.setText(object.getData().getName() + "");
                    timeArrayList = object.getData().getTime();
                    commentBooleanArrayList = object.getData().getCommentLike();
                    if (object.getData().getUserDetail() != null) {
                        tvBiotxt.setText(object.getData().getUserDetail().getBio() + "");
//                    tvAgetxt.setText(object.getData().getUserDetail().getDob() + "");
                        tv_gender.setText(" | " + object.getData().getUserDetail().getGender() + "");
                        tv_location.setText(object.getData().getUserDetail().getCity().getCity() + "");
                        profession.setText(object.getData().getUserDetail().getProfession().getProfession() + "");

                        tvMobileNo.setText(object.getData().getUserDetail().getContact() + "");
                     /*   try {
                            LocalDate today = LocalDate.now();
                            LocalDate birthday = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                birthday = LocalDate.parse(object.getData().getUserDetail().getDob());
                                Period p = Period.between(birthday, today);
                                tvAgetxt.setText(p.getYears() + " Years " + p.getMonths() + " Months " + ",");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        if (object.getData().getFollow() == true) {
                            btnFollow.setText("Following");
                        } else {
                            btnFollow.setText("Follow");
                        }


                        try {

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDate today = LocalDate.now();
                                LocalDate birthday = null;
                                birthday = LocalDate.parse(loginModel.getData().getUser().getUserDetail().getDob());
                                Period p = Period.between(birthday, today);
                                tvAgetxt.setText(p.getYears() + " Years " + p.getMonths() + " Months " + " ");
                            } else {
                                tvAgetxt.setText(loginModel.getData().getUser().getUserDetail().getDob());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    tvFollowers.setText(object.getData().getFollowers() + "");
                    tvFollowing.setText(object.getData().getFollowing() + "");
//                    tvHelping.setText(object.getData().get() + "");
//                    tvReviewNo.setText(object.getData().getUserDetail+ "");
                    myCustomAdapterOther = new MyCustomAdapterOther(commentArrayLis);
                    rvReviews.setAdapter(myCustomAdapterOther);

                    if (object.getData().getUserDetail().getPhoto() != null) {
                        Glide.with(DetailBlogsActivity.this)
                                .load(BuildConstants.Main_Image + object.getData().getUserDetail().getPhoto().replace("public", "storage"))
                                .placeholder(R.drawable.place_holder)
                                .centerCrop()
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                .into(ivProfileImage);
//                        Log.e("Profile=>", BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage" + ""));
                    }
                } else {
//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OtherUserProfileModel> call, @NonNull Throwable t) {
                t.printStackTrace();

                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    public class MyCustomAdapterOther extends RecyclerView.Adapter<MyCustomAdapterOther.MyViewHolder> {

        private ArrayList<com.aryupay.helpingapp.modal.other_user.Comment> moviesList;

        public MyCustomAdapterOther(ArrayList<com.aryupay.helpingapp.modal.other_user.Comment> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_list, parent, false);

            return new MyViewHolder(itemView);
        }

        public void clear() {
            int size = this.moviesList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.moviesList.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final com.aryupay.helpingapp.modal.other_user.Comment datum = moviesList.get(position);
            holder.tvName.setText(datum.getFullname() + "");
            if (datum.getComment() != null) {
                holder.tvHeading.setText(datum.getComment() + "");
            } else {
                holder.tvHeading.setText("");
            }
//            if (timeArrayList != null)
            holder.tvTime.setText(timeArrayList.get(position) + "");


//            holder.tvTotalComment.setText(Comments.get(position) + "");
//            holder.tvTotalView.setText(Views.get(position) + "");
//            holder.tvTotalLikes.setText(datum.getLike()+ "");
            if (datum.getPhoto() != null) {
                Glide.with(DetailBlogsActivity.this)
                        .load(BuildConstants.Main_Image + datum.getPhoto().replace("public", "storage"))
//                        .centerCrop()
                        .placeholder(R.drawable.place_holder)
                        .into(holder.ivEmployee);
            }

            holder.rlReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RatingOption();
                }
            });

            holder.rvReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialogs = new Dialog(Objects.requireNonNull(DetailBlogsActivity.this));
                    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                    dialogs.setContentView(R.layout.dialog_rating_comment);
                    dialogs.setCancelable(true);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(Objects.requireNonNull(dialogs.getWindow()).getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
                    Button btnSendReport = dialogs.findViewById(R.id.btnSendReport);

                    ChipGroup chipGroup = dialogs.findViewById(R.id.chipReportCommentGroup);

                    chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(ChipGroup chipGroup, int i) {
                            Chip chip = chipGroup.findViewById(i);
                            report = chip.getText().toString();

                        }
                    });


//                    String finalReport = report;
                    btnSendReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (report.isEmpty()) {
                                Toast.makeText(DetailBlogsActivity.this, "Please Select Reason", Toast.LENGTH_SHORT).show();
                            } else {
                                HashMap<String, String> hashMap = new HashMap<>();

                                hashMap.put("report", report + "");
                                showProgressDialog();
                                Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).report_comment(datum.getId() + "", "Bearer " + token, hashMap);
                                marqueCall.enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                        JsonObject object = response.body();
                                        hideProgressDialog();
                                        Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                        if (response.isSuccessful()) {
                                            dialogs.dismiss();

                                            Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                                        } else {
                                            try {
                                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
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


                    dialogs.show();
                    dialogs.getWindow().setAttributes(lp);
                }
            });

            if (commentBooleanArrayList.get(position).booleanValue() == false) {
                holder.rvLikeComment.setBackgroundResource(R.drawable.add_ping_edittext_bg);
            } else {

            }

            holder.rvLikeComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).like_comment(datum.getId() + "", "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {

                                BlogDetails();
                                ProfileCall();
                                Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
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
            });

        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView ivEmployee;
            TextView tvName, tvTime, tvHeading, tvTotalLikes;
            RelativeLayout rlReply, rvReport, rvLikeComment;

            public MyViewHolder(View view) {
                super(view);


                ivEmployee = view.findViewById(R.id.ivEmployee);
                tvName = view.findViewById(R.id.tvName);

                tvTime = view.findViewById(R.id.tvTime);
                tvHeading = view.findViewById(R.id.tvComments);

                tvTotalLikes = view.findViewById(R.id.tvTotalLikes);
                rlReply = view.findViewById(R.id.rlReply);
                rvReport = view.findViewById(R.id.rvReport);
                rvLikeComment = view.findViewById(R.id.rvLikeComment);

            }

        }

    }

    public void ReportBlog() {
        final Dialog dialog = new Dialog(Objects.requireNonNull(DetailBlogsActivity.this));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_report_blog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final RadioGroup radioSexGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReportBlog);

        final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        final Button btnSend = (Button) dialog.findViewById(R.id.btnSend);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get selected radio button from radioGroup
                int selectedId = radioSexGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);


                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("report", radioButton.getText().toString() + "");
                showProgressDialog();
                Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).report_blog(blogid, "Bearer " + token, hashMap);
                marqueCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        JsonObject object = response.body();
                        hideProgressDialog();
                        Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            BlogDetails();
                            Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
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
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void ReportOption() {
        final Dialog dialogs = new Dialog(Objects.requireNonNull(DetailBlogsActivity.this));
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogs.setContentView(R.layout.dialog_report_options);
        dialogs.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogs.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP | Gravity.RIGHT;
        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView tvShareProfile = dialogs.findViewById(R.id.tvShareProfile);
        final TextView tvReportUser = dialogs.findViewById(R.id.tvReportUser);

        tvShareProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
        tvReportUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                final Dialog dialog = new Dialog(Objects.requireNonNull(DetailBlogsActivity.this));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.dialog_report_user);
                dialog.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final RadioGroup radioSexGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReportUser);

                final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                final Button btnSend = (Button) dialog.findViewById(R.id.btnSend);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // get selected radio button from radioGroup
                        int selectedId = radioSexGroup.getCheckedRadioButtonId();

                        // find the radiobutton by returned id
                        RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);


                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("report", radioButton.getText().toString() + "");
                        showProgressDialog();
                        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).report_user(userid, "Bearer " + token, hashMap);
                        marqueCall.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                JsonObject object = response.body();
                                hideProgressDialog();
                                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                if (response.isSuccessful()) {
                                    dialog.dismiss();
                                    BlogDetails();
                                    ProfileCall();
                                    Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
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
                });
                dialog.show();
                dialog.getWindow().setAttributes(lp);

            }
        });

        dialogs.show();
        dialogs.getWindow().setAttributes(lp);
    }

    public void RatingOption() {
        final Dialog dialogs = new Dialog(Objects.requireNonNull(DetailBlogsActivity.this));
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogs.setContentView(R.layout.dialog_rating_user);
        dialogs.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogs.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RatingBar ratingStar = dialogs.findViewById(R.id.ratingStar);
        EditText edtComment = dialogs.findViewById(R.id.edtComment);
        Button btnSendRating = dialogs.findViewById(R.id.btnSendRating);

        btnSendRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("rate", ratingStar.getNumStars() + "");
                hashMap.put("comment", edtComment.getText().toString() + "");
                showProgressDialog();
                Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).rateprofile(userid, "Bearer " + token, hashMap);
                marqueCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        JsonObject object = response.body();
                        hideProgressDialog();
                        Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                        if (response.isSuccessful()) {
                            dialogs.dismiss();
                            ProfileCall();
                            Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(DetailBlogsActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
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
        });

        dialogs.show();
        dialogs.getWindow().setAttributes(lp);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                // do something here
                Intent i = new Intent(DetailBlogsActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}