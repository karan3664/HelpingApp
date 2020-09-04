package com.aryupay.helpingapp.api;


import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApiService {
//    @Multipart
//    @PUT("v1/video_upload")
//    Call<ResponseBody> uploadVideo(@Part("description") String description,
//                                   @Part("price") String price,
//                                   @Part("user_id") String user_id,
//                                   @Part("shop_detail") String shop_detail,
//                                   @Part("video") RequestBody video);


    //the base URL for our API
    //make sure you are not using localhost
    //find the ip usinc ipconfig command
    String BASE_URL = BuildConstants.CURRENT_REST_URL;

    //this is our multipart request
    //we have two parameters on is name and other one is description

}
