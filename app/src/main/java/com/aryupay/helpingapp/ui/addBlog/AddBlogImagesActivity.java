package com.aryupay.helpingapp.ui.addBlog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.adapter.ImageAdapter;
import com.aryupay.helpingapp.adapter.SelectedImageAdapter;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBlogImagesActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGES = 2;
    public static final int STORAGE_PERMISSION = 100;

    ArrayList<ImageModel> imageList;
    ArrayList<String> selectedImageList;
    RecyclerView imageRecyclerView, selectedImageRecyclerView;
    int[] resImg = {R.drawable.ic_camera_white_30dp, R.drawable.ic_folder_white_30dp};
    String[] title = {"Camera", "Folder"};
    String mCurrentPhotoPath;
    SelectedImageAdapter selectedImageAdapter;
    ImageAdapter imageAdapter;
    String[] projection = {MediaStore.MediaColumns.DATA};
    File image;
    Button done, btnNext, btnBack;
    String id;
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    String token, blogid, uriz;
    File fileImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        loginModel = PrefUtils.getUser(AddBlogImagesActivity.this);
        token = loginModel.getData().getToken() + "";
        Intent i = getIntent();
        id = i.getStringExtra("id");
        if (isStoragePermissionGranted()) {
            init();
            getAllImages();
            setImageList();
            setSelectedImageList();
        }
    }

    public void init() {
        imageRecyclerView = findViewById(R.id.recycler_view);
        selectedImageRecyclerView = findViewById(R.id.selected_recycler_view);
        done = findViewById(R.id.done);

        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        selectedImageList = new ArrayList<>();
        imageList = new ArrayList<>();

//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 0; i < selectedImageList.size(); i++) {
//                    Toast.makeText(getApplicationContext(), selectedImageList.get(i), Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < selectedImageList.size(); i++) {
                    Toast.makeText(getApplicationContext(), selectedImageList.get(i), Toast.LENGTH_LONG).show();

                    fileImage = new File(selectedImageList.get(i));

                    Toast.makeText(AddBlogImagesActivity.this, fileImage.getAbsolutePath() + "", Toast.LENGTH_SHORT).show();
                    uploadFile(fileImage);


                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addLoc = new Intent(AddBlogImagesActivity.this, AddLocationActivity.class);
                addLoc.putExtra("id", id + "");
                startActivity(addLoc);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setImageList() {
        imageRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        imageAdapter = new ImageAdapter(getApplicationContext(), imageList);
        imageRecyclerView.setAdapter(imageAdapter);

        imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (position == 0) {
                    takePicture();
                } else if (position == 1) {
                    getPickImageIntent();
                } else {
                    try {
                        if (!imageList.get(position).isSelected) {
                            selectImage(position);
                        } else {
                            unSelectImage(position);
                        }
                    } catch (ArrayIndexOutOfBoundsException ed) {
                        ed.printStackTrace();
                    }
                }
            }
        });
        setImagePickerList();
    }

    public void setSelectedImageList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        selectedImageRecyclerView.setLayoutManager(layoutManager);
        selectedImageAdapter = new SelectedImageAdapter(this, selectedImageList);
        selectedImageRecyclerView.setAdapter(selectedImageAdapter);
    }

    // Add Camera and Folder in ArrayList
    public void setImagePickerList() {
        for (int i = 0; i < resImg.length; i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setResImg(resImg[i]);
            imageModel.setTitle(title[i]);
            imageList.add(i, imageModel);
        }
        imageAdapter.notifyDataSetChanged();
    }

    // get all images from external storage
    public void getAllImages() {
        imageList.clear();
        Uri uri = MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_INTERNAL
        );
        Cursor cursor = null;
