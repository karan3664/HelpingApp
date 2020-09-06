package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.BuildConstants;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.city.CityModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.profession.ProfessionModel;
import com.aryupay.helpingapp.modal.register.RegisterModel;
import com.aryupay.helpingapp.ui.HomeActivity;

import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.Tools;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.bumptech.glide.Glide;
import com.facebook.login.Login;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission_group.CAMERA;
import static android.os.Build.VERSION_CODES.M;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    CircleImageView ivProfile;
    EditText userNameEt, fullNameEt, writeAboutYouEt, contactNumberEt, emailEt,
            dobEt, genderEt, professionEt, spin_cities, et_password, et_confirmpassword;
    Button btnSaveRegister;
    protected ViewDialog viewDialog;

    ArrayList<CityModel> resultGetpostcodes;
    ArrayList<ProfessionModel> professions;
    String city_id, profession_id;
    private List<String> nameList = new ArrayList<>();
    private List<String> pnameList = new ArrayList<>();
    ListView listView, listView1;
    BottomSheetDialog dialog, dialo1;
    String name, email, mobile;
    LoginModel loginModel;
    String token;


    public static final int PERMISSION_REQUEST_CODE = 1111;
    private static final int REQUEST = 1337;
    public static int SELECT_FROM_GALLERY = 2;
    public static int CAMERA_PIC_REQUEST = 0;
    private String currentPhotoPath;
    private File photoFile = null;
    File fileImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        loginModel = PrefUtils.getUser(EditProfileActivity.this);
        token = loginModel.getData().getToken();

        ivProfile = findViewById(R.id.ivProfile);
        userNameEt = findViewById(R.id.userNameEt);
        fullNameEt = findViewById(R.id.fullNameEt);
        writeAboutYouEt = findViewById(R.id.writeAboutYouEt);
        contactNumberEt = findViewById(R.id.contactNumberEt);
        emailEt = findViewById(R.id.emailEt);
        dobEt = findViewById(R.id.dobEt);
        genderEt = findViewById(R.id.genderEt);
        professionEt = findViewById(R.id.professionEt);
        spin_cities = findViewById(R.id.spin_cities);
        et_password = findViewById(R.id.et_password);
        et_confirmpassword = findViewById(R.id.et_confirmpassword);
        btnSaveRegister = findViewById(R.id.btnSaveRegister);

        btnSaveRegister.setOnClickListener(this);
        dobEt.setOnClickListener(this);
        spin_cities.setOnClickListener(this);
        professionEt.setOnClickListener(this);
        genderEt.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        userNameEt.setText(loginModel.getData().getUser().getName() + "");
        fullNameEt.setText(loginModel.getData().getUser().getFullname() + "");
        writeAboutYouEt.setText(loginModel.getData().getUser().getUserDetail().getBio() + "");
        genderEt.setText(loginModel.getData().getUser().getUserDetail().getGender() + "");
        spin_cities.setText(loginModel.getData().getUser().getUserDetail().getCity().getCity() + "");
        professionEt.setText(loginModel.getData().getUser().getUserDetail().getProfession().getProfession() + "");
        emailEt.setText(loginModel.getData().getUser().getEmail() + "");
        contactNumberEt.setText(loginModel.getData().getUser().getUserDetail().getContact() + "");
        dobEt.setText(loginModel.getData().getUser().getUserDetail().getDob() + "");
        profession_id = loginModel.getData().getUser().getUserDetail().getProfessionId() + "";
        city_id = loginModel.getData().getUser().getUserDetail().getCityId() + "";
        if (loginModel.getData().getUser().getUserDetail().getPhoto() != null) {
            Glide.with(this)
                    .load(BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage"))
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(ivProfile);
            Log.e("Profile=>", BuildConstants.Main_Image + loginModel.getData().getUser().getUserDetail().getPhoto().replace("public", "storage" + ""));
        }
        CityCall();
        ProfessionCall();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveRegister:
                RegisterCall();
                break;
            case R.id.dobEt:
                dialogDatePickerLight();
                break;
            case R.id.spin_cities:
                dialog.show();
                break;
            case R.id.professionEt:
                dialo1.show();
                break;
            case R.id.genderEt:
                showGenderDialog(view);
                break;
            case R.id.ivProfile:
                selectImage();
                break;
        }
    }

    private void dialogDatePickerLight() {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();
                        dobEt.setText(Tools.getFormattedDateSimple(date_ship_millis));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorAccent));
//        datePicker.setMinDate(cur_calender);
        datePicker.show(getSupportFragmentManager(), "Datepickerdialog");
    }


    private void showGenderDialog(final View v) {
        final String[] array = new String[]{
                "Male", "Female"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Gender");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void RegisterCall() {
        File file = fileImage;

        //        if (!file.exists()) {
//            Toast.makeText(EditProfileActivity.this, "Failed to upload pic", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Map<String, RequestBody> hashMap = new HashMap<>();
        File fileImage = null;

        try {

            fileImage = new File(String.valueOf(file));

        } catch (Exception e) {
            e.printStackTrace();
        }


        RequestBody thumbnailimage = null;
        try {
            assert file != null;
            assert fileImage != null;
//            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
//            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
            thumbnailimage = RequestBody.create(MediaType.parse("*/*"), file);
        } catch (Exception e) {
            e.printStackTrace();
        }


        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), userNameEt.getText().toString() + "");
        RequestBody fullname = RequestBody.create(MediaType.parse("text/plain"), fullNameEt.getText().toString() + "");
        RequestBody bio = RequestBody.create(MediaType.parse("text/plain"), writeAboutYouEt.getText().toString() + "");
        RequestBody contact = RequestBody.create(MediaType.parse("text/plain"), contactNumberEt.getText().toString() + "");
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), emailEt.getText().toString() + "");
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), dobEt.getText().toString() + "");
        RequestBody cityid = RequestBody.create(MediaType.parse("text/plain"), city_id + "");
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), genderEt.getText().toString() + "");
        RequestBody professionid = RequestBody.create(MediaType.parse("text/plain"), profession_id + "");
//        RequestBody photo = null;
        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
//        photo = RequestBody.create(MediaType.parse("*/*"), file);
//        if (loginModel.getData().getUser().getUserDetail().getPhoto() != null) {
//            photo = RequestBody.create(MediaType.parse("*/*"), file);
//        } else {
//
//
//        }
        hashMap.put("name", name);
        hashMap.put("fullname", fullname);
        hashMap.put("bio", bio);
        hashMap.put("contact", contact);
        hashMap.put("email", email);
        hashMap.put("dob", dob);
        hashMap.put("city_id", cityid);
        hashMap.put("gender", gender);
        hashMap.put("profession_id", professionid);
        if (thumbnailimage != null) {
            hashMap.put("photo\";  filename=\"" + fileImage.getName() + "\"", thumbnailimage);
        } else {
            hashMap.put("photo", attachmentEmpty);
        }


        showProgressDialog();
        Call<LoginModel> registerModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).userUpdate("Bearer " + token, hashMap);
        registerModelCall.enqueue(new Callback<LoginModel>() {

            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                LoginModel object = response.body();
                Log.e("TAG", "Register_Response : " + new Gson().toJson(response.body()));
                hideProgressDialog();


                if (response.isSuccessful()) {
                    assert object != null;
                    Toast.makeText(EditProfileActivity.this, object.getMessage() + "", Toast.LENGTH_SHORT).show();
                    PrefUtils.setUser(object, EditProfileActivity.this);

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(EditProfileActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Log.e("Register_Response", t.getMessage() + "");
            }
        });
    }


    public void CityCall() {
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.sub_category_dialog);

        WindowManager.LayoutParams lpState = new WindowManager.LayoutParams();
        lpState.copyFrom(dialog.getWindow().getAttributes());
        lpState.width = WindowManager.LayoutParams.MATCH_PARENT;
        lpState.height = WindowManager.LayoutParams.MATCH_PARENT;
        lpState.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lpState);

        listView = (ListView) dialog.findViewById(R.id.list_sub_cat);
        listView.setDivider(getResources().getDrawable(R.drawable.close));
        listView.setDividerHeight(1);
        TextView txtState = (TextView) dialog.findViewById(R.id.dialogtitile);
        txtState.setText("Select City");
        showProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();

        Call<ArrayList<CityModel>> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).cities(hashMap);
        postCodeModelCall.enqueue(new Callback<ArrayList<CityModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CityModel>> call, @NonNull Response<ArrayList<CityModel>> response) {
                final ArrayList<CityModel> object = response.body();
                hideProgressDialog();
                nameList.clear();
                if (object != null) {
                    Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
//                    resultGetpostcodes = new ArrayList<CityModel>();

                    for (int i = 0; i < object.size(); i++) {

                        nameList.add(object.get(i).getCity() + "");
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfileActivity.this, android.R.layout.simple_list_item_1, nameList); //selected item will look like a spinner set from XML

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            String text = listView.getItemAtPosition(position).toString().replace(object.get(position).getCity(), "");
                            spin_cities.setText(object.get(position).getCity());
                            city_id = object.get(position).getId() + "";

                            dialog.dismiss();
                        }
                    });

                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CityModel>> call, @NonNull Throwable t) {

                hideProgressDialog();
                t.printStackTrace();
                Log.e("Postcode_Response", t.getMessage() + "");
            }
        });
