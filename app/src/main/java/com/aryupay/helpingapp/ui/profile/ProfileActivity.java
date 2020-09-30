package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.bloglist.Blog;
import com.aryupay.helpingapp.modal.bloglist.BlogListModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.profile.my_profile.Comment;
import com.aryupay.helpingapp.modal.profile.my_profile.MyProfileModel;
import com.aryupay.helpingapp.ui.HomeActivity;
import com.aryupay.helpingapp.ui.fragments.ChatFragment;
import com.aryupay.helpingapp.ui.fragments.HomeFragment;
import com.aryupay.helpingapp.ui.fragments.MyPingFragment;
import com.aryupay.helpingapp.ui.fragments.activity.HelpingActivity;
import com.aryupay.helpingapp.ui.profile.ui.FollowersFragment;
import com.aryupay.helpingapp.ui.profile.ui.FollowingFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONObject;

import java.text.ParseException;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView ivProfileImage;
    TextView tv_name, tvBiotxt, tv_bio, tvAgetxt, tv_gender, profession, tv_location,
            tvEmailId, tvMobileNo, tvFollowers, tvFollowing, tvHelping, tvReviewNo, tvNocomments;
    ImageView iv_editprofile, ivClose, ivShare, ivOption;
    RecyclerView rvReviews;

    LoginModel loginModel;
    String token;
    private MyCustomAdapter myCustomAdapter;
    LinearLayout llFollowers, llFollowing, llHelping;
    protected ViewDialog viewDialog;
    ArrayList<Comment> commentArrayLis = new ArrayList<>();
    String report = "";
    LinearLayout llClose;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);

        setContentView(R.layout.activity_profile);
        loginModel = PrefUtils.getUser(ProfileActivity.this);
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
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
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        tvHelping = findViewById(R.id.tvHelping);
        tvReviewNo = findViewById(R.id.tvReviewNo);
        iv_editprofile = findViewById(R.id.iv_editprofile);
        ivClose = findViewById(R.id.ivClose);
        llClose = findViewById(R.id.llClose);
        ivShare = findViewById(R.id.ivShare);
        ivOption = findViewById(R.id.ivOption);
        rvReviews = findViewById(R.id.rvReviews);
        llFollowers = findViewById(R.id.llFollowers);
        llFollowing = findViewById(R.id.llfollowing);
        llHelping = findViewById(R.id.llHelping);
        tvNocomments = findViewById(R.id.tvNocomments);

        tv_name.setText(loginModel.getData().getUser().getFullname() + "");
        tv_bio.setText(loginModel.getData().getUser().getUserDetail().getBio() + "");
        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate today = LocalDate.now();
                LocalDate birthday = null;
                birthday = LocalDate.parse(loginModel.getData().getUser().getUserDetail().getDob());
                Period p = Period.between(birthday, today);
                tvAgetxt.setText(p.getYears() + " Years " + p.getMonths() + " Months " + ",");
            } else {
                tvAgetxt.setText(loginModel.getData().getUser().getUserDetail().getDob());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        tv_gender.setText(loginModel.getData().getUser().getUserDetail().getGender() + "");
        tv_location.setText(loginModel.getData().getUser().getUserDetail().getCity().getCity() + "");
        tvEmailId.setText(loginModel.getData().getUser().getEmail() + "");
        tvMobileNo.setText(loginModel.getData().getUser().getUserDetail().getContact() + "");

        if (loginModel.getData().getUser().getUserDetail().getPhoto() != null) {
            Glide.with(this)
                    .load(BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage"))
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(ivProfileImage);
            Log.e("Profile=>", BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage" + ""));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this);
        rvReviews.setLayoutManager(layoutManager);
        rvReviews.setHasFixedSize(true);

        ProfileCall();

        iv_editprofile.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        llClose.setOnClickListener(this);
        ivOption.setOnClickListener(this);
        ivShare.setOnClickListener(this);

        llHelping.setOnClickListener(this);
        llFollowers.setOnClickListener(this);
        llFollowing.setOnClickListener(this);
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
            case R.id.ivOption:
                Intent is = new Intent(ProfileActivity.this, OpenOptionsActivity.class);
                startActivity(is);
                break;
            case R.id.ivShare:
                break;
            case R.id.iv_editprofile:
                Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(i);
                break;
            case R.id.ivClose:
                Intent ia = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(ia);
                finish();
                break;
            case R.id.llClose:
                Intent ii = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(ii);
                finish();
                break;
            case R.id.llFollowers:
                Intent fol = new Intent(ProfileActivity.this, FollowerFollowingHelpingActivity.class);
                fol.putExtra("frag1", 0);
                startActivity(fol);

                break;

            case R.id.llfollowing:
                Intent foll = new Intent(ProfileActivity.this, FollowerFollowingHelpingActivity.class);
                foll.putExtra("frag1", 1);
                startActivity(foll);
                break;

            case R.id.llHelping:
                Intent iv = new Intent(ProfileActivity.this, HelpingActivity.class);
                startActivity(iv);
//                openFragment(MyPingFragment.newInstance("", ""));
                break;


        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void ProfileCall() {

        showProgressDialog();
        Call<MyProfileModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).MyProfileModel("Bearer " + token);
        marqueCall.enqueue(new Callback<MyProfileModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<MyProfileModel> call, @NonNull Response<MyProfileModel> response) {
                MyProfileModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {

                    if (object.getData().getComments().size() != 0) {
                        commentArrayLis = object.getData().getComments();
                        myCustomAdapter = new MyCustomAdapter(commentArrayLis);
                        rvReviews.setAdapter(myCustomAdapter);
                    } else {
                        tvNocomments.setVisibility(View.VISIBLE);
                    }


                    tvFollowers.setText(object.getData().getFollowers() + "");
                    tvFollowing.setText(object.getData().getFollowing() + "");
                    tvHelping.setText(object.getData().getHelping() + "");
                    tvReviewNo.setText(object.getData().getReviews() + "");


                    tv_name.setText(object.getData().getUser().getFullname() + "");
                    tv_bio.setText(object.getData().getUser().getUserDetail().getBio() + "");
                    profession.setText(object.getData().getUser().getUserDetail().getProfession().getProfession() + "");
                    try {

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalDate today = LocalDate.now();
                            LocalDate birthday = null;
                            birthday = LocalDate.parse(object.getData().getUser().getUserDetail().getDob());
                            Period p = Period.between(birthday, today);
                            tvAgetxt.setText(p.getYears() + " Years " + p.getMonths() + " Months " + " | ");
                        } else {
                            tvAgetxt.setText(object.getData().getUser().getUserDetail().getDob());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    tv_gender.setText(" | " + object.getData().getUser().getUserDetail().getGender() + "");
                    tv_location.setText(object.getData().getUser().getUserDetail().getCity().getCity() + "");
                    tvEmailId.setText(object.getData().getUser().getEmail() + "");
                    tvMobileNo.setText(object.getData().getUser().getUserDetail().getContact() + "");

                    if (object.getData().getUser().getUserDetail().getPhoto() != null) {
                        Glide.with(ProfileActivity.this)
                                .load(BuildConstants.Main_Image + object.getData().getUser().getUserDetail().getPhoto().replace("public", "storage"))
                                .placeholder(R.drawable.placeholder)
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
            public void onFailure(@NonNull Call<MyProfileModel> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<Comment> moviesList;

        public MyCustomAdapter(ArrayList<Comment> moviesList) {
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


            final Comment datum = moviesList.get(position);
            if (datum != null) {
                holder.tvName.setText(datum.getFullname() + "");
                if (datum.getComment() != null) {
                    holder.tvHeading.setText(datum.getComment() + "");

                } else {
                    holder.tvHeading.setText("");
                }
                holder.tvTime.setText(datum.getTime() + "");
                if (datum.getPhoto() != null) {
                    if (datum.getPhoto().getPath() != null) {
                        Glide.with(ProfileActivity.this)
                                .load(BuildConstants.Main_Image + datum.getPhoto().getPath().replace("public", "storage"))
//                        .centerCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(holder.ivEmployee);
                    }

                }
            }
            holder.rlReply.setVisibility(View.GONE);
            holder.ivHide.setVisibility(View.GONE);

            holder.rvReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialogs = new Dialog(Objects.requireNonNull(ProfileActivity.this));
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
                                Toast.makeText(ProfileActivity.this, "Please Select Reason", Toast.LENGTH_SHORT).show();
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

                                            Toast.makeText(ProfileActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                                        } else {
                                            try {
                                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                Toast.makeText(ProfileActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
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
            if (datum.getLike().booleanValue() == false) {
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


                                ProfileCall();
                                Toast.makeText(ProfileActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(ProfileActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
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

//            holder.tvTotalComment.setText(Comments.get(position) + "");
//            holder.tvTotalView.setText(Views.get(position) + "");
//            holder.tvTotalLikes.setText(datum.getLike()+ "");


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView ivEmployee;
            TextView tvName, tvTime, tvHeading, tvTotalLikes;
            RelativeLayout rlReply, rvReport, rvLikeComment;
            View ivHide;

            public MyViewHolder(View view) {
                super(view);


                ivEmployee = view.findViewById(R.id.ivEmployee);
                tvName = view.findViewById(R.id.tvName);

                tvTime = view.findViewById(R.id.tvTime);
                tvHeading = view.findViewById(R.id.tvComments);
                ivHide = view.findViewById(R.id.ivHide);

                tvTotalLikes = view.findViewById(R.id.tvTotalLikes);
                rlReply = view.findViewById(R.id.rlReply);
                rvReport = view.findViewById(R.id.rvReport);
                rvLikeComment = view.findViewById(R.id.rvLikeComment);


            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ProfileCall();

    }


}