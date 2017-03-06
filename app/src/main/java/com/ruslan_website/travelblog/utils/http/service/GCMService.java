package com.ruslan_website.travelblog.utils.http.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GCMService {
    @FormUrlEncoded
    @POST("laravel/travel_blog/api/gcmtoken")
    Call<ResponseBody> uploadToken(
            @Field("token") String gcmToken);
}
