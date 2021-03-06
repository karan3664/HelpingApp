package com.aryupay.helpingapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.helpingapp.R;

public class HelpAndSupportActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = HelpAndSupportActivity.class.getSimpleName();

    ImageView imageViewCall;
    TextView textViewCall;
    LinearLayout relativeLayoutCall;
    ImageView imageViewEmail;
    TextView textViewEmail;
    LinearLayout relativeLayoutEmail;
    ImageView imageViewMsg;
    TextView textViewMsg;
    LinearLayout relativeLayoutMessageMe;
    ImageView imageViewWhatsUp, ivBack;
    TextView textViewWhatsUp;
    LinearLayout relativeLayoutWhatsUs;

    private Context context;
    private static final int CALL_REQUEST = 1337;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);
        imageViewCall = findViewById(R.id.imageViewCall);
        textViewCall = findViewById(R.id.textViewCall);
        relativeLayoutCall = findViewById(R.id.relativeLayoutCall);
        textViewEmail = findViewById(R.id.textViewEmail);
        imageViewEmail = findViewById(R.id.imageViewEmail);
        relativeLayoutEmail = findViewById(R.id.relativeLayoutEmail);
        imageViewMsg = findViewById(R.id.imageViewMsg);
        textViewMsg = findViewById(R.id.textViewMsg);
        imageViewMsg = findViewById(R.id.imageViewMsg);
        relativeLayoutMessageMe = findViewById(R.id.relativeLayoutMessageMe);
        imageViewWhatsUp = findViewById(R.id.imageViewWhatsUp);
        textViewWhatsUp = findViewById(R.id.textViewWhatsUp);
        relativeLayoutWhatsUs = findViewById(R.id.relativeLayoutWhatsUs);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(this);
        relativeLayoutCall.setOnClickListener(this);
        relativeLayoutEmail.setOnClickListener(this);
        relativeLayoutMessageMe.setOnClickListener(this);
        relativeLayoutWhatsUs.setOnClickListener(this);
    }

    private void sendMsg(String mobileNumber) {
        Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", mobileNumber);
        smsIntent.putExtra("sms_body", "Hello I have Problem Please contact me");
        startActivity(smsIntent);
    }


    private void openWhatsApp() {
        if (!isWhatsaapClientInstalled(this)) {
            goToMarketForWhats(this);
            return;
        }
        String contact = "+91 9713550801"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = this.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(HelpAndSupportActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
//        Uri uri = Uri.parse(url);
//        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
////        i.putExtra("sms_body", "Hello whatsaap");
//        i.setPackage("com.whatsapp");
//        try {
//            startActivity(i);
//        } catch (ActivityNotFoundException ex) {
//            Toast.makeText(this, "Opps not able to connect", Toast.LENGTH_SHORT).show();
//        }

    }

    public void goToMarketForWhats(Context myContext) {
        Uri marketUri = Uri.parse("market://details?id=com.whatsapp");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(myIntent);
    }


    public boolean isWhatsaapClientInstalled(Context myContext) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        ;
        try {
            myPackageMgr.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }


    private void sendMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {"helpingindia.app@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Helping App Support Care");
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the content here...");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

    private void makeCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (checkCameraPermission()) {
            startActivity(callIntent);
        } else {
            requestCallPermission();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall("9713550801");
                } else {
                    makeCall("9713550801");
                }
                return;
            }
        }
    }


    private boolean checkCameraPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCallPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relativeLayoutCall:
                makeCall("9713550801");
                break;
            case R.id.relativeLayoutEmail:
                sendMail();
                break;
            case R.id.relativeLayoutMessageMe:
                sendMsg("9713550801");
                break;
            case R.id.relativeLayoutWhatsUs:
                openWhatsApp();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;

        }
    }
}