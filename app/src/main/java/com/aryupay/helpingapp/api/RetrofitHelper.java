package com.aryupay.helpingapp.api;


import android.content.Context;

import androidx.annotation.NonNull;

import com.aryupay.helpingapp.modal.addblog.AddBlogModel;
import com.aryupay.helpingapp.modal.blogdetails.BlogDetailsModel;
import com.aryupay.helpingapp.modal.blogdetails.CommentsBlogModel;
import com.aryupay.helpingapp.modal.bloglist.BlogListModel;
import com.aryupay.helpingapp.modal.city.CityModel;
import com.aryupay.helpingapp.modal.login.LoginModel;
import com.aryupay.helpingapp.modal.myping.MyPingBlogModel;
import com.aryupay.helpingapp.modal.phonesignup.PhoneSignupModel;
import com.aryupay.helpingapp.modal.profession.ProfessionModel;
import com.aryupay.helpingapp.modal.register.RegisterModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.io.IOException;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


/**
 * Created by Karan Brahmaxatriya on 20-Sept-18.
 */
public class RetrofitHelper {
    public static OkHttpClient okHttpClient;
    public static Retrofit retrofit, retrofitMatchScore;
    public static CookieManager cookieManager;
    private Context mContext;

    public RetrofitHelper(Context context) {
        mContext = context;
    }

    public static OkHttpClient getOkHttpClientInstance() {
        if (okHttpClient != null) {
            return okHttpClient;
        }


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(2000, TimeUnit.SECONDS);
        httpClient.readTimeout(120, TimeUnit.SECONDS);
        httpClient.writeTimeout(2000, TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
              /*  if (!Connectivity.isConnected(Betx11Application.getContext())) {
                    throw new NoConnectivityException();
                }*/
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder(); // Add Device Detail

                Request request = requestBuilder.build();
                Response response = chain.proceed(request);
                String requestedHost = request.url().host();
                assert response.networkResponse() != null;
                String responseHost = response.networkResponse().request().url().host();
                if (!requestedHost.equalsIgnoreCase(responseHost)) {
                    throw new NoConnectivityException();
                }


                return response;
            }
        });
        httpClient.addInterceptor(logging);

        return httpClient.build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .serializeNulls()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConstants.CURRENT_REST_URL)
                    .client(getOkHttpClientInstance())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        return retrofit.create(serviceClass);
    }


    public interface Service {
        /*---------------------------------------GET METHOD------------------------------------------------------*/
        @GET("cities")
        Call<ArrayList<CityModel>> cities(@QueryMap HashMap<String, String> hashMap);

        @GET("profession")
        Call<ArrayList<ProfessionModel>> profession(@QueryMap HashMap<String, String> hashMap);

        @GET("verifyotp")
        Call<JsonObject> verifyotp(@Header("Authorization") String token);

        @GET("view_blog/{blog_id}")
        Call<BlogDetailsModel> BlogDetailsModel(@Path("blog_id") String blog_id, @Header("Authorization") String token);

        @GET("comment_blog/{blog_id}")
        Call<CommentsBlogModel> CommentsBlogModel(@Path("blog_id") String blog_id, @Header("Authorization") String token);

        @GET("like_blog/{blog_id}")
        Call<JsonObject> like_blog(@Path("blog_id") String blog_id, @Header("Authorization") String token);

        @GET("favourite/{blog_id}")
        Call<JsonObject> favourite(@Path("blog_id") String blog_id, @Header("Authorization") String token);

        @GET("myping")
        Call<MyPingBlogModel> MyPingBlogModel(@Header("Authorization") String token);

        @GET("myping/{category}")
        Call<MyPingBlogModel> MyPingBlogModelCategory(@Path("category") String category, @Header("Authorization") String token);


        @FormUrlEncoded
        @POST("login")
        Call<LoginModel> LoginModel(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("register")
        Call<RegisterModel> register(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @POST("phone_signup")
        Call<PhoneSignupModel> PhoneSignupModel(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @PATCH("blogs")
        Call<BlogListModel> BlogListModel(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @PATCH("blogs/{category}")
        Call<BlogListModel> CategoryBlog(@Path("category") String category, @Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @POST("comment_blog/{blog_id}")
        Call<JsonObject> comment_blog(@Path("blog_id") String blog_id, @Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @POST("blog")
        Call<AddBlogModel> Addblog(@Header("Authorization") String token, @FieldMap HashMap<String, String> hashMap);

        @Multipart
        @POST("file/{blog_id}")
        Call<JsonObject> uploadFile(@Path("blog_id") String blog_id, @Part MultipartBody.Part file, @Header("Authorization") String token);

        @Multipart
        @POST("file/{blog_id}")
        Call<JsonObject> upload(
                @Path("blog_id") String blog_id,
                @Header("Authorization") String authorization,
                @PartMap Map<String, RequestBody> map
        );

    }


    public static ArrayList<KeyValueModel> getKeyValueInputData(LinkedHashMap<String, String> hm) {
        ArrayList<KeyValueModel> modelList = new ArrayList<>();
        for (String key : hm.keySet()) {
            KeyValueModel obj = new KeyValueModel();
            obj.setKey(key);
            obj.setValue(hm.get(key));
            modelList.add(obj);
        }
        return modelList;
    }


}