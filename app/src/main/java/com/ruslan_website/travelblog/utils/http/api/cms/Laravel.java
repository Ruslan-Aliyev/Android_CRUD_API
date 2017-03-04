package com.ruslan_website.travelblog.utils.http.api.cms;

import com.ruslan_website.travelblog.utils.http.api.APIStrategy;
import com.ruslan_website.travelblog.utils.http.model.Entry;
import com.ruslan_website.travelblog.utils.http.model.User;
import com.ruslan_website.travelblog.utils.http.service.EntryService;
import com.ruslan_website.travelblog.utils.http.service.ImageService;
import com.ruslan_website.travelblog.utils.http.service.TokenService;
import com.ruslan_website.travelblog.utils.http.service.UserService;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Laravel implements APIStrategy {

    private SharedPreferencesManagement mSPM;
    private String url;

    public Laravel(){
        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }
        url = mSPM.getLaravelUrl();
    }

    @Override
    public Call<ResponseBody> obtainToken(String clientId, String clientSecret, String grantType, String type, String username, String password) {

        OkHttpClient client = makeHttpClient();

        TokenService tokenService = new Retrofit.Builder()
                .baseUrl( url )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TokenService.class);

        Call<ResponseBody> tokenRequest = tokenService.obtain(clientId, clientSecret, grantType, type, username, password);

        return tokenRequest;
    }

    @Override
    public Call<User> obtainUserInfo(String accessToken) {

        OkHttpClient client = makeHttpClient();

        UserService userService = new Retrofit.Builder()
                .baseUrl( url )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserService.class);

        Call<User> userRequest = userService.obtain("application/json", "Bearer " + accessToken );

        return userRequest;
    }

    @Override
    public Call<List<Entry>> obtainEntry(String accessToken) {

        OkHttpClient client = makeHttpClient();

        EntryService entryService = new Retrofit.Builder()
                .baseUrl( url )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EntryService.class);

        Call<List<Entry>> entryRequest = entryService.obtain("application/json", "Bearer " + accessToken );

        return entryRequest;
    }

    @Override
    public Call<ResponseBody> obtainImage(String imageName) {

        OkHttpClient client = makeHttpClient();

        ImageService imageService = new Retrofit.Builder()
                .baseUrl( url )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageService.class);

        Call<ResponseBody> imageRequest = imageService.download(imageName);

        return imageRequest;
    }

    @Override
    public Call<ResponseBody> uploadEntry(String accessToken, MultipartBody.Part image, RequestBody userId, RequestBody place, RequestBody comments) {

        OkHttpClient client = makeHttpClient();

        EntryService entryService = new Retrofit.Builder()
                .baseUrl( url )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EntryService.class);

        Call<ResponseBody> newEntryRequest = entryService.upload( "application/json", "Bearer " + accessToken, image, userId, place, comments );

        return newEntryRequest;
    }

    @Override
    public Call<ResponseBody> createNewUser(String userName, String userEmail, String password, String type, String socialId){

        OkHttpClient client = makeHttpClient();

        UserService userService = new Retrofit.Builder()
                .baseUrl( url )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserService.class);

        Call<ResponseBody> newUserRequest = userService.create(userName, userEmail, password, type, socialId);

        return newUserRequest;
    }

    private OkHttpClient makeHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }
}
