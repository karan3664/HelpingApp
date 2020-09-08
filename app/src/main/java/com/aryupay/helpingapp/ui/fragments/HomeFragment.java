package com.aryupay.helpingapp.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.bloglist.Blog;
import com.aryupay.helpingapp.modal.bloglist.BlogListModel;
import com.aryupay.helpingapp.modal.bloglist.Image;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.NotificationsAllActivity;
import com.aryupay.helpingapp.ui.fragments.activity.DetailBlogsActivity;
import com.aryupay.helpingapp.ui.fragments.activity.SearchBlogActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements View.OnClickListener {
    RecyclerView rvBlogList;

    private MyCustomAdapter myCustomAdapter;
    private ArrayList<Blog> blogArrayList = new ArrayList<>();
    private ArrayList<Integer> Like = new ArrayList<>();
    private ArrayList<Integer> Comments = new ArrayList<>();
    private ArrayList<Integer> Views = new ArrayList<>();
    private ArrayList<Image> Images = new ArrayList<>();
    LoginModel loginModel;
    SharedPreferences preferences;
    String token, location;
    Chip chipAll, chipUrgent, chipInformation, chipGeneral, chipFav, chipSearch;
    TextView tvLoc;
    EditText edtSearch;
    ImageView ivNotification;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        loginModel = PrefUtils.getUser(getContext());
        token = loginModel.getData().getToken() + "";
        preferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        location = preferences.getString("location", "");
        rvBlogList = rootView.findViewById(R.id.rvBlogList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvBlogList.setLayoutManager(layoutManager);
        rvBlogList.setHasFixedSize(true);

        ivNotification = rootView.findViewById(R.id.ivNotification);
        chipAll = rootView.findViewById(R.id.chipAll);
        chipUrgent = rootView.findViewById(R.id.chipUrgent);
        chipInformation = rootView.findViewById(R.id.chipInformation);
        chipGeneral = rootView.findViewById(R.id.chipGeneral);
        chipFav = rootView.findViewById(R.id.chipFav);
        chipSearch = rootView.findViewById(R.id.chipSearch);
        tvLoc = rootView.findViewById(R.id.tvLoc);
        edtSearch = rootView.findViewById(R.id.edtSearch);
        tvLoc.setText(location);
        chipAll.setOnClickListener(this);
        chipUrgent.setOnClickListener(this);
        chipInformation.setOnClickListener(this);
        chipGeneral.setOnClickListener(this);
        chipFav.setOnClickListener(this);
        edtSearch.setOnClickListener(this);
        ivNotification.setOnClickListener(this);

        BlogList();
        return rootView;
    }

    private void BlogList() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("location", "Atmakur,Andhra Pradesh");

        Log.e("GAYA", hashMap + "");
        Call<BlogListModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).BlogListModel("Bearer " + token, hashMap);
        marqueCall.enqueue(new Callback<BlogListModel>() {
            @Override
            public void onResponse(@NonNull Call<BlogListModel> call, @NonNull Response<BlogListModel> response) {
                BlogListModel object = response.body();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    blogArrayList = object.getMessage().getBlog();
                    Like = object.getMessage().getLikes();
                    Comments = object.getMessage().getComments();
                    Views = object.getMessage().getViews();
                    Images = object.getMessage().getImages();

                    myCustomAdapter = new MyCustomAdapter(blogArrayList);
                    rvBlogList.setAdapter(myCustomAdapter);

                } else {
//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BlogListModel> call, @NonNull Throwable t) {
                t.printStackTrace();

                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    private void CategoryBlogList(String category) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("location", "Atmakur,Andhra Pradesh");


        Call<BlogListModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).CategoryBlog(category, "Bearer " + token, hashMap);
        marqueCall.enqueue(new Callback<BlogListModel>() {
            @Override
            public void onResponse(@NonNull Call<BlogListModel> call, @NonNull Response<BlogListModel> response) {
                BlogListModel object = response.body();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    blogArrayList = object.getMessage().getBlog();
                    Like = object.getMessage().getLikes();
                    Comments = object.getMessage().getComments();
                    Views = object.getMessage().getViews();
                    Images = object.getMessage().getImages();

                    myCustomAdapter = new MyCustomAdapter(blogArrayList);
                    rvBlogList.setAdapter(myCustomAdapter);
                    myCustomAdapter.notifyDataSetChanged();

                } else {
//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BlogListModel> call, @NonNull Throwable t) {
                t.printStackTrace();

                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chipAll:
                chipSearch.setText("ALL");
                chipSearch.setChipIconEnabled(false);
                chipSearch.setChipBackgroundColorResource(R.color.sky_blue_color);
                chipAll.setChipBackgroundColorResource(R.color.sky_blue_color);
                chipUrgent.setChipBackgroundColorResource(R.color.dark_grey);
                chipInformation.setChipBackgroundColorResource(R.color.dark_grey);
                chipGeneral.setChipBackgroundColorResource(R.color.dark_grey);
                chipFav.setChipBackgroundColorResource(R.color.dark_grey);
//                CategoryBlogList("all");
                BlogList();
                break;
            case R.id.chipUrgent:
                chipSearch.setText("URGENT");
                chipSearch.setChipIconEnabled(false);
                chipSearch.setChipBackgroundColorResource(R.color.colorPrimary);
                chipAll.setChipBackgroundColorResource(R.color.dark_grey);
                chipUrgent.setChipBackgroundColorResource(R.color.colorPrimary);
                chipInformation.setChipBackgroundColorResource(R.color.dark_grey);
                chipGeneral.setChipBackgroundColorResource(R.color.dark_grey);
                chipFav.setChipBackgroundColorResource(R.color.dark_grey);
                CategoryBlogList("urgent");

                break;
            case R.id.chipInformation:
                chipSearch.setText("INFORMATION");
                chipSearch.setChipIconEnabled(false);
                chipSearch.setChipBackgroundColorResource(R.color.pink_color);
                chipAll.setChipBackgroundColorResource(R.color.dark_grey);
                chipUrgent.setChipBackgroundColorResource(R.color.dark_grey);
                chipInformation.setChipBackgroundColorResource(R.color.pink_color);
                chipGeneral.setChipBackgroundColorResource(R.color.dark_grey);
                chipFav.setChipBackgroundColorResource(R.color.dark_grey);
                CategoryBlogList("information");
                break;
            case R.id.chipGeneral:
                chipSearch.setText("GENERAL");
                chipSearch.setChipIconEnabled(false);
                chipSearch.setChipBackgroundColorResource(R.color.green_color);
                chipAll.setChipBackgroundColorResource(R.color.dark_grey);
                chipUrgent.setChipBackgroundColorResource(R.color.dark_grey);
                chipInformation.setChipBackgroundColorResource(R.color.dark_grey);
                chipGeneral.setChipBackgroundColorResource(R.color.green_color);
                chipFav.setChipBackgroundColorResource(R.color.dark_grey);
                CategoryBlogList("general");
                break;
            case R.id.chipFav:
                chipSearch.setText("FAV");
                chipSearch.setChipIconEnabled(true);
                chipSearch.setChipBackgroundColorResource(R.color.phone_login_color);
                chipAll.setChipBackgroundColorResource(R.color.dark_grey);
                chipUrgent.setChipBackgroundColorResource(R.color.dark_grey);
                chipInformation.setChipBackgroundColorResource(R.color.dark_grey);
                chipGeneral.setChipBackgroundColorResource(R.color.dark_grey);
                chipFav.setChipBackgroundColorResource(R.color.phone_login_color);
                CategoryBlogList("fav");
                break;

            case R.id.edtSearch:
                Intent i = new Intent(getContext(), SearchBlogActivity.class);
                startActivity(i);
                break;
            case R.id.ivNotification:
                Intent ivNotification = new Intent(getContext(), NotificationsAllActivity.class);
                startActivity(ivNotification);
                break;
        }
    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<Blog> moviesList;

        public MyCustomAdapter(ArrayList<Blog> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog_list, parent, false);

            return new MyCustomAdapter.MyViewHolder(itemView);
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
        public void onBindViewHolder(MyCustomAdapter.MyViewHolder holder, final int position) {


            final Blog datum = moviesList.get(position);


//            Uri uri = Uri.parse(BuildConstants.Main_Image + datum.getThmbnailImage());
//            holder.imageView_Spotlight.setImageURI(uri);


            if (datum.getFav() == true) {
                holder.favStar.setImageResource(R.drawable.favourite_star);
            }
            try {
                holder.tvName.setText(datum.getName() + "");
                holder.tvHeading.setText(datum.getHeading() + "");
                holder.tvSubHeading.setText(datum.getDescription() + "");
                holder.tvLocation.setText(datum.getLocation() + "");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    birthday = LocalDate.parse(datum.getTime() + "");
//                    Period p = Period.between(birthday, today);

                    LocalDate start = LocalDate.parse(datum.getTime());
                    LocalDate end = LocalDate.now();

                    System.out.println(ChronoUnit.DAYS.between(start, end));

                    holder.tvTime.setText(ChronoUnit.DAYS.between(start, end) + "");
                }



            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.catName.setText(datum.getCategory() + "");
            holder.tvTotalComment.setText(Comments.get(position) + "");
            holder.tvTotalView.setText(Views.get(position) + "");
            holder.tvTotalLikes.setText(Like.get(position) + "");
            if (Images.get(position) != null) {
                Glide.with(getContext())
                        .load(BuildConstants.Main_Image + Images.get(position).getPath().replace("public", "storage"))
//                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.ivEmployee);
            }
            if (datum.getCategory().matches("general")) {
                holder.rlCategory.setBackgroundResource(R.drawable.general_cat_bg);
            } else if (datum.getCategory().matches("urgent")) {
//                holder.rlCategory.setBackgroundColor(R.drawable.urgent_bg);

            } else {

            }

            holder.CVReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), DetailBlogsActivity.class);
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

    @Override
    public void onStart() {
        super.onStart();
//        BlogList();
    }
}