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
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.aryupay.helpingapp.modal.bloglist.Blog;
import com.aryupay.helpingapp.modal.bloglist.BlogListModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.ui.fragments.HomeFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBlogsActivity extends AppCompatActivity implements View.OnClickListener {

    AdapterViewFlipper adapterViewFlipper;
    TextView tvName, catName, tvTime, tvSubHeading, tvHeading, tvTotalView, tvTotalLikes, tvTotalComment, tvLocation;
    RelativeLayout btnComment, btnChat;
    String token, blogid;
    LoginModel loginModel;
    ImageView userProfile, ivLike, ivFav;

    protected ViewDialog viewDialog;
    LinearLayout llDetailContain, llBottom;

    // Comment Section
    RecyclerView rvCommnetList;
    LinearLayout llCommentSection, llAddComment;
    CircleImageView userProfileComment;
    CardView cvOpenComment;
    private MyCustomAdapter myCustomAdapter;
    ArrayList<Datum> datumArrayList = new ArrayList<>();

    EditText edtComment;
    Button btnCommentSave;

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

        //Comment Section

        llCommentSection = findViewById(R.id.llCommentSection);
        llAddComment = findViewById(R.id.llAddComment);
        rvCommnetList = findViewById(R.id.rvCommnetList);
        userProfileComment = findViewById(R.id.userProfileComment);
        cvOpenComment = findViewById(R.id.cvOpenComment);
        edtComment = findViewById(R.id.edtComment);
        btnCommentSave = findViewById(R.id.btnCommentSave);

        LinearLayoutManager layoutManager = new LinearLayoutManager(DetailBlogsActivity.this);
        rvCommnetList.setLayoutManager(layoutManager);
        rvCommnetList.setHasFixedSize(true);

        ivLike.setOnClickListener(this);
        ivFav.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        cvOpenComment.setOnClickListener(this);
        btnCommentSave.setOnClickListener(this);

        BlogDetails();
        CommentsList();
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
//                                .centerCrop()
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
                        Toast.makeText(DetailBlogsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(DetailBlogsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        CommentsList();
                        llCommentSection.setVisibility(View.VISIBLE);
                        llAddComment.setVisibility(View.GONE);
                        Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
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
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });
    }
    public void AddFavBlog() {

        showProgressDialog();
        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).favourite(blogid, "Bearer " + token);
        marqueCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    Toast.makeText(DetailBlogsActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
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
                llDetailContain.setVisibility(View.GONE);
                llBottom.setVisibility(View.GONE);
                llCommentSection.setVisibility(View.VISIBLE);
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
            holder.tvHeading.setText(datum.getComments() + "");
            holder.tvTime.setText(datum.getCreatedAt() + "");

//            holder.tvTotalComment.setText(Comments.get(position) + "");
//            holder.tvTotalView.setText(Views.get(position) + "");
//            holder.tvTotalLikes.setText(datum.get+ "");
            if (datum.getPhoto().getPhoto() != null) {
                Glide.with(DetailBlogsActivity.this)
                        .load(BuildConstants.Main_Image + datum.getPhoto().getPhoto().replace("public", "storage"))
//                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.ivEmployee);
            }

            holder.rlReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llCommentSection.setVisibility(View.GONE);
                    llAddComment.setVisibility(View.VISIBLE);

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
            RelativeLayout rlReply;

            public MyViewHolder(View view) {
                super(view);


                ivEmployee = view.findViewById(R.id.ivEmployee);
                tvName = view.findViewById(R.id.tvName);

                tvTime = view.findViewById(R.id.tvTime);
                tvHeading = view.findViewById(R.id.tvHeading);

                tvTotalLikes = view.findViewById(R.id.tvTotalLikes);
                rlReply = view.findViewById(R.id.rlReply);


            }

        }

    }

}