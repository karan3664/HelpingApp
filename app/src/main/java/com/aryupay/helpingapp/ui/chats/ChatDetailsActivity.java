package com.aryupay.helpingapp.ui.chats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailsActivity extends AppCompatActivity {

    TextView userChatName;
    CircleImageView ivProfile;
    ImageView ivClose, ivCall, ivOption;

    String name, image_path;
    Button btnCommentSave;
    EditText edtComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        userChatName = findViewById(R.id.userChatName);
        ivProfile = findViewById(R.id.ivProfile);
        ivClose = findViewById(R.id.ivClose);
        ivCall = findViewById(R.id.ivCall);
        ivOption = findViewById(R.id.ivOption);
        btnCommentSave = findViewById(R.id.btnCommentSave);
        edtComment = findViewById(R.id.edtComment);
        Intent i = getIntent();
        if (i != null) {
            name = i.getStringExtra("name");
            image_path = i.getStringExtra("image_path");

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
                edtComment.setText("");
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
}