package com.aryupay.helpingapp.ui.profile.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment {
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

    public FollowingFragment() {
        // Required empty public constructor
    }
    MyReceiver r;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingFragment newInstance(String param1, String param2) {
        FollowingFragment fragment = new FollowingFragment();
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

    public void refresh() {
        //yout code in refresh.
        FollowerList();
        Log.i("Refresh", "YES");
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
                new IntentFilter("TAG_REFRESH"));
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FollowingFragment.this.refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_following, container, false);
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

//        showProgressDialog();
        Call<FollowersModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).following("Bearer " + token);
        marqueCall.enqueue(new Callback<FollowersModel>() {
            @Override
            public void onResponse(@NonNull Call<FollowersModel> call, @NonNull Response<FollowersModel> response) {
                FollowersModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "Following : " + new Gson().toJson(response.body()));

                if (response.isSuccessful()){
                    datumArrayList = object.getData();
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    rvFollowers.setAdapter(myCustomAdapter);
                    myCustomAdapter.notifyDataSetChanged();
                }
                else {

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


            holder.rlCategory.setBackgroundResource(R.drawable.btn_back_bg);
            holder.rlCategory.setText("Following");

            if (datum.getPhoto() != null) {
                if (datum.getPhoto().getPath() != null) {
                    Glide.with(getContext())
                            .load(BuildConstants.Main_Image + datum.getPhoto().getPath().replace("public", "storage"))
//                        .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(holder.civProfile);
                }
            }

            holder.rlCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent i = new Intent(getContext(), DetailBlogsActivity.class);
//                    i.putExtra("blogid", datum.getId() + "");
//                    startActivity(i);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            FollowerList();
            //do something  //Load or Refresh Data
        }
    }
}