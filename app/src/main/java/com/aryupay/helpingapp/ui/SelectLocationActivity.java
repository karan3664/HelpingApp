package com.aryupay.helpingapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.city.CityModel;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLocationActivity extends AppCompatActivity {
    RecyclerView rvCityList;
    LinearLayout llCurrentCity;
    private MyCustomAdapter myCustomAdapter;
    ArrayList<CityModel> datumArrayList = new ArrayList<>();
    protected ViewDialog viewDialog;
    EditText et_search;

    String cityId, cityName, city;
    SharedPreferences preferences;
    TextView tvCurrentCity;
    ImageButton bt_clear;
    ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        viewDialog = new ViewDialog(SelectLocationActivity.this);
        viewDialog.setCancelable(false);
        et_search = findViewById(R.id.et_search);
        tvCurrentCity = findViewById(R.id.tvCurrentCity);
        llCurrentCity = findViewById(R.id.llCurrentCity);
        rvCityList = findViewById(R.id.rvCityList);
        bt_clear = findViewById(R.id.bt_clear);
        ivBack = findViewById(R.id.ivBack);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SelectLocationActivity.this);
        rvCityList.setLayoutManager(layoutManager);
        rvCityList.setHasFixedSize(true);
        preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        city = preferences.getString("city", "");
        FollowerList();
        tvCurrentCity.setText(city);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SearchList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowerList();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("city", city + "");

        showProgressDialog();
        Call<ArrayList<CityModel>> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).search_city(hashMap);
        marqueCall.enqueue(new Callback<ArrayList<CityModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CityModel>> call, @NonNull Response<ArrayList<CityModel>> response) {
                ArrayList<CityModel> object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    if (object != null) {
                        for (int i = 0; i < object.size(); i++) {
                            cityName = object.get(i).getCity() + "";
                            cityId = object.get(i).getId() + "";
                        }

                        llCurrentCity.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.putExtra("id", cityId + "");
                                intent.putExtra("cityname", cityName + "");
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SelectLocationActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
//                    Toast.makeText(ChatListActivity.this, "No Chat Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CityModel>> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });
    }

    private void SearchList(String s) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("city", s + "");

        showProgressDialog();
        Call<ArrayList<CityModel>> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).search_city(hashMap);
        marqueCall.enqueue(new Callback<ArrayList<CityModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CityModel>> call, @NonNull Response<ArrayList<CityModel>> response) {
                ArrayList<CityModel> object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null) {


                    datumArrayList = object;
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    rvCityList.setAdapter(myCustomAdapter);

                } else {

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SelectLocationActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CityModel>> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    private void FollowerList() {

        showProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();

        Call<ArrayList<CityModel>> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).cities(hashMap);
        postCodeModelCall.enqueue(new Callback<ArrayList<CityModel>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArrayList<CityModel>> call, @NonNull Response<ArrayList<CityModel>> response) {
                final ArrayList<CityModel> object = response.body();
                hideProgressDialog();

                if (response.isSuccessful()) {
                    Log.e("TAG", "Postcode_Response : " + new Gson().toJson(response.body()));
//                    resultGetpostcodes = new ArrayList<CityModel>();

                    datumArrayList = object;
                    myCustomAdapter = new MyCustomAdapter(datumArrayList);
                    rvCityList.setAdapter(myCustomAdapter);

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SelectLocationActivity.this, jObjError.getString("error") + "", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ArrayList<CityModel>> call, @NonNull Throwable t) {

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

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<CityModel> moviesList;

        public MyCustomAdapter(ArrayList<CityModel> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_list, parent, false);

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


            CityModel cityModel = moviesList.get(position);
            holder.cityName.setText(cityModel.getCity() + "");
            holder.llCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("id", cityModel.getId() + "");
                    intent.putExtra("cityname", cityModel.getCity() + "");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView cityName;


            LinearLayout llCity;

            public MyViewHolder(View view) {
                super(view);


                cityName = view.findViewById(R.id.cityName);
                llCity = view.findViewById(R.id.llCity);


            }

        }

    }

}