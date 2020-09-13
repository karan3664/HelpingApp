package com.aryupay.helpingapp.ui.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.chats.chatDetails.ChatDetailModel;
import com.aryupay.helpingapp.modal.chats.chatList.ChatListModel;
import com.aryupay.helpingapp.modal.chats.chatList.Datum;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.chats.Model.Chat;
import com.aryupay.helpingapp.ui.chats.fragments.ChatsFragment;
import com.aryupay.helpingapp.ui.chats.fragments.ProfileFragment;
import com.aryupay.helpingapp.ui.chats.fragments.UsersFragment;
import com.aryupay.helpingapp.ui.profile.SearchFollowFollowingActivity;
import com.aryupay.helpingapp.ui.profile.ui.SuggestedFragment;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pusher.pushnotifications.PushNotifications;


import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListActivity extends AppCompatActivity {
    protected ViewDialog viewDialog;
//    RecyclerView rvChatList;
//    private MyCustomAdapter myCustomAdapter;
    LoginModel loginModel;
//    ArrayList<Datum> datumArrayList = new ArrayList<>();
//    private AlertDialog.Builder alertDialogBuilder;
    String token;
    ImageView ivBack;
    private EditText et_search;
    private ImageButton bt_clear;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        loginModel = PrefUtils.getUser(ChatListActivity.this);
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(ChatListActivity.this);
        viewDialog.setCancelable(false);
//        rvChatList = findViewById(R.id.rvChatList);
        ivBack = findViewById(R.id.ivBack);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatListActivity.this);
//        rvChatList.setLayoutManager(layoutManager);
//        rvChatList.setHasFixedSize(true);
//        FollowerList();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);


        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
                        unread++;
                    }
                }

                if (unread == 0) {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
                } else {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "(" + unread + ") Chats");
                }

                viewPagerAdapter.addFragment(new UsersFragment(), "Users");
//                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        // Ctrl + O

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }



/*

    private void SearchList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user", et_search.getText().toString() + "");

        showProgressDialog();
        Call<ChatListModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).search_chat("Bearer " + token, hashMap);
        marqueCall.enqueue(new Callback<ChatListModel>() {
            @Override
            public void onResponse(@NonNull Call<ChatListModel> call, @NonNull Response<ChatListModel> response) {
                ChatListModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    datumArrayList = object.getData();
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    rvChatList.setAdapter(myCustomAdapter);

                } else {

//                    Toast.makeText(ChatListActivity.this, "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatListModel> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }


    private void FollowerList() {

        showProgressDialog();
        Call<ChatListModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ChatListModel("Bearer " + token);
        marqueCall.enqueue(new Callback<ChatListModel>() {
            @Override
            public void onResponse(@NonNull Call<ChatListModel> call, @NonNull Response<ChatListModel> response) {
                ChatListModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    datumArrayList = object.getData();
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    rvChatList.setAdapter(myCustomAdapter);

                } else {

//                    Toast.makeText(ChatListActivity.this, "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatListModel> call, @NonNull Throwable t) {
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);

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


            if (datum.getPhoto() != null) {
                if (datum.getPhoto() != null) {
                    Glide.with(ChatListActivity.this)
                            .load(BuildConstants.Main_Image + datum.getPhoto().replace("public", "storage"))
//                        .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(holder.civProfile);
                }

            } else {
                Glide.with(ChatListActivity.this)
                        .load("")
//                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.civProfile);
            }

            holder.llChatDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Call<ChatDetailModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ChatDetailModel(String.valueOf(datum.getUserId()), "Bearer " + token);
                    marqueCall.enqueue(new Callback<ChatDetailModel>() {
                        @Override
                        public void onResponse(@NonNull Call<ChatDetailModel> call, @NonNull Response<ChatDetailModel> response) {
                            ChatDetailModel object = response.body();
                            Log.e("TAG", "ChatReceive_Response : " + new Gson().toJson(response.body()));

                            if (response.isSuccessful()) {
                                assert object != null;
                                assert response.body() != null;
                                if (response.body().getMessage().equalsIgnoreCase("User is Blocked!")) {
                                    alertDialogBuilder = new AlertDialog.Builder(ChatListActivity.this, R.style.AlertDialogTheme);
                                    alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
                                    alertDialogBuilder.setIcon(R.drawable.eyu);
                                    alertDialogBuilder
                                            .setMessage(object.getMessage() + "")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();

                                } else {
                                    Intent i = new Intent(ChatListActivity.this, ChatDetailsActivity.class);
                                    i.putExtra("name", datum.getName() + "");
                                    i.putExtra("image_path", datum.getPhoto() + "");
                                    i.putExtra("id", datum.getUserId() + "");
                                    Log.e("ChatHas=>", datum.getUserId()  + "");
                                    startActivity(i);
                                }

                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    Toast.makeText(ChatListActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(ChatListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ChatDetailModel> call, @NonNull Throwable t) {
                            t.printStackTrace();
                            Log.e("ChatReceive_Response", t.getMessage() + "");
//                Toast.makeText(ChatDetailsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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


            LinearLayout llChatDetail;

            public MyViewHolder(View view) {
                super(view);


                civProfile = view.findViewById(R.id.civProfile);
                tvName = view.findViewById(R.id.tvName);
                llChatDetail = view.findViewById(R.id.llChatDetail);


            }

        }

    }
*/


    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}