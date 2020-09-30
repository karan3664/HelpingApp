package com.aryupay.helpingapp.ui.addBlog;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.adapter.ImageAdapter;
import com.aryupay.helpingapp.adapter.SelectedImageAdapter;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.addblog.FileUploadModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.ui.HomeActivity;
import com.aryupay.helpingapp.ui.LoginActivity;
import com.aryupay.helpingapp.ui.fragments.activity.DetailBlogsActivity;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.developers.imagezipper.ImageZipper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission_group.CAMERA;
import static android.icu.lang.UCharacter.LineBreak.LINE_FEED;
import static android.os.Build.VERSION_CODES.M;

public class AddBlogImagesActivity extends Activity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGES = 2;
    public static final int STORAGE_PERMISSION = 100;
    public static final int PERMISSION_REQUEST_CODE = 1111;
    private static final int REQUEST = 1337;
    ArrayList<ImageModel> imageList;
    ArrayList<String> selectedImageList;
    RecyclerView imageRecyclerView, selectedImageRecyclerView;
    int[] resImg = {R.drawable.arcamera, R.drawable.video};
    String[] title = {"Camera", "Folder"};
    String mCurrentPhotoPath;
    SelectedImageAdapter selectedImageAdapter;
    ImageAdapter imageAdapter;
    String[] projection = {MediaStore.MediaColumns.DATA};
    File image;
    Button done;
    RelativeLayout btnNext, btnBack;
    String id;
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    String token, blogid, uriz;
    File ImageNameFile;
    private File photoFile = null;
    ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog_images);
        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        loginModel = PrefUtils.getUser(AddBlogImagesActivity.this);
        token = loginModel.getData().getToken() + "";
        Intent i = getIntent();
        id = i.getStringExtra("id");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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
        ivClose = findViewById(R.id.ivClose);
        selectedImageList = new ArrayList<>();
        imageList = new ArrayList<>();
        done.setVisibility(View.GONE);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < selectedImageList.size(); i++) {
                    photoFile = new File(selectedImageList.get(i));
//                    btnNext.setEnabled(true);
                    uploadFile(photoFile);
                    new CountDownTimer(5000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            showProgressDialog();

                        }

                        public void onFinish() {
                            hideProgressDialog();
                            Intent addLoc = new Intent(AddBlogImagesActivity.this, AddLocationActivity.class);
                            addLoc.putExtra("id", id + "");
                            startActivity(addLoc);
                        }
                    }.start();
//                    Toast.makeText(getApplicationContext(), ImageNameFile + "", Toast.LENGTH_LONG).show();

                }

//                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddBlogImagesActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                finish();
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
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkCameraPermission())
                            cameraIntent();
                        else
                            requestPermission();
                    } else
                        cameraIntent();

                } else if (position == 1) {
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkGalleryPermission())
                            galleryIntent();
                        else
                            requestGalleryPermission();
                    } else
                        galleryIntent();

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
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            ImageModel ImageModel = new ImageModel();
            ImageModel.setImage(absolutePathOfImage);
            imageList.add(ImageModel);
        }
        cursor.close();
    }

    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(AddBlogImagesActivity.this, "com.aryupay.helpingapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES);
    }

    // Add image in selectedImageList
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


    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "image",  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /* public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (resultCode == RESULT_OK) {
             if (requestCode == REQUEST_IMAGE_CAPTURE && photoFile != null) {
                 Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                 if (null != bitmap) {
                     final File userImageFile = getUserImageFile(bitmap);
 //                    ImageNameFile = userImageFile;
                     addImage(mCurrentPhotoPath);
                     assert userImageFile != null;
 //                    uploadFile(userImageFile);
                 }
 //                if (mCurrentPhotoPath != null) {
 //
 //                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
 //                    final File userImageFile = getUserImageFile(bitmap);
 //
 //
 //                }
             } else if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK && null != data) {

                 if (data.getClipData() != null) {
                     ClipData mClipData = data.getClipData();
                     for (int i = 0; i < mClipData.getItemCount(); i++) {
 //                        Uri galleryURI = data.getData();
 //
                         ClipData.Item item = mClipData.getItemAt(i);
                         Uri uri = item.getUri();
                         getImageFilePath(uri);
                         Bitmap bitmap = null;
                         try {
                             bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                         final File userImageFile = getUserImageFile(bitmap);
 //                        photoFile = userImageFile;
                         assert userImageFile != null;
                         photoFile = new File(userImageFile.getAbsolutePath());
 //                        addImage(photoFile.getAbsolutePath());
                     }
                 } else if (data.getData() != null) {
                     Uri galleryURI = data.getData();
                     Bitmap bitmap = null;
                     try {
                         bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryURI);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     getImageFilePath(galleryURI);
                     final File userImageFile = getUserImageFile(bitmap);
                     assert userImageFile != null;
                     photoFile = new File(userImageFile.getAbsolutePath());
 //                    addImage(photoFile.getAbsolutePath());

                 }
             }
         }
     }
 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && photoFile != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            if (null != bitmap) {

                addImage(mCurrentPhotoPath);
                final File userImageFile = getUserImageFile(bitmap);
                if (null != userImageFile) {
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
                    uploadFile(userImageFile);
//
                }
            }

        } else if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK && null != data) {
            Uri galleryURI = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryURI);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != bitmap) {
                String type = "";
                getImageFilePath(galleryURI);
                final File userImageFile = getUserImageFile(bitmap);
                if (null != userImageFile) {
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
                    uploadFile(userImageFile);
//                    photoFile = userImageFile;

                }

            }


        }

    }

    private File getUserImageFile(Bitmap bitmap) {
        try {
            File f = new File(getApplication().getCacheDir(), ".jpg");
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get image file path
    public void getImageFilePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                if (absolutePathOfImage != null) {
                    checkImage(absolutePathOfImage);
                } else {
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(AddBlogImagesActivity.this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(AddBlogImagesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST);
    }

    private boolean checkCameraPermission() {
        int result1 = ContextCompat.checkSelfPermission(AddBlogImagesActivity.this, Manifest.permission.CAMERA);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkGalleryPermission() {
        int result2 = ContextCompat.checkSelfPermission(AddBlogImagesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result2 == PackageManager.PERMISSION_GRANTED;
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
        Log.e("IMAGE", file + "");

        if (!file.exists()) {
//            Toast.makeText(AddBlogImagesActivity.this, "Failed to upload pic", Toast.LENGTH_SHORT).show();
            return;
        }
        File imageZipperFile = null;
        try {
            imageZipperFile = new ImageZipper(AddBlogImagesActivity.this)
                    .setQuality(50)
                    .setMaxWidth(300)
                    .setMaxHeight(300)
                    .compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileExtension
                = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(imageZipperFile).toString());
        String mimeType
                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        RequestBody fbody = RequestBody.create(MediaType.parse(mimeType), imageZipperFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file[]", imageZipperFile.getName(), fbody);
        try {
            Bitmap b = new ImageZipper(AddBlogImagesActivity.this).compressToBitmap(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Map<String, RequestBody> map = new HashMap<>();
//        map.put("file[]\"; filename=\"" + file.getName() + "\"", requestBody);
//        showProgressDialog();

        Log.e("MAP", body + "");
        Call<FileUploadModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).upload(id + "", "Bearer " + token, body);
        marqueCall.enqueue(new Callback<FileUploadModel>() {
            @Override
            public void onResponse(@NonNull Call<FileUploadModel> call, @NonNull Response<FileUploadModel> response) {
                FileUploadModel object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ImageUploadSuccess : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    Log.e("TAG", "ImageUploadSuccess 2 " + new Gson().toJson(response.body()));
                    // Toast.makeText(AddBlogImagesActivity.this, response.body().get("message") + "", Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(AddBlogImagesActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FileUploadModel> call, @NonNull Throwable t) {
                t.printStackTrace();
//                hideProgressDialog();
                Log.e("ImageUploadError", t.getMessage() + "");
            }
        });

    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

}

