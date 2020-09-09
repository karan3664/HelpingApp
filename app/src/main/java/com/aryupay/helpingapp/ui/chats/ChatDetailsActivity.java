package com.aryupay.helpingapp.ui.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.chats.chatDetails.ChatDetailModel;
import com.aryupay.helpingapp.modal.chats.chatDetails.Datum;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.Tools;
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

public class ChatDetailsActivity extends AppCompatActivity {

    TextView userChatName;
    CircleImageView ivProfile;
    ImageView ivClose, ivCall, ivOption;

    String name, image_path;
    Button btnCommentSave;
    EditText edtComment;

    private RecyclerView recycler_view;

    private final int CHAT_ME = 100;
    private final int CHAT_YOU = 200;
    private ActionBar actionBar;

    String id = "", lableusername = "", product_url = "", type = "";
    LoginModel loginModel;

    private MyCustomAdapter myCustomAdapter;
    ArrayList<Datum> datumArrayList = new ArrayList<>();
    private Handler mHandler;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        loginModel = PrefUtils.getUser(ChatDetailsActivity.this);
        token = loginModel.getData().getToken();
        userChatName = findViewById(R.id.userChatName);
        ivProfile = findViewById(R.id.ivProfile);
        ivClose = findViewById(R.id.ivClose);
        ivCall = findViewById(R.id.ivCall);
        ivOption = findViewById(R.id.ivOption);
        btnCommentSave = findViewById(R.id.btnCommentSave);
        edtComment = findViewById(R.id.edtComment);
        mHandler = new Handler();
        Intent i = getIntent();
        if (i != null) {
            name = i.getStringExtra("name");
            image_path = i.getStringExtra("image_path");
            id = i.getStringExtra("id");

            userChatName.setText(name);
            if (image_path != null) {
                Glide.with(ChatDetailsActivity.this)
                        .load(BuildConstants.Main_Image + image_path.replace("public", "storage"))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(ivProfile);

            }

        }

        btnCommentSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendChat();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        recycler_view = findViewById(R.id.recyclerViewReplyTickets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);
        layoutManager.setStackFromEnd(true);
//        adapter = new AdapterChatWhatsapp(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 30; i++) {
                    final int currentProgressCount = i;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                  Update the value background thread to UI thread
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ChatList();
//                            myCustomAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();


    }

    public void SendMsg() {

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("to_user", id + "");
        hashMap.put("message", edtComment.getText().toString() + "");


        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).sendMessage("Bearer " + token, hashMap);
        marqueCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject object = response.body();


                if (response.isSuccessful()) {

                    Log.e("TAG", "ChatSend_Response : " + new Gson().toJson(response.body()));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            SendMsg();

                            ChatList();
//                            myCustomAdapter.insertItem(new ResultReceive(myCustomAdapter.getItemCount(), et_content.getText().toString() + "", false, myCustomAdapter.getItemCount() % 5 == 0, Tools.getFormattedTimeEvent(System.currentTimeMillis())));
//                            recycler_view.scrollToPosition(myCustomAdapter.getItemCount() - 1);

                        }
                    }, 100);

                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.e("ChatSend_Response", t.getMessage() + "");
            }
        });

    }

    private void sendChat() {
        final String msg = edtComment.getText().toString();
        if (msg.isEmpty()) return;
        SendMsg();
//        myCustomAdapter.add(msg+"");
//        myCustomAdapter.insertItem(new Datum(myCustomAdapter.getItemCount(), msg + "", false, myCustomAdapter.getItemCount() % 5 == 0, Tools.getFormattedTimeEvent(System.currentTimeMillis())));
//        adapter.insertItem(new Message(adapter.getItemCount(), "Hello!", true, adapter.getItemCount() % 5 == 0, Tools.getFormattedTimeEvent(System.currentTimeMillis())));
//        myCustomAdapter.notifyItemChanged(myCustomAdapter.moviesList.size() - 1);
//        adapter.insertItem(new Message(adapter.getItemCount(), msg, true, adapter.getItemCount() % 5 == 0, Tools.getFormattedTimeEvent(System.currentTimeMillis())));
        edtComment.setText("");
//        recycler_view.scrollToPosition(myCustomAdapter.getItemCount() - 1);
        recycler_view.scrollToPosition(recycler_view.getBottom());
        myCustomAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                SendMsg();
//                myCustomAdapter.notifyItemChanged(myCustomAdapter.moviesList.size() - 1);
//                myCustomAdapter.insertItem(new ResultReceive(myCustomAdapter.getItemCount(), msg + "", false, myCustomAdapter.getItemCount() % 5 == 0, Tools.getFormattedTimeEvent(System.currentTimeMillis())));
//                recycler_view.scrollToPosition(myCustomAdapter.getItemCount() - 1);
//                recycler_view.scrollToPosition(recycler_view.getBottom());
//                myCustomAdapter.notifyDataSetChanged();

            }
        }, 0);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void ChatList() {
        Call<ChatDetailModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ChatDetailModel(id, "Bearer " + token);
        marqueCall.enqueue(new Callback<ChatDetailModel>() {
            @Override
            public void onResponse(@NonNull Call<ChatDetailModel> call, @NonNull Response<ChatDetailModel> response) {
                ChatDetailModel object = response.body();
                Log.e("TAG", "ChatReceive_Response : " + new Gson().toJson(response.body()));

                if (response.isSuccessful()) {
                    assert object != null;
                    datumArrayList = object.getData();
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    recycler_view.setAdapter(myCustomAdapter);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(ChatDetailsActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(ChatDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    public class MyCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Datum> moviesList;

        public MyCustomAdapter(ArrayList<Datum> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_vendor_list, parent, false);
            if (viewType == CHAT_ME) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_whatsapp_me, parent, false);
                vh = new MyViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_whatsapp_telegram_you, parent, false);
                vh = new MyViewHolder(v);
            }
            return vh;
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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


            final Datum datum = moviesList.get(position);
            MyViewHolder vItem = (MyViewHolder) holder;
            if (datum != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    vItem.text_content.setText(Html.fromHtml(datum.getMessage() + "", Html.FROM_HTML_MODE_COMPACT));
                } else {
                    vItem.text_content.setText(datum.getMessage() + "");

                }

                vItem.text_time.setText(datum.getCreatedAt());
            }
            if (datum.getUserId().toString().equalsIgnoreCase(loginModel.getData().getUser().getUserDetail().getUserId() + "")) {

                if (loginModel.getData().getUser().getUserDetail().getPhoto() != null) {
                    Glide.with(ChatDetailsActivity.this)
                            .load(BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage"))
//                        .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(((MyViewHolder) holder).ivOtheruserProfile);
                }

            } else {
                if (image_path != null) {
                    Glide.with(ChatDetailsActivity.this)
                            .load(BuildConstants.Main_Image + image_path.replace("public", "storage"))
//                        .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(((MyViewHolder) holder).ivOtheruserProfile);
                }
            }

        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            public TextView text_content;
            public TextView text_time;
            public View lyt_parent;
            CircleImageView ivOtheruserProfile;

            public MyViewHolder(View v) {
                super(v);


                text_content = v.findViewById(R.id.text_content);
                text_time = v.findViewById(R.id.text_time);
                lyt_parent = v.findViewById(R.id.lyt_parent);
                ivOtheruserProfile = v.findViewById(R.id.ivOtheruserProfile);

            }

        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return this.moviesList.get(position).getFromId() ? CHAT_ME : CHAT_YOU;
//        }

        //        public void insertItem(ResultReceive item) {
//            this.moviesList.add(item);
//            notifyItemInserted(getItemCount());
//        }
//
//        public void setItems(ArrayList<ResultReceive> items) {
//            this.moviesList = items;
//        }
        public void add(Datum message) {
            this.moviesList.add(message);
            notifyItemInserted(this.moviesList.size() - 1);
        }

        public void add(int i, Datum message) {
            this.moviesList.add(0, message);
            notifyItemInserted(this.moviesList.size() - 1);
        }

        public void insertItem(Datum item) {
            this.moviesList.add(item);
            notifyItemInserted(getItemCount());
        }


        @Override
        public int getItemViewType(int position) {
            Datum message = this.moviesList.get(position);
//
            if (message.getUserId().toString().equalsIgnoreCase(loginModel.getData().getUser().getUserDetail().getUserId() + "")) {
                return CHAT_ME;
            }

            return position;

        }

//        @Override
//        public int getItemViewType(int position) {
//            return this.items.get(position).isFromMe() ? CHAT_ME : CHAT_YOU;
//        }
    }
}