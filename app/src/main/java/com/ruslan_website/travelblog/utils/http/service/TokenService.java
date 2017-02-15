package com.ruslan_website.travelblog.utils.http.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TokenService {
    @FormUrlEncoded
    @POST("laravel/travel_blog/oauth/token")
    Call<ResponseBody> obtain(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password);
}