//        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
//        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection, null,null, null);
        CursorLoader loader = new CursorLoader(AddBlogImagesActivity.this, MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection, null, null, null);
        cursor = loader.loadInBackground();

        assert cursor != null;
        while (cursor.moveToNext()) {
//            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(android.provider.MediaStore.MediaColumns.DATA));

//            int absolutePathOfImage = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            ImageModel ImageModel = new ImageModel();
            ImageModel.setImage(String.valueOf(absolutePathOfImage));
            imageList.add(ImageModel);
        }
        cursor.close();
    }

    // start the image capture Intent
    public void takePicture() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Continue only if the File was successfully created;
        File photoFile = createImageFile();
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void getPickImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGES);
    }

    // Add image in SelectedArrayList
    public void selectImage(int position) {
        // Check before add new item in ArrayList;
        if (!selectedImageList.contains(imageList.get(position).getImage())) {
            imageList.get(position).setSelected(true);
            selectedImageList.add(0, imageList.get(position).getImage());
            selectedImageAdapter.notifyDataSetChanged();
            imageAdapter.notifyDataSetChanged();
        }
    }

    // Remove image from selectedImageList
    public void unSelectImage(int position) {
        for (int i = 0; i < selectedImageList.size(); i++) {
            if (imageList.get(position).getImage() != null) {
                if (selectedImageList.get(i).equals(imageList.get(position).getImage())) {
                    imageList.get(position).setSelected(false);
                    selectedImageList.remove(i);
                    selectedImageAdapter.notifyDataSetChanged();
                    imageAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public File createImageFile() {
        // Create an image file name
        String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + dateTime + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        storageDir.mkdirs();
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getPath();
        fileImage = new File(image.getAbsolutePath());
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (mCurrentPhotoPath != null) {
                    addImage(mCurrentPhotoPath);
                    fileImage = new File(mCurrentPhotoPath);
                }
            } else if (requestCode == PICK_IMAGES) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        getImageFilePath(uri);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    getImageFilePath(uri);
                }
            }
        }
    }

    // Get image file path
    public void getImageFilePath(Uri uri) {
        CursorLoader loader = new CursorLoader(AddBlogImagesActivity.this, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
//        Cursor cursor = getContentResolver().query(uri, projection, null,    null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                if (absolutePathOfImage != null) {
                    fileImage = new File(absolutePathOfImage);
                    checkImage(absolutePathOfImage);
                } else {
                    fileImage = new File(uri + "");
                    checkImage(String.valueOf(uri));
                }
            }
        }
    }

    public void checkImage(String filePath) {
        // Check before adding a new image to ArrayList to avoid duplicate images
        if (!selectedImageList.contains(filePath)) {
            for (int pos = 0; pos < imageList.size(); pos++) {
                if (imageList.get(pos).getImage() != null) {
                    if (imageList.get(pos).getImage().equalsIgnoreCase(filePath)) {
                        imageList.remove(pos);
                    }
                }
            }
            addImage(filePath);
        }
    }

    // add image in selectedImageList and imageList
    public void addImage(String filePath) {
        ImageModel imageModel = new ImageModel();
        imageModel.setImage(filePath);
        imageModel.setSelected(true);
        imageList.add(2, imageModel);
        selectedImageList.add(0, filePath);
        selectedImageAdapter.notifyDataSetChanged();
        imageAdapter.notifyDataSetChanged();
    }

    public boolean isStoragePermissionGranted() {
        int ACCESS_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if ((ACCESS_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
            getAllImages();
            setImageList();
            setSelectedImageList();
        }
    }

    private void uploadFile(File file) {

//        Log.e("FILE==>", file.getName() + "");
//        File file = new File(image.getAbsolutePath());
//        if (!file.exists()) {
//            Toast.makeText(AddBlogImagesActivity.this, "Failed to upload pic", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        File file = null;
//        File fileImage = null;

        try {
            file = new File(String.valueOf(file));
//            fileImage = new File(final_thumbnail_video);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = null;
//        RequestBody thumbnailimage = null;
        try {
            assert file != null;
//            assert fileImage != null;
            requestBody = RequestBody.create(MediaType.parse("*/*"), file);
//            thumbnailimage = RequestBody.create(MediaType.parse("*/*"), fileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, RequestBody> map = new HashMap<>();
        map.put("file[]\"; filename=\"" + file.getName() + "\"", requestBody);
//
//        String fileExtension
//                = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
//        String mimeType
//                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

//        RequestBody fbody = RequestBody.create(MediaType.parse(mimeType), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("file[]", file.getName(), fbody);
        Log.e("MAP", map + "");
        showProgressDialog();
        Call<JsonObject> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).upload(id, "Bearer " + token, map);
        marqueCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    Toast.makeText(AddBlogImagesActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                    Intent addImage = new Intent(AddBlogImagesActivity.this, AddLocationActivity.class);

                    addImage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(addImage);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(AddBlogImagesActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(AddBlogImagesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
       /* String token = "Bearer " + sharedData.getToken();

        Call<JsonObject> call = apiInterface.uploadFile(map, body, token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    Log.d("file upload", response.body().toString());

                } else {
                    try {
                        JsonObject object = new JsonParser().parse(response.errorBody().toString()).getAsJsonObject();
                        Toast.makeText(AddBlogImagesActivity.this, object.get("error").getAsString(), Toast.LENGTH_SHORT).show();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                t.printStackTrace();
            }
        });
*/
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}