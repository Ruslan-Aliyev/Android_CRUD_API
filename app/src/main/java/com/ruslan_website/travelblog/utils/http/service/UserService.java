package com.ruslan_website.travelblog.utils.http.service;

import com.ruslan_website.travelblog.utils.http.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserService {
    @GET("laravel/travel_blog/api/user")
    Call<User> obtain(@Header("Accept") String accept, @Header("Authorization") String authorization);
}
