package com.ruslan_website.travelblog.utils.http.service;

import com.ruslan_website.travelblog.utils.http.model.Entry;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface EntryService {
    @GET("laravel/travel_blog/api/entry")
    Call<List<Entry>> obtain(
            @Header("Accept") String accept,
            @Header("Authorization") String authorization
    );

    @Multipart
    @POST("laravel/travel_blog/api/entry")
    Call<ResponseBody> upload(
            @Header("Accept") String accept,
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part image,
            @Part("user_id") RequestBody userId,
            @Part("place") RequestBody place,
            @Part("comments") RequestBody comments
    );

}
