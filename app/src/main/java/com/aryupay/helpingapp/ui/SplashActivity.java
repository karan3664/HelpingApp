package com.aryupay.helpingapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;


import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.utils.AppPreferences;
import com.aryupay.helpingapp.utils.ConnectivityReceiver;
import com.aryupay.helpingapp.utils.GpsUtils;
import com.aryupay.helpingapp.utils.MyApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class SplashActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    final String TAG = "GPS";
    private long UPDATE_INTERVAL = 2
            * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    GoogleApiClient gac;
    LocationRequest locationRequest;
    private boolean isContinue = false;
    private boolean isGPS = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        checkConnection();
        isGooglePlayServicesAvailable();
        if (!isLocationEnabled())
            showAlert();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gac = new GoogleApiClient.Builder(SplashActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS

                isGPS = isGPSEnable;

            }

        });

        if (!checkPermission()) {
//            openGPSSettings();

            requestPermission();

//                    return;
//
        } else {

            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashActivity.this, LoginOrRegisterActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(SplashActivity.this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
//        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_SECURE_SETTINGS);
//        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_SETTINGS);

        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
//                result3 == PackageManager.PERMISSION_GRANTED &&
//                result4 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,
                READ_EXTERNAL_STORAGE, READ_PHONE_STATE
        }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean telepone = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && telepone) {
                        startActivity(new Intent(this, this.getClass()));
//                        updateUI(location);
                        finish();
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_EXTERNAL_STORAGE, READ_PHONE_STATE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
//        this.location = loc;
        updateUI(location);
        AppPreferences.setLati(SplashActivity.this, location.getLatitude());
        AppPreferences.setLongi(SplashActivity.this, location.getLongitude());
       /* File file = new File(SplashActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "text");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, "sample");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("Latitude =>" + location.getLatitude() + "-------" + "Longitutde =>" + location.getLongitude());
            writer.flush();
            writer.close();
            Toast.makeText(SplashActivity.this, "Saved your Lat Long", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }*/
        Log.e("Shilpa==>", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        if (location != null) {


            try {

                Geocoder geo = new Geocoder(SplashActivity.this, Locale.getDefault());
//                List<Address> addresses = geo.getFromLocation(Double.parseDouble(et_latitude.getText().toString()),
//                        Double.parseDouble(et_longitude.getText().toString()), 1);
//                Log.e("L:A", addresses + "");
//                AppPreferences.setLati(SplashActivity.this, Double.parseDouble(et_latitude.getText().toString()));
//                AppPreferences.setLongi(SplashActivity.this, Double.parseDouble(et_longitude.getText().toString()));
//                if (addresses.isEmpty()) {
////                    address.setText("Waiting for Location");
//                } else {
//                    if (addresses.size() > 0) {
////                        address.setText(/*addresses.get(0).getFeatureName() + ", " + addresses.get(0).getSubLocality() + ", " + addresses.get(0).getSubAdminArea() + ", " +*/ addresses.get(0).getAddressLine(0) + "");
////                        Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
//                    }
//
//                }
            } catch (Exception e) {
                e.printStackTrace(); // getFromLocation() may sometimes fail
            }

        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        Log.d(TAG, "onConnected");

        Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);
        Log.d(TAG, "LastLocation: " + (ll == null ? "NO LastLocation" : ll.toString()));

        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(SplashActivity.this, "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
        Log.d("DDD", connectionResult.toString());
    }


    public void updateUI(Location location) {
        Log.d(TAG, "updateUI");
//        lati = location.getLatitude();
//        longi = location.getLongitude();
        AppPreferences.setLati(SplashActivity.this, location.getLatitude());
        AppPreferences.setLongi(SplashActivity.this, location.getLongitude());
//        et_latitude.setText(Double.toString(loc.getLatitude()));
//        et_longitude.setText(Double.toString(loc.getLongitude()));
        Log.e("Update = >", "API CALL");

        Log.e("LATLONG", location.getLatitude() + "" + "    " + location.getLongitude() + "");

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager =
                (LocationManager) SplashActivity.this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(SplashActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(SplashActivity.this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d(TAG, "SplashActivity.this device is not supported.");
                SplashActivity.this.finish();
            }
            return false;
        }
        Log.d(TAG, "SplashActivity.this device is supported.");
        return true;
    }

    private void showAlert() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(SplashActivity.this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use SplashActivity.this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
//            message = "";
//            color = Color.WHITE;
        } else {
            Toast.makeText(SplashActivity.this, "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

//        Toast.makeText(SplashActivity.this, message + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        gac.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
//        MyApplication.getInstance().setConnectivityListener(SplashActivity.this);
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        gac.disconnect();
        Log.e("lifecycle", "onStop invoked");
        super.onStop();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

}