//        dialog.show();
    }

    public void ProfessionCall() {
        dialo1 = new BottomSheetDialog(this);
        dialo1.setContentView(R.layout.sub_category_dialog);

        WindowManager.LayoutParams lpState = new WindowManager.LayoutParams();
        lpState.copyFrom(dialo1.getWindow().getAttributes());
        lpState.width = WindowManager.LayoutParams.MATCH_PARENT;
        lpState.height = WindowManager.LayoutParams.MATCH_PARENT;
        lpState.gravity = Gravity.CENTER;
        dialo1.getWindow().setAttributes(lpState);

        listView1 = (ListView) dialo1.findViewById(R.id.list_sub_cat);
        listView1.setDivider(getResources().getDrawable(R.drawable.close));
        listView1.setDividerHeight(1);
        TextView txtState = (TextView) dialo1.findViewById(R.id.dialogtitile);
        txtState.setText("Select Profession");
        showProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();

        Call<ArrayList<ProfessionModel>> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).profession(hashMap);
        postCodeModelCall.enqueue(new Callback<ArrayList<ProfessionModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ProfessionModel>> call, @NonNull Response<ArrayList<ProfessionModel>> response) {
                final ArrayList<ProfessionModel> object = response.body();
                hideProgressDialog();
                pnameList.clear();
                if (object != null) {
                    Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
                    professions = new ArrayList<ProfessionModel>();

                    for (int i = 0; i < object.size(); i++) {

                        pnameList.add(object.get(i).getProfession() + "");
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfileActivity.this, android.R.layout.simple_list_item_1, pnameList); //selected item will look like a spinner set from XML

                    listView1.setAdapter(adapter);
                    listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

//                            String text = listView1.getItemAtPosition(position).toString().replace(object.getProfession(), "");
                            profession_id = object.get(position).getId() + "";
                            professionEt.setText(object.get(position).getProfession());

                            dialo1.dismiss();
                        }
                    });

                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ProfessionModel>> call, @NonNull Throwable t) {

                hideProgressDialog();
                t.printStackTrace();
                Log.e("Postcode_Response", t.getMessage() + "");
            }
        });

    }


    private void selectImage() {
        final CharSequence[] options = {"From Camera", "From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Please choose an Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("From Camera")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkCameraPermission())
                            cameraIntent();
                        else
                            requestPermission();
                    } else
                        cameraIntent();
                } else if (options[item].equals("From Gallery")) {
                    if (Build.VERSION.SDK_INT >= M) {
                        if (checkGalleryPermission())
                            galleryIntent();
                        else
                            requestGalleryPermission();
                    } else
                        galleryIntent();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private void galleryIntent() {
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FROM_GALLERY);
    }

    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.aryupay.helpingapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "image",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        fileImage = new File(image.getAbsolutePath());
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && photoFile != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            if (null != bitmap) {
                ivProfile.setImageBitmap(bitmap);
                final File userImageFile = getUserImageFile(bitmap);
                if (null != userImageFile) {
                    fileImage = userImageFile;
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
//                    ((DashboardActivity) getActivity()).alertDialog("Click ok to upload image", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            uploadFile("pic", userImageFile);
//                        }
//                    });
                }
            }

        } else if (requestCode == SELECT_FROM_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            Uri galleryURI = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryURI);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != bitmap) {
                ivProfile.setImageBitmap(bitmap);
                final File userImageFile = getUserImageFile(bitmap);
                if (null != userImageFile) {
                    fileImage = userImageFile;
                    //call presetner of manager for api it always required file
                    // new ProfilePresenter(context, this).callImageUploadApi(userImageFile);
//                    ((DashboardActivity) getActivity()).alertDialog("Click ok to upload image", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            uploadFile("pic", userImageFile);
//                        }
//                    });
                }
            }
        }
    }

    private File getUserImageFile(Bitmap bitmap) {
        try {
            File f = new File(getCacheDir(), ".jpg");
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST);
    }

    private boolean checkCameraPermission() {
        int result1 = ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.CAMERA);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkGalleryPermission() {
        int result2 = ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result2 == PackageManager.PERMISSION_GRANTED;
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}