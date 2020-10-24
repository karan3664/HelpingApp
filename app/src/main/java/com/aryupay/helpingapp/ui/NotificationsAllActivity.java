package com.aryupay.helpingapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.notification.Datum;
import com.aryupay.helpingapp.modal.notification.NotificationsModel;

import com.aryupay.helpingapp.ui.fragments.activity.DetailBlogsActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsAllActivity extends AppCompatActivity {
    RecyclerView notificationRecyclerview;
    private MyCustomAdapter myCustomAdapter;

    private ArrayList<Datum> blogArrayList = new ArrayList<>();
    String token;
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    TextView tvNoNotification;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_all);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        ivBack = findViewById(R.id.ivBack);
        tvNoNotification = findViewById(R.id.tvNoNotification);
        notificationRecyclerview = findViewById(R.id.notificationRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notificationRecyclerview.setLayoutManager(layoutManager);
        notificationRecyclerview.setHasFixedSize(true);
        BlogList();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void BlogList() {


        Call<NotificationsModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).NotificationsModel("Bearer " + token);
        marqueCall.enqueue(new Callback<NotificationsModel>() {
            @Override
            public void onResponse(@NonNull Call<NotificationsModel> call, @NonNull Response<NotificationsModel> response) {
                NotificationsModel object = response.body();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {

                    if (object.getData().size() != 0) {
                        blogArrayList = object.getData();
                        myCustomAdapter = new MyCustomAdapter(blogArrayList);
                        notificationRecyclerview.setAdapter(myCustomAdapter);
                    } else {
                        tvNoNotification.setVisibility(View.VISIBLE);
                    }


                } else {
//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationsModel> call, @NonNull Throwable t) {
                t.printStackTrace();

                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<Datum> moviesList;

        public MyCustomAdapter(ArrayList<Datum> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_list, parent, false);

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


            final Datum datum = moviesList.get(position);
            final String BlogID = "";
            holder.tvHeadingNoti.setText(datum.getTitle() + "");
            try {
                JSONObject jsonObject = new JSONObject(datum.getDescription());
                holder.tvName.setText(jsonObject.getString("fullname") + "");
                holder.tvHeading.setText(jsonObject.getString("heading") + "");
                holder.tvSubHeading.setText(jsonObject.getString("description") + "");

                if (jsonObject.get("path") != null) {
                    Glide.with(NotificationsAllActivity.this)
                            .load(BuildConstants.Main_Image + jsonObject.getString("path").toString().replace("public", "storage"))
//                        .centerCrop()
                            .placeholder(R.drawable.place_holder)
                            .into(holder.ivEmployee);
                }

                holder.CVReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(NotificationsAllActivity.this, DetailBlogsActivity.class);
                        try {
                            i.putExtra("blogid", jsonObject.getString("blog_id") + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(i);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


//            Uri uri = Uri.parse(BuildConstants.Main_Image + datum.getThmbnailImage());
//            holder.imageView_Spotlight.setImageURI(uri);


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CircleImageView ivEmployee;
            TextView tvName, catName, tvTime, tvSubHeading, tvHeading, tvHeadingNoti, tvTotalLikes, tvTotalComment, tvLocation;
            RelativeLayout rlCategory;
            ImageView favStar;
            CardView CVReview;

            public MyViewHolder(View view) {
                super(view);


                ivEmployee = view.findViewById(R.id.ivEmployee);
                tvName = view.findViewById(R.id.tvName);
                tvHeadingNoti = view.findViewById(R.id.tvHeadingNoti);

                tvHeading = view.findViewById(R.id.tvHeading);
                tvSubHeading = view.findViewById(R.id.tvSubHeading);

                CVReview = view.findViewById(R.id.CVReview);

            }

        }

    }


    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }


}