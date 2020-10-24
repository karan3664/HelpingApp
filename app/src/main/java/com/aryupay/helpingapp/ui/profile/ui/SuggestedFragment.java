package com.aryupay.helpingapp.ui.profile.ui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.profile.followers.Datum;
import com.aryupay.helpingapp.modal.profile.followers.FollowersModel;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SuggestedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuggestedFragment extends Fragment {
    protected ViewDialog viewDialog;
    RecyclerView rvFollowers;
    private MyCustomAdapter myCustomAdapter;
    LoginModel loginModel;
    ArrayList<Datum> datumArrayList = new ArrayList<>();

    String token;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SuggestedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuggestedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SuggestedFragment newInstance(String param1, String param2) {
        SuggestedFragment fragment = new SuggestedFragment();
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


        View rootView = inflater.inflate(R.layout.fragment_suggested, container, false);
        loginModel = PrefUtils.getUser(getContext());
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(getContext());
        viewDialog.setCancelable(false);
        rvFollowers = rootView.findViewById(R.id.rvFollowers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvFollowers.setLayoutManager(layoutManager);
        rvFollowers.setHasFixedSize(true);
        FollowerList();
        return rootView;
    }

    private void FollowerList() {

        showProgressDialog();
        Call<FollowersModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).suggested("Bearer " + token);
        marqueCall.enqueue(new Callback<FollowersModel>() {
            @Override
            public void onResponse(@NonNull Call<FollowersModel> call, @NonNull Response<FollowersModel> response) {
                FollowersModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    datumArrayList = object.getData();
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    rvFollowers.setAdapter(myCustomAdapter);

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

        @SuppressLint("SetTextI18n")
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
                if (datum.getPhoto().getPath() != null) {
                    Glide.with(getContext())
                            .load(BuildConstants.Main_Image + datum.getPhoto().getPath().replace("public", "storage"))
//                        .centerCrop()
                            .placeholder(R.drawable.place_holder)
                            .into(holder.civProfile);
                }

            } else {
                Glide.with(getContext())
                        .load("")
//                        .centerCrop()
                        .placeholder(R.drawable.place_holder)
                        .into(holder.civProfile);
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

                                Toast.makeText(getContext(), response.body().get("message") + "", Toast.LENGTH_SHORT).show();
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


    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}