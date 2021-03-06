package com.aryupay.helpingapp.ui.chats.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.ui.chats.Model.Chat;
import com.aryupay.helpingapp.ui.chats.Model.User;
import com.aryupay.helpingapp.utils.Tools;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    DatabaseReference reference;
    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_whatsapp_me, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_whatsapp_telegram_you, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);


        holder.show_message.setText(chat.getMessage());

        holder.show_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CharSequence options[] = new CharSequence[]{"Delete", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete this message");
                builder.setItems(options, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                                /*
                                        ....CODE FOR DELETING THE MESSAGE IS YET TO BE WRITTEN HERE...
                                 */
                            long mesPos = holder.getAdapterPosition();
                            String mesId = mChat.get((int) mesPos).toString();
                            Log.e("Message Id is ", mesId);
                            Log.e("Message is : ", mChat.get((int) mesPos).getMessage());
//                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//                            rootRef.child("Chats").child(mesId)
//                                    .removeValue();
                        }

                        if (which == 1) {

                        }

                    }
                });
                builder.show();
                return true;
            }
        });

        holder.text_time.setText(Tools.getMessageTime(chat.getTimestamp()) + "");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (mChat.get(position).getSender().equals(fuser.getUid())) {
                    if (user.getImageURL().equals("default")) {
                        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        if (user.getImageURL() != null) {
                            try {
                                Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                        }

                    }
                } else {
                    if (imageurl.equals("default")) {
                        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        if (imageurl != null) {
                            try {
                                Glide.with(mContext).load(imageurl).into(holder.profile_image);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message, text_time;
        public CircleImageView profile_image;
        public TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            text_time = itemView.findViewById(R.id.text_time);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}