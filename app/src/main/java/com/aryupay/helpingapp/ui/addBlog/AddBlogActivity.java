package com.aryupay.helpingapp.ui.addBlog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.addblog.AddBlogModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBlogActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_heading, et_category, et_description;
    String token, blogid;
    LoginModel loginModel;
    ImageView ivBack;
    RelativeLayout btnNext, btnBack;
    protected ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        loginModel = PrefUtils.getUser(AddBlogActivity.this);
        token = loginModel.getData().getToken() + "";
        et_heading = findViewById(R.id.et_heading);
        et_category = findViewById(R.id.et_category);
        et_description = findViewById(R.id.et_description);

        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        ivBack = findViewById(R.id.ivBack);

        et_category.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_category:
                showCategoryDialog(view);
                break;
            case R.id.btnNext:
//                Intent i = new Intent(AddBlogActivity.this, AddBlogImagesActivity.class);
//                i.putExtra("id",  "205");
//                startActivity(i);
                AddBlog();
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void showCategoryDialog(final View v) {
        final String[] array = new String[]{
                "urgent", "information", "general"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddBlogActivity.this);
        builder.setTitle("Select Category");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void AddBlog() {
        if (et_heading.getText().toString().isEmpty()) {
            et_heading.setError("Heading Required...");
            et_heading.requestFocus();
        } else if (et_description.getText().toString().isEmpty()) {
            et_description.setError("Description Required...");
            et_description.requestFocus();
        } else if (et_category.getText().toString().isEmpty()) {
            et_category.setError("Category Required...");
            et_category.requestFocus();
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("heading", et_heading.getText().toString() + "");
            hashMap.put("description", et_description.getText().toString() + "");
            hashMap.put("category", et_category.getText().toString() + "");

            showProgressDialog();
            Call<AddBlogModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).Addblog("Bearer " + token, hashMap);
            marqueCall.enqueue(new Callback<AddBlogModel>() {
                @Override
                public void onResponse(@NonNull Call<AddBlogModel> call, @NonNull Response<AddBlogModel> response) {
                    AddBlogModel object = response.body();
                    hideProgressDialog();
                    Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                    if (response.isSuccessful()) {
                        assert object != null;
                        Toast.makeText(AddBlogActivity.this, object.getMessage() + "", Toast.LENGTH_SHORT).show();
                        blogid = object.getData().getId() + "";
                        Intent i = new Intent(AddBlogActivity.this, AddBlogImagesActivity.class);
                        i.putExtra("id", object.getData().getId() + "");
                        startActivity(i);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(AddBlogActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AddBlogModel> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    hideProgressDialog();
                    Log.e("ChatV_Response", t.getMessage() + "");
                }
            });
        }
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}