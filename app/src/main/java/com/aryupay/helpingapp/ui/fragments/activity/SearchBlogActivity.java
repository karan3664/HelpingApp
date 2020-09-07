package com.aryupay.helpingapp.ui.fragments.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.bloglist.Blog;
import com.aryupay.helpingapp.modal.bloglist.BlogListModel;
import com.aryupay.helpingapp.modal.bloglist.Image;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.fragments.HomeFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBlogActivity extends AppCompatActivity {

    RecyclerView searchRecyclerview;
    private MyCustomAdapter myCustomAdapter;

    private ArrayList<Blog> blogArrayList = new ArrayList<>();
    private ArrayList<Integer> Like = new ArrayList<>();
    private ArrayList<Integer> Comments = new ArrayList<>();
    private ArrayList<Integer> Views = new ArrayList<>();
    private ArrayList<Image> Images = new ArrayList<>();
    protected ViewDialog viewDialog;
    private EditText et_search;
    private ImageButton bt_clear;
    String token;

    LoginModel loginModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_blog);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        et_search = (EditText) findViewById(R.id.et_search);
        bt_clear = (ImageButton) findViewById(R.id.bt_clear);
//        productList();

        searchRecyclerview = findViewById(R.id.searchRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        searchRecyclerview.setLayoutManager(layoutManager);
//        searchRecyclerview.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 8), true));
        searchRecyclerview.setHasFixedSize(true);
        searchRecyclerview.setNestedScrollingEnabled(false);
//        Toast.makeText(getContext(), latitude + "   -  " + longitude + "", Toast.LENGTH_SHORT).show();
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
//                productList();
            }
        });


//        productList();
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchAction() {
//        progress_bar.setVisibility(View.VISIBLE);
        showProgressDialog();
        searchRecyclerview.setVisibility(View.VISIBLE);

        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    progress_bar.setVisibility(View.GONE);
                    hideProgressDialog();
                    BlogList();
//                    if (resultProductLists.isEmpty())
//                    {
//                        productList();
//                    }
//                    else {
//                        mProductListCustomAdapter = new ProductListCustomAdapter(resultProductLists);
//                        searchRecyclerview.setAdapter(mProductListCustomAdapter);
//
//                    }

                }
            }, 500);
        } else {
            Toast.makeText(SearchBlogActivity.this, "Please fill search input", Toast.LENGTH_SHORT).show();
        }
    }

    private void BlogList() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("location", "Atmakur,Andhra Pradesh");
        hashMap.put("heading", et_search.getText().toString() + "");


        Log.e("GAYA", hashMap + "");
        Call<BlogListModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).search_blog("Bearer " + token, hashMap);
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
                    searchRecyclerview.setAdapter(myCustomAdapter);

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

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<Blog> moviesList;

        public MyCustomAdapter(ArrayList<Blog> moviesList) {
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

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final Blog datum = moviesList.get(position);


//            Uri uri = Uri.parse(BuildConstants.Main_Image + datum.getThmbnailImage());
//            holder.imageView_Spotlight.setImageURI(uri);

            holder.tvName.setText(datum.getName() + "");
            holder.tvHeading.setText(datum.getHeading() + "");
            holder.tvSubHeading.setText(datum.getDescription() + "");
            holder.tvLocation.setText(datum.getLocation() + "");

            try {
                LocalDate today = LocalDate.now();
                LocalDate birthday = null;
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
            if (datum.getCategory().matches("general")) {
                holder.rlCategory.setBackgroundResource(R.drawable.general_cat_bg);
            } else if (datum.getCategory().matches("urgent")) {
//                holder.rlCategory.setBackgroundColor(R.drawable.urgent_bg);

            } else {

            }

            if (datum.getFav() == true) {
                holder.favStar.setImageResource(R.drawable.favourite_star);
            }
            holder.catName.setText(datum.getCategory() + "");
            holder.tvTotalComment.setText(Comments.get(position) + "");
            holder.tvTotalView.setText(Views.get(position) + "");
            holder.tvTotalLikes.setText(Like.get(position) + "");
            if (Images.get(position) != null) {
                Glide.with(SearchBlogActivity.this)
                        .load(BuildConstants.Main_Image + Images.get(position).getPath().replace("public", "storage"))
//                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.ivEmployee);
            }

            holder.CVReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(SearchBlogActivity.this, DetailBlogsActivity.class);
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