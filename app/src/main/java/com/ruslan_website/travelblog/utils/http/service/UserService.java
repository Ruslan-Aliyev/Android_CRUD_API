package com.ruslan_website.travelblog.utils.http.service;

import com.ruslan_website.travelblog.utils.http.model.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserService {
    @GET("laravel/travel_blog/api/user")
    Call<User> obtain(@Header("Accept") String accept, @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("laravel/travel_blog/api/user")
    Call<ResponseBody> create(
            @Field("name") String userName,
            @Field("email") String userEmail,
            @Field("password") String password,
            @Field("type") String type,
            @Field("social_id") String socialId
    );
}
