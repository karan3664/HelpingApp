package com.aryupay.helpingapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.bloglist.Image;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.myping.Datum;
import com.aryupay.helpingapp.modal.myping.MyPingBlogModel;
import com.aryupay.helpingapp.ui.HomeActivity;
import com.aryupay.helpingapp.ui.fragments.activity.DetailBlogsActivity;
import com.aryupay.helpingapp.ui.fragments.activity.SearchMyPingActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPingFragment extends Fragment implements View.OnClickListener {

    RecyclerView rvBlogList;

    private MyCustomAdapter myCustomAdapter;
    private ArrayList<Datum> blogArrayList = new ArrayList<>();
    private ArrayList<Integer> Like = new ArrayList<>();
    private ArrayList<Integer> Comments = new ArrayList<>();
    private ArrayList<Integer> Views = new ArrayList<>();
    private ArrayList<Image> Images = new ArrayList<>();
    LoginModel loginModel;
    protected ViewDialog viewDialog;
    String token;
    Chip chipAll, chipUrgent, chipInformation, chipGeneral, chipFav, chipSearch;
    EditText edtSearch;
    ImageView ivBack;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyPingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPingFragment newInstance(String param1, String param2) {
        MyPingFragment fragment = new MyPingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_ping, container, false);
        loginModel = PrefUtils.getUser(getContext());
        token = loginModel.getData().getToken() + "";
        rvBlogList = rootView.findViewById(R.id.rvBlogList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvBlogList.setLayoutManager(layoutManager);
        rvBlogList.setHasFixedSize(true);
        viewDialog = new ViewDialog(getContext());
        viewDialog.setCancelable(false);
        chipAll = rootView.findViewById(R.id.chipAll);
        chipUrgent = rootView.findViewById(R.id.chipUrgent);
        chipInformation = rootView.findViewById(R.id.chipInformation);
        chipGeneral = rootView.findViewById(R.id.chipGeneral);
        chipFav = rootView.findViewById(R.id.chipFav);
        edtSearch = rootView.findViewById(R.id.edtSearch);
        ivBack = rootView.findViewById(R.id.ivBack);


        chipAll.setOnClickListener(this);
        chipUrgent.setOnClickListener(this);
        chipInformation.setOnClickListener(this);
        chipGeneral.setOnClickListener(this);
        chipFav.setOnClickListener(this);
        edtSearch.setOnClickListener(this);
        BlogList();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), HomeActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        return rootView;
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

    private void BlogList() {

        showProgressDialog();
        Call<MyPingBlogModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).MyPingBlogModel("Bearer " + token);
        marqueCall.enqueue(new Callback<MyPingBlogModel>() {
            @Override
            public void onResponse(@NonNull Call<MyPingBlogModel> call, @NonNull Response<MyPingBlogModel> response) {
                MyPingBlogModel object = response.body();
                hideProgressDialog();
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
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    private void CategoryBlogList(String category) {
        showProgressDialog();
        Call<MyPingBlogModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).MyPingBlogModelCategory(category, "Bearer " + token);
        marqueCall.enqueue(new Callback<MyPingBlogModel>() {
            @Override
            public void onResponse(@NonNull Call<MyPingBlogModel> call, @NonNull Response<MyPingBlogModel> response) {
                MyPingBlogModel object = response.body();
                hideProgressDialog();
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
                hideProgressDialog();
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
//                chipFav.setTextColor(R.color.white_color);
                CategoryBlogList("fav");
                break;
            case R.id.edtSearch:
                Intent i = new Intent(getContext(), SearchMyPingActivity.class);
                startActivity(i);
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
            if (datum.getFav() == true) {
                holder.favStar.setVisibility(View.VISIBLE);
                holder.unfavStar.setVisibility(View.GONE);
//                holder.favStar.setImageResource(R.drawable.favourite_star);
            }
            holder.tvName.setText(datum.getName() + "");
            holder.tvHeading.setText(datum.getHeading() + "");
            holder.tvSubHeading.setText(datum.getDescription() + "");
            holder.tvLocation.setText(datum.getLocation() + "");
            holder.tvTime.setText(datum.getTime() + "");
            if (datum.getCategory().matches("information")) {
                holder.rlCategory.setBackgroundResource(R.drawable.information_cat_bg);
            } else if (datum.getCategory().matches("urgent")) {
                holder.rlCategory.setBackgroundResource(R.drawable.urgent_cat_bg);

            } else {

            }

            holder.favStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.favStar.setVisibility(View.GONE);
                    holder.unfavStar.setVisibility(View.VISIBLE);
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).favourite(datum.getId() + "", "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {
//                                BlogDetails();
                                Toast.makeText(getContext(), response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(getContext(), jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
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
            holder.unfavStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.favStar.setVisibility(View.VISIBLE);
                    holder.unfavStar.setVisibility(View.GONE);
                    showProgressDialog();
                    Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).favourite(datum.getId() + "", "Bearer " + token);
                    marqueCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (response.isSuccessful()) {
//                                BlogDetails();
                                Toast.makeText(getContext(), response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(getContext(), jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
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
            holder.catName.setText(datum.getCategory() + "");
            holder.tvTotalComment.setText(datum.getComments() + "");
            holder.tvTotalView.setText(datum.getViews() + "");
            holder.tvTotalLikes.setText(datum.getLikes() + "");
            if (datum.getImages() != null) {
                Glide.with(getContext())
                        .load(BuildConstants.Main_Image + datum.getImages().getPath().replace("public", "storage"))
//                        .centerCrop()
                        .placeholder(R.drawable.place_holder)
                        .into(holder.ivEmployee);
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
            ImageView favStar, unfavStar;
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
                unfavStar = view.findViewById(R.id.unfavStar);
            }

        }

    }
}
