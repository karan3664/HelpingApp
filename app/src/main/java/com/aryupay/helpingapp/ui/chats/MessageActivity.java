package com.aryupay.helpingapp.ui.chats;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.bloglist.Message;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.chats.Adapter.MessageAdapter;
import com.aryupay.helpingapp.ui.chats.Model.Chat;
import com.aryupay.helpingapp.ui.chats.Model.User;
import com.aryupay.helpingapp.ui.chats.Notifications.Client;
import com.aryupay.helpingapp.ui.chats.Notifications.Data;
import com.aryupay.helpingapp.ui.chats.Notifications.MyResponse;
import com.aryupay.helpingapp.ui.chats.Notifications.Sender;
import com.aryupay.helpingapp.ui.chats.Notifications.Token;
import com.aryupay.helpingapp.ui.chats.fragments.APIService;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    Button btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    String userid, user_id, token;

    APIService apiService;
    LoginModel loginModel;

    boolean notify = false;
    ImageView ivClose, ivCall, ivOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        loginModel = PrefUtils.getUser(MessageActivity.this);
        token = loginModel.getData().getToken() + "";
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // and this
//                startActivity(new Intent(MessageActivity.this, ChatListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            }
//        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        ivClose = findViewById(R.id.ivClose);
        ivCall = findViewById(R.id.ivCall);
        ivOption = findViewById(R.id.ivOption);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ivOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenOptionDialog();
            }
        });
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        user_id = intent.getStringExtra("user_id");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        HashMap<String, String> hash = new HashMap<>();

        hash.put("to_user", user_id + "");
        hash.put("message", message + "");
        Log.e("ChatHas=>", hash + "");

        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).sendMessage("Bearer " + token, hash);
        marqueCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject object = response.body();


                if (response.isSuccessful()) {


                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.e("ChatSend_Response", t.getMessage() + "");
            }
        });
        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }


    public void OpenOptionDialog() {
        final Dialog dialogs = new Dialog(Objects.requireNonNull(MessageActivity.this));
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogs.setContentView(R.layout.dialog_chat_options);
        dialogs.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogs.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP | Gravity.RIGHT;
        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView tvDeletechat = dialogs.findViewById(R.id.tvDeletechat);
        final TextView tvBlockUser = dialogs.findViewById(R.id.tvBlockUser);
        final TextView tvReportUser = dialogs.findViewById(R.id.tvReportUser);

        tvDeletechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                final Dialog deletChatDialog = new Dialog(Objects.requireNonNull(MessageActivity.this));
                deletChatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                deletChatDialog.setContentView(R.layout.dialog_delete_chat);
                deletChatDialog.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(Objects.requireNonNull(deletChatDialog.getWindow()).getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                deletChatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deletChatDialog.show();
                final Button btnCancel = (Button) deletChatDialog.findViewById(R.id.btnCancel);
                final Button btnSend = (Button) deletChatDialog.findViewById(R.id.btnOk);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletChatDialog.dismiss();
                    }
                });

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        Chat chat = snapshot.getValue(Chat.class);
//                        if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
//                                chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
//                            mchat.add(chat);
//                        }

                       /* DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        rootRef.child("Chats").child(userid)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });*/
                        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).delete_chat(user_id, "Bearer " + token);
                        marqueCall.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                JsonObject object = response.body();
                                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                if (object != null) {
//                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//                                    rootRef.child("Chats").child(mchat.get(0).getReceiver()).child(mchat.get(0).getSender()).child(userid).removeValue();
                                    Toast.makeText(MessageActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
//                                    onBackPressed();

                                } else {

//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                t.printStackTrace();

                                Log.e("ChatV_Response", t.getMessage() + "");
                            }
                        });
                    }
                });

            }
        });

        tvBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                final Dialog blockChatDialog = new Dialog(Objects.requireNonNull(MessageActivity.this));
                blockChatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                blockChatDialog.setContentView(R.layout.dialog_block_chat_user);
                blockChatDialog.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(Objects.requireNonNull(blockChatDialog.getWindow()).getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                blockChatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final Button btnCancel = (Button) blockChatDialog.findViewById(R.id.btnCancel);
                final Button btnSend = (Button) blockChatDialog.findViewById(R.id.btnBlock);
                blockChatDialog.show();
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        blockChatDialog.dismiss();
                    }
                });

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).block_user(user_id, "Bearer " + token);
                        marqueCall.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                JsonObject object = response.body();

                                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                if (object != null) {

                                    Toast.makeText(MessageActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();


                                } else {

//                    Toast.makeText(getContext(), "No Chat Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                t.printStackTrace();

                                Log.e("ChatV_Response", t.getMessage() + "");
                            }
                        });
                    }
                });

            }
        });
        tvReportUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                final Dialog dialog = new Dialog(Objects.requireNonNull(MessageActivity.this));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.dialog_report_user);
                dialog.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final RadioGroup radioSexGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReportUser);

                final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                final Button btnSend = (Button) dialog.findViewById(R.id.btnSend);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // get selected radio button from radioGroup
                        int selectedId = radioSexGroup.getCheckedRadioButtonId();

                        // find the radiobutton by returned id
                        RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);


                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("report", radioButton.getText().toString() + "");
                        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).report_user(user_id, "Bearer " + token, hashMap);
                        marqueCall.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                JsonObject object = response.body();

                                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                if (response.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(MessageActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(MessageActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                                t.printStackTrace();

                                Log.e("ChatV_Response", t.getMessage() + "");
                            }
                        });
                    }
                });
                dialog.show();
                dialog.getWindow().setAttributes(lp);

            }
        });

        dialogs.show();
        dialogs.getWindow().setAttributes(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }
}