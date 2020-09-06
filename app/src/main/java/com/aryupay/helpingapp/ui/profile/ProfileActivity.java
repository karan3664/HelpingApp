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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.bloglist.Blog;
import com.aryupay.helpingapp.modal.bloglist.BlogListModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.profile.my_profile.Comment;
import com.aryupay.helpingapp.modal.profile.my_profile.MyProfileModel;
import com.aryupay.helpingapp.ui.fragments.ChatFragment;
import com.aryupay.helpingapp.ui.fragments.HomeFragment;
import com.aryupay.helpingapp.ui.fragments.MyPingFragment;
import com.aryupay.helpingapp.ui.fragments.activity.DetailBlogsActivity;
import com.aryupay.helpingapp.ui.profile.ui.FollowersFragment;
import com.aryupay.helpingapp.ui.profile.ui.FollowingFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView ivProfileImage;
    TextView tv_name, tvBiotxt, tv_bio, tvAgetxt, tv_gender, tv_location,
            tvEmailId, tvMobileNo, tvFollowers, tvFollowing, tvHelping, tvReviewNo;
    ImageView iv_editprofile, ivClose, ivShare, ivOption;
    RecyclerView rvReviews;
    LoginModel loginModel;
    String token;
    private MyCustomAdapter myCustomAdapter;
    LinearLayout llFollowers, llFollowing, llHelping;

    ArrayList<Comment> commentArrayLis = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        loginModel = PrefUtils.getUser(ProfileActivity.this);
        token = loginModel.getData().getToken();
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tv_name = findViewById(R.id.tv_name);
        tvBiotxt = findViewById(R.id.tvBiotxt);
        tv_bio = findViewById(R.id.tv_bio);
        tvAgetxt = findViewById(R.id.tvAgetxt);
        tv_gender = findViewById(R.id.tv_gender);
        tv_location = findViewById(R.id.tv_location);
        tvEmailId = findViewById(R.id.tvEmailId);
        tvMobileNo = findViewById(R.id.tvMobileNo);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        tvHelping = findViewById(R.id.tvHelping);
        tvReviewNo = findViewById(R.id.tvReviewNo);
        iv_editprofile = findViewById(R.id.iv_editprofile);
        ivClose = findViewById(R.id.ivClose);
        ivShare = findViewById(R.id.ivShare);
        ivOption = findViewById(R.id.ivOption);
        rvReviews = findViewById(R.id.rvReviews);
        llFollowers = findViewById(R.id.llFollowers);
        llFollowing = findViewById(R.id.llfollowing);
        llHelping = findViewById(R.id.llHelping);

        tv_name.setText(loginModel.getData().getUser().getFullname() + "");
        tv_bio.setText(loginModel.getData().getUser().getUserDetail().getBio() + "");
        try {
            LocalDate today = LocalDate.now();
            LocalDate birthday = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                birthday = LocalDate.parse(loginModel.getData().getUser().getUserDetail().getDob());
                Period p = Period.between(birthday, today);
                tvAgetxt.setText(p.getYears() + " Years " + p.getMonths() + " Months " + ",");
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
        ivOption.setOnClickListener(this);
        ivShare.setOnClickListener(this);

        llHelping.setOnClickListener(this);
        llFollowers.setOnClickListener(this);
        llFollowing.setOnClickListener(this);
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
                onBackPressed();
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
                openFragment(MyPingFragment.newInstance("", ""));
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


        Call<MyProfileModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).MyProfileModel("Bearer " + token);
        marqueCall.enqueue(new Callback<MyProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<MyProfileModel> call, @NonNull Response<MyProfileModel> response) {
                MyProfileModel object = response.body();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    commentArrayLis = object.getData().getComments();

                    tvFollowers.setText(object.getData().getFollowers() + "");
                    tvFollowing.setText(object.getData().getFollowing() + "");
                    tvHelping.setText(object.getData().getHelping() + "");
                    tvReviewNo.setText(object.getData().getReviews() + "");
                    myCustomAdapter = new MyCustomAdapter(commentArrayLis);
                    rvReviews.setAdapter(myCustomAdapter);

                } else {
//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyProfileModel> call, @NonNull Throwable t) {
                t.printStackTrace();

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
            holder.tvName.setText(datum.getFullname() + "");
            holder.tvHeading.setText(datum.getComment() + "");
            holder.tvTime.setText(datum.getTime() + "");

//            holder.tvTotalComment.setText(Comments.get(position) + "");
//            holder.tvTotalView.setText(Views.get(position) + "");
//            holder.tvTotalLikes.setText(datum.getLike()+ "");
            if (datum.getPhoto() != null) {
                Glide.with(ProfileActivity.this)
                        .load(BuildConstants.Main_Image + datum.getPhoto().getPath().replace("public", "storage"))
//                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.ivEmployee);
            }

        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView ivEmployee;
            TextView tvName, tvTime, tvHeading, tvTotalLikes;
            RelativeLayout rlReply;

            public MyViewHolder(View view) {
                super(view);


                ivEmployee = view.findViewById(R.id.ivEmployee);
                tvName = view.findViewById(R.id.tvName);

                tvTime = view.findViewById(R.id.tvTime);
                tvHeading = view.findViewById(R.id.tvComments);

                tvTotalLikes = view.findViewById(R.id.tvTotalLikes);
                rlReply = view.findViewById(R.id.rlReply);


            }

        }

    }
}