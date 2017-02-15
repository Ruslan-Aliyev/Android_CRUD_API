package com.ruslan_website.travelblog.utils.http.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImageService {
    @GET("laravel/travel_blog/images/{fileName}")
    Call<ResponseBody> download(@Path("fileName") String fileName);
}
