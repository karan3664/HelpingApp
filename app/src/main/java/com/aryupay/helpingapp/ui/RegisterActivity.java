package com.aryupay.helpingapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.city.CityModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.login.Profession;
import com.aryupay.helpingapp.modal.profession.ProfessionModel;
import com.aryupay.helpingapp.modal.register.RegisterModel;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.Tools;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewDialog = new ViewDialog(this);
        viewDialog.setCancelable(false);
        Intent i = getIntent();


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
        if (i != null) {
            name = i.getStringExtra("name");
            email = i.getStringExtra("email");
            mobile = i.getStringExtra("mobile");
            fullNameEt.setText(name);
            emailEt.setText(email);
            contactNumberEt.setText(mobile);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("name", userNameEt.getText().toString() + "");
        hashMap.put("fullname", fullNameEt.getText().toString() + "");
        hashMap.put("bio", writeAboutYouEt.getText().toString() + "");
        hashMap.put("contact", contactNumberEt.getText().toString() + "");
        hashMap.put("email", emailEt.getText().toString() + "");
        hashMap.put("dob", dobEt.getText().toString() + "");
        hashMap.put("city_id", city_id + "");
        hashMap.put("gender", genderEt.getText().toString() + "");
        hashMap.put("profession_id", profession_id + "");
        hashMap.put("photo", "");
        hashMap.put("password", et_password.getText().toString() + "");

        showProgressDialog();
        Call<RegisterModel> registerModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).register(hashMap);
        registerModelCall.enqueue(new Callback<RegisterModel>() {

            @Override
            public void onResponse(@NonNull Call<RegisterModel> call, @NonNull Response<RegisterModel> response) {
                RegisterModel object = response.body();
                Log.e("TAG", "Register_Response : " + new Gson().toJson(response.body()));
                hideProgressDialog();


                if (response.isSuccessful()) {
                    if (object.getData().getOtp().matches("0")) {
                        HashMap<String, String> hashMap = new HashMap<>();

                        hashMap.put("name", userNameEt.getText().toString() + "");
                        hashMap.put("password", et_password.getText().toString() + "");

                        showProgressDialog();
                        Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LoginModel(hashMap);
                        loginModelCall.enqueue(new Callback<LoginModel>() {

                            @Override
                            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                                LoginModel object = response.body();
                                Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));

                                hideProgressDialog();

                                if (response.isSuccessful()) {
                                    PrefUtils.setUser(object, RegisterActivity.this);
                                    Intent loginIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginIntent);
                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(RegisterActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                                hideProgressDialog();
                                t.printStackTrace();
                                Log.e("Login_Response", t.getMessage() + "");
                            }
                        });
                    } else {
                        Intent intent = new Intent(RegisterActivity.this, MobileRegisterActivity.class);
                        intent.putExtra("mobile", contactNumberEt.getText().toString() + "");
                        intent.putExtra("name", userNameEt.getText().toString() + "");
                        intent.putExtra("password", et_password.getText().toString() + "");
                        intent.putExtra("otp", object.getData().getOtp() + "");
                        intent.putExtra("token", object.getData().getToken() + "");
                        intent.putExtra("one", true);
                        startActivity(intent);
                    }

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(RegisterActivity.this, jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterModel> call, @NonNull Throwable t) {
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


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_list_item_1, nameList); //selected item will look like a spinner set from XML

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


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_list_item_1, pnameList); //selected item will look like a spinner set from XML

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

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
}