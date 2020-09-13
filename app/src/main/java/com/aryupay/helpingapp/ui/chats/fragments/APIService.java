package com.aryupay.helpingapp.ui.chats.fragments;




import com.aryupay.helpingapp.ui.chats.Notifications.MyResponse;
import com.aryupay.helpingapp.ui.chats.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAY2pK0sQ:APA91bFmNJkbr78V9epvGdOssqLfxJY2p5mxXoXWl0-cG3_7aa2gN81k6pq5Q9iNyqaOmcs7zI0d8j8jkyvsJ3RSWkuux2IV9PvtMdaZomP2HSosHf_hUyDP8frcU0mc82lS23ae_QOL"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
