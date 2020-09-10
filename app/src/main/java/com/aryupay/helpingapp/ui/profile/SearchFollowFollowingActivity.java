package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.bloglist.Blog;
import com.aryupay.helpingapp.modal.bloglist.Image;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.profile.followers.Datum;
import com.aryupay.helpingapp.modal.profile.followers.FollowersModel;
import com.aryupay.helpingapp.ui.fragments.activity.SearchBlogActivity;
import com.aryupay.helpingapp.ui.profile.ui.FollowersFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFollowFollowingActivity extends AppCompatActivity {
    RecyclerView searchRecyclerview;
    private MyCustomAdapter myCustomAdapter;
    ArrayList<Datum> datumArrayList = new ArrayList<>();

    protected ViewDialog viewDialog;
    private EditText et_search;
    private ImageButton bt_clear;
    String token;
    ImageView ivBack;
    LoginModel loginModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_follow_following);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        et_search = (EditText) findViewById(R.id.et_search);
        bt_clear = (ImageButton) findViewById(R.id.bt_clear);
        ivBack = findViewById(R.id.ivBack);
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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                    FollowerList();
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
            Toast.makeText(SearchFollowFollowingActivity.this, "Please fill search input", Toast.LENGTH_SHORT).show();
        }
    }


    private void FollowerList() {
        HashMap<String, String> hashmap = new HashMap<>();
        hashmap.put("user", et_search.getText().toString() + "");

        showProgressDialog();
        Call<FollowersModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).search_user("Bearer " + token, hashmap);
        marqueCall.enqueue(new Callback<FollowersModel>() {
            @Override
            public void onResponse(@NonNull Call<FollowersModel> call, @NonNull Response<FollowersModel> response) {
                FollowersModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    datumArrayList = object.getData();
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    searchRecyclerview.setAdapter(myCustomAdapter);

                } else {

//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FollowersModel> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_following_list, parent, false);

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

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {


            final Datum datum = moviesList.get(position);


//            Uri uri = Uri.parse(BuildConstants.Main_Image + datum.getThmbnailImage());
//            holder.imageView_Spotlight.setImageURI(uri);

            holder.tvName.setText(datum.getName() + "");

            if (datum.getFollow() == false) {
                holder.rlCategory.setBackgroundResource(R.drawable.btn_follow_bg);
                holder.rlCategory.setText("Follow");
            } else {
                holder.rlCategory.setBackgroundResource(R.drawable.btn_back_bg);
                holder.rlCategory.setText("Following");
            }

            if (datum.getPhoto() != null) {
                if (datum.getPhoto().getPath()!= null){
                    Glide.with(SearchFollowFollowingActivity.this)
                            .load(BuildConstants.Main_Image + datum.getPhoto().getPath().replace("public", "storage"))
//                        .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(holder.civProfile);
                }

            }

            holder.rlCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).followunfollo(datum.getId() + "", "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (object != null) {

                                Toast.makeText(SearchFollowFollowingActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                                FollowerList();

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
        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CircleImageView civProfile;
            TextView tvName;
            Button rlCategory;


            public MyViewHolder(View view) {
                super(view);


                civProfile = view.findViewById(R.id.civProfile);
                tvName = view.findViewById(R.id.tvName);
                rlCategory = view.findViewById(R.id.btnFollow);


            }

        }

    }


}