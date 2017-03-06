package com.ruslan_website.travelblog.utils.http.api.cms;

import com.ruslan_website.travelblog.utils.http.api.APIStrategy;
import com.ruslan_website.travelblog.utils.http.model.Entry;
import com.ruslan_website.travelblog.utils.http.model.User;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

public class Cake implements APIStrategy {

    private SharedPreferencesManagement mSPM;
    private String url;

    public Cake(){
        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }
        url = mSPM.getCakeUrl();
    }

    @Override
    public Call<ResponseBody> obtainToken(String clientId, String clientSecret, String grantType, String type, String username, String password) {
        return null;
    }

    @Override
    public Call<User> obtainUserInfo(String accessToken) {
        return null;
    }

    @Override
    public Call<List<Entry>> obtainEntry(String accessToken) {
        return null;
    }

    @Override
    public Call<ResponseBody> obtainImage(String imageName) {
        return null;
    }

    @Override
    public Call<ResponseBody> uploadEntry(String accessToken, MultipartBody.Part image, RequestBody userId, RequestBody place, RequestBody comments) {
        return null;
    }

    @Override
    public Call<ResponseBody> createNewUser(String userName, String userEmail, String password, String type, String socialId){
        return null;
    }

    @Override
    public Call<ResponseBody> uploadGcmToken(String gcmToken) {
        return null;
    }

    private OkHttpClient makeHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();    }
}
