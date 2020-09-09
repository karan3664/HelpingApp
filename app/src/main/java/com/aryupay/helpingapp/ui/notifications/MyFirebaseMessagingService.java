package com.aryupay.helpingapp.ui.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aryupay.helpingapp.R;
import com.aryupay.helpingapp.ui.NotificationsAllActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    String messageBody = "";
    String title = "";
    String myDeviceModel = "";
    PendingIntent pending_intent;
    int color_ORANGE;
    Uri default_sound_uri;
    public Intent intent;
    String fn, mobileno, gender, Booking_ID, OTP;

//    public static final String PREFS_NAME = "user_info";

    @Override
    public void onNewToken(String s) {
        Log.e("NEW_TOKEN", s);

  /*      SharedPreferences.Editor editor = getSharedPreferences("TOKEN_PREF", MODE_PRIVATE).edit();
        editor.putString("token", s);
        editor.apply();
        Log.d("token_stored", "" + editor);*/
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //  Log.d("fcm_onMR", "" + remoteMessage.getData().toString());
        //show me the passing section

        Log.d(TAG, "Message from server" + remoteMessage.getData());
        Map<String, String> data = remoteMessage.getData();

        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {

/*        title = remoteMessage.getData().get("drop_latitude");
        messageBody = remoteMessage.getData().get("drop_longitude");
        Log.d(TAG, "From: " + title + "  " + messageBody);*/


        intent = new Intent(MyFirebaseMessagingService.this, NotificationsAllActivity.class);
//        intent.putExtra("title", remoteMessage.getData().get("drop_latitude"));
//        intent.putExtra("message", remoteMessage.getData().get("drop_longitude"));
//        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


        pending_intent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        myDeviceModel = Build.MODEL;
        //  pending_intent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        color_ORANGE = 0xFFFF3300;

        try {
            default_sound_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } catch (Exception e) {

            Log.d(TAG, "From: " + e.toString());
        }


        if (Build.VERSION.SDK_INT < 16) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setColor(color_ORANGE)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setContentIntent(pending_intent)
                    .setSound(default_sound_uri)
                    .setAutoCancel(true)
                    .getNotification();
            NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notification_manager.notify(1, notification);

        } else {
            Bitmap bitmap_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            /*Notification notification = new NotificationCompat.Builder(this)
                    .setColor(color_ORANGE)
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(bitmap_icon)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setContentIntent(pending_intent)
                    .setSound(default_sound_uri)
                    .setAutoCancel(true)
                    .build();*/

            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(bitmap_icon)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setSound(default_sound_uri)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    .setContentIntent(pending_intent)
                    .setAutoCancel(true);

            // NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, mNotificationBuilder.build());

        }
        startActivity(intent);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_stat_name : R.drawable.ic_stat_name;
    }
}