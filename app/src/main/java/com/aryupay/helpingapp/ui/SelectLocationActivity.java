package com.aryupay.helpingapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.adapter.AutoCompleteAdapter;
import com.aryupay.helpingapp.api.RetrofitHelper;
import com.aryupay.helpingapp.modal.city.CityModel;
import com.aryupay.helpingapp.modal.location.LocationModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.utils.PrefUtils;
import com.aryupay.helpingapp.utils.ViewDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLocationActivity extends AppCompatActivity {
    RecyclerView rvCityList;
    LinearLayout llCurrentCity;
    private MyCustomAdapter myCustomAdapter;
    ArrayList<LocationModel> datumArrayList = new ArrayList<>();
    protected ViewDialog viewDialog;
    //    EditText ;

    LoginModel loginModel;
    String token;
    String cityId, cityName, city;
    SharedPreferences preferences;
    TextView tvCurrentCity;
    ImageButton bt_clear;
    ImageView ivBack;
    private static String TAG = SelectLocationActivity.class.getSimpleName();
    TextView places;
    AppCompatAutoCompleteTextView autoCompleteTextView;
    AutoCompleteAdapter adapter;

    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        loginModel = PrefUtils.getUser(this);
        token = loginModel.getData().getToken();
        viewDialog = new ViewDialog(SelectLocationActivity.this);
        viewDialog.setCancelable(false);

        tvCurrentCity = findViewById(R.id.tvCurrentCity);
        llCurrentCity = findViewById(R.id.llCurrentCity);
        rvCityList = findViewById(R.id.rvCityList);
        bt_clear = findViewById(R.id.bt_clear);

        ivBack = findViewById(R.id.ivBack);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SelectLocationActivity.this);
        rvCityList.setLayoutManager(layoutManager);
        rvCityList.setHasFixedSize(true);
        preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        city = preferences.getString("location", "");
        FollowerList();
        tvCurrentCity.setText(city);
        String apiKey = getString(R.string.api_key);
        places = findViewById(R.id.place);
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(this);
        initAutoCompleteTextView();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

      /*  // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                places.setText(place.getName() + ", " + place.getAddressComponents().asList().get(1).getName() + " ");
                Log.e(TAG, "Place1: " + place.getName() + ", ");
                Log.e(TAG, "Place2: " + place.getAddressComponents().asList().get(0).getName() + ", ");
                Log.e(TAG, "Place3: " + place.getAddressComponents().asList().get(2).getName() + ", ");
                Log.e(TAG, "Place4: " + place.getAddressComponents());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
*/
        places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
//                    intent.putExtra("id", cityModel.get() + "");
                intent.putExtra("cityname", places.getText().toString() + "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.setText("");
            }
        });
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("location", city + "");
        Log.e("SearchLocation", hashMap + "");
        showProgressDialog();
        Call<ArrayList<LocationModel>> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LocationModel(hashMap);
        marqueCall.enqueue(new Callback<ArrayList<LocationModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<LocationModel>> call, @NonNull Response<ArrayList<LocationModel>> response) {
                ArrayList<LocationModel> object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    if (object != null) {
                        for (int i = 0; i < object.size(); i++) {
                            cityName = object.get(i).getLocation() + "";
//                            cityId = object.get(i).getId() + "";
                        }

                        llCurrentCity.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
//                                intent.putExtra("id", cityId + "");
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
            public void onFailure(@NonNull Call<ArrayList<LocationModel>> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });
    }

    private void initAutoCompleteTextView() {

        autoCompleteTextView = findViewById(R.id.auto);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener(autocompleteClickListener);
        adapter = new AutoCompleteAdapter(this, placesClient);
        autoCompleteTextView.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            try {
                final AutocompletePrediction item = adapter.getItem(i);
                String placeID = null;
                if (item != null) {
                    placeID = item.getPlaceId();
                }

//                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
//                Use only those fields which are required.

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                        , Place.Field.ADDRESS_COMPONENTS);

                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
//                            responseView.setText(task.getPlace().getName() + "\n" + task.getPlace().getAddress());
                            places.setText(task.getPlace().getAddressComponents().asList().get(1).getName() + ", " + task.getPlace().getAddressComponents().asList().get(2).getName() + " ");
                            Log.e(TAG, "Place1: " + task.getPlace().getName() + ", ");
                            Log.e(TAG, "Place2: " + task.getPlace().getAddressComponents().asList().get(0).getName() + ", ");
                            Log.e(TAG, "Place3: " + task.getPlace().getAddressComponents().asList().get(2).getName() + ", ");
                            Log.e(TAG, "Place4: " + task.getPlace().getAddressComponents());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            places.setText(e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private void SearchList(String s) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("location", s + "");

        showProgressDialog();
        Call<ArrayList<LocationModel>> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LocationModel(hashMap);
        marqueCall.enqueue(new Callback<ArrayList<LocationModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<LocationModel>> call, @NonNull Response<ArrayList<LocationModel>> response) {
                ArrayList<LocationModel> object = response.body();
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
            public void onFailure(@NonNull Call<ArrayList<LocationModel>> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });

    }

    private void FollowerList() {

        showProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();

        Call<ArrayList<LocationModel>> postCodeModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LocationModelG("Bearer " + token);
        postCodeModelCall.enqueue(new Callback<ArrayList<LocationModel>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ArrayList<LocationModel>> call, @NonNull Response<ArrayList<LocationModel>> response) {
                final ArrayList<LocationModel> object = response.body();
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
            public void onFailure(@NonNull retrofit2.Call<ArrayList<LocationModel>> call, @NonNull Throwable t) {

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

        private ArrayList<LocationModel> moviesList;

        public MyCustomAdapter(ArrayList<LocationModel> moviesList) {
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


            LocationModel cityModel = moviesList.get(position);
            holder.cityName.setText(cityModel.getLocation() + "");
            holder.llCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
//                    intent.putExtra("id", cityModel.get() + "");
                    intent.putExtra("cityname", cityModel.getLocation() + "");
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