package com.ruslan_website.travelblog.utils.http.api;

import com.ruslan_website.travelblog.utils.http.model.Entry;
import com.ruslan_website.travelblog.utils.http.model.User;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public interface APIStrategy {
    public Call<ResponseBody> obtainToken(String clientId, String clientSecret, String grantType, String type, String username, String password);
    public Call<User> obtainUserInfo(String accessToken);
    public Call<List<Entry>> obtainEntry(String accessToken);
    public Call<ResponseBody> obtainImage(String imageName);
    public Call<ResponseBody> uploadEntry(String accessToken, MultipartBody.Part image, RequestBody userId, RequestBody place, RequestBody comments);
    public Call<ResponseBody> createNewUser(String userName, String userEmail, String password, String type, String socialId);
    public Call<ResponseBody> uploadGcmToken(String gcmToken);
}
