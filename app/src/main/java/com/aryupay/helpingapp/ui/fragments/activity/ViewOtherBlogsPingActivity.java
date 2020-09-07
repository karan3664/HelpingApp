package com.aryupay.helpingapp.ui.fragments.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.bloglist.Image;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.myping.Datum;
import com.aryupay.helpingapp.modal.myping.MyPingBlogModel;
import com.aryupay.helpingapp.ui.fragments.MyPingFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOtherBlogsPingActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rvBlogList;

    private MyCustomAdapter myCustomAdapter;
    private ArrayList<Datum> blogArrayList = new ArrayList<>();
    private ArrayList<Integer> Like = new ArrayList<>();
    private ArrayList<Integer> Comments = new ArrayList<>();
    private ArrayList<Integer> Views = new ArrayList<>();
    private ArrayList<Image> Images = new ArrayList<>();
    LoginModel loginModel;
    String token, userid, name;
    Chip chipAll, chipUrgent, chipInformation, chipGeneral, chipFav, chipSearch;
    TextView tvMyPing;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_blogs_ping);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken() + "";
        rvBlogList = findViewById(R.id.rvBlogList);
        tvMyPing = findViewById(R.id.tvMyPing);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvBlogList.setLayoutManager(layoutManager);
        rvBlogList.setHasFixedSize(true);

        chipAll = findViewById(R.id.chipAll);
        chipUrgent = findViewById(R.id.chipUrgent);
        chipInformation = findViewById(R.id.chipInformation);
        chipGeneral = findViewById(R.id.chipGeneral);
        chipFav = findViewById(R.id.chipFav);
        ivBack = findViewById(R.id.ivBack);


        chipAll.setOnClickListener(this);
        chipUrgent.setOnClickListener(this);
        chipInformation.setOnClickListener(this);
        chipGeneral.setOnClickListener(this);
        chipFav.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        Intent i = getIntent();
        if (i != null) {


            userid = i.getStringExtra("userid");
            name = i.getStringExtra("name");
            tvMyPing.setText(name);

            BlogList();
        }
    }

    private void BlogList() {


        Call<MyPingBlogModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).view_other_blog(userid + "", "Bearer " + token);
        marqueCall.enqueue(new Callback<MyPingBlogModel>() {
            @Override
            public void onResponse(@NonNull Call<MyPingBlogModel> call, @NonNull Response<MyPingBlogModel> response) {
                MyPingBlogModel object = response.body();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    blogArrayList = object.getData();

                    myCustomAdapter = new MyCustomAdapter(blogArrayList);
                    rvBlogList.setAdapter(myCustomAdapter);

                } else {
//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyPingBlogModel> call, @NonNull Throwable t) {
                t.printStackTrace();

                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }


    private void CategoryBlogList(String category) {

        Call<MyPingBlogModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).view_other_blogC(userid + "", category, "Bearer " + token);
        marqueCall.enqueue(new Callback<MyPingBlogModel>() {
            @Override
            public void onResponse(@NonNull Call<MyPingBlogModel> call, @NonNull Response<MyPingBlogModel> response) {
                MyPingBlogModel object = response.body();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    blogArrayList = object.getData();
                    myCustomAdapter = new MyCustomAdapter(blogArrayList);
                    rvBlogList.setAdapter(myCustomAdapter);
                    myCustomAdapter.notifyDataSetChanged();

                } else {
//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyPingBlogModel> call, @NonNull Throwable t) {
                t.printStackTrace();

                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chipAll:

                chipAll.setChipBackgroundColorResource(R.color.sky_blue_color);
                chipUrgent.setChipBackgroundColorResource(R.color.dark_grey);
                chipInformation.setChipBackgroundColorResource(R.color.dark_grey);
                chipGeneral.setChipBackgroundColorResource(R.color.dark_grey);
                chipFav.setChipBackgroundColorResource(R.color.dark_grey);
//                CategoryBlogList("all");
                BlogList();
                break;
            case R.id.chipUrgent:

                chipAll.setChipBackgroundColorResource(R.color.dark_grey);
                chipUrgent.setChipBackgroundColorResource(R.color.colorPrimary);
                chipInformation.setChipBackgroundColorResource(R.color.dark_grey);
                chipGeneral.setChipBackgroundColorResource(R.color.dark_grey);
                chipFav.setChipBackgroundColorResource(R.color.dark_grey);
                CategoryBlogList("urgent");

                break;
            case R.id.chipInformation:

                chipAll.setChipBackgroundColorResource(R.color.dark_grey);
                chipUrgent.setChipBackgroundColorResource(R.color.dark_grey);
                chipInformation.setChipBackgroundColorResource(R.color.pink_color);
                chipGeneral.setChipBackgroundColorResource(R.color.dark_grey);
                chipFav.setChipBackgroundColorResource(R.color.dark_grey);
                CategoryBlogList("information");
                break;
            case R.id.chipGeneral:

                chipAll.setChipBackgroundColorResource(R.color.dark_grey);
                chipUrgent.setChipBackgroundColorResource(R.color.dark_grey);
                chipInformation.setChipBackgroundColorResource(R.color.dark_grey);
                chipGeneral.setChipBackgroundColorResource(R.color.green_color);
                chipFav.setChipBackgroundColorResource(R.color.dark_grey);
                CategoryBlogList("general");
                break;
            case R.id.chipFav:

                chipAll.setChipBackgroundColorResource(R.color.dark_grey);
                chipUrgent.setChipBackgroundColorResource(R.color.dark_grey);
                chipInformation.setChipBackgroundColorResource(R.color.dark_grey);
                chipGeneral.setChipBackgroundColorResource(R.color.dark_grey);
                chipFav.setChipBackgroundColorResource(R.color.phone_login_color);
                CategoryBlogList("fav");
                break;
            case R.id.ivBack:
                onBackPressed();
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog_list, parent, false);

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

            holder.tvName.setText(datum.getName() + "");
            holder.tvHeading.setText(datum.getHeading() + "");
            holder.tvSubHeading.setText(datum.getDescription() + "");
            holder.tvLocation.setText(datum.getLocation() + "");
            holder.tvTime.setText(datum.getTime() + "");
            if (datum.getCategory().matches("general")) {
                holder.rlCategory.setBackgroundResource(R.drawable.general_cat_bg);
            } else if (datum.getCategory().matches("urgent")) {
//                holder.rlCategory.setBackgroundColor(R.drawable.urgent_bg);

            } else {

            }
            holder.catName.setText(datum.getCategory() + "");
            holder.tvTotalComment.setText(datum.getComments() + "");
            holder.tvTotalView.setText(datum.getViews() + "");
            holder.tvTotalLikes.setText(datum.getLikes() + "");
            if (datum.getImages() != null) {
                Glide.with(ViewOtherBlogsPingActivity.this)
                        .load(BuildConstants.Main_Image + datum.getImages().getPath().replace("public", "storage"))
//                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.ivEmployee);
            }

            holder.CVReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ViewOtherBlogsPingActivity.this, DetailBlogsActivity.class);
                    i.putExtra("blogid", datum.getId() + "");
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CircleImageView ivEmployee;
            TextView tvName, catName, tvTime, tvSubHeading, tvHeading, tvTotalView, tvTotalLikes, tvTotalComment, tvLocation;
            RelativeLayout rlCategory;
            ImageView favStar;
            CardView CVReview;

            public MyViewHolder(View view) {
                super(view);


                ivEmployee = view.findViewById(R.id.ivEmployee);
                tvName = view.findViewById(R.id.tvName);
                catName = view.findViewById(R.id.catName);
                tvTime = view.findViewById(R.id.tvTime);
                tvHeading = view.findViewById(R.id.tvHeading);
                tvSubHeading = view.findViewById(R.id.tvSubHeading);
                tvTotalView = view.findViewById(R.id.tvTotalView);
                tvTotalLikes = view.findViewById(R.id.tvTotalLikes);
                tvTotalComment = view.findViewById(R.id.tvTotalComment);
                tvLocation = view.findViewById(R.id.tvLocation);
                rlCategory = view.findViewById(R.id.rlCategory);
                favStar = view.findViewById(R.id.favStar);
                CVReview = view.findViewById(R.id.CVReview);

            }

        }

    }
}