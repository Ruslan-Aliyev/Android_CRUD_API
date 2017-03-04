package com.ruslan_website.travelblog.utils.common;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ruslan_website.travelblog.EntryActivity;
import com.ruslan_website.travelblog.utils.http.api.APIStrategy;
import com.ruslan_website.travelblog.utils.http.model.User;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Auth {

    public static void login(final APIStrategy apiStrategy, final SharedPreferencesManagement mSPM,
                                   final Activity activity, final ProgressBar progressBar,
                                   final Button[] changingButtons, String clientId, String clientSecret,
                                   String grantType, String type, String username, String password) {

        Call<ResponseBody> tokenRequest = apiStrategy.obtainToken(clientId, clientSecret, grantType, type, username, password);

        tokenRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if( response.code() == 200 && response.isSuccessful() ){
                    try {
                        JSONObject jsonObj = new JSONObject(response.body().string());
                        mSPM.setAccessToken(jsonObj.getString("access_token"));
                        Log.i("token", jsonObj.getString("access_token"));
                        obtainUserInfo(apiStrategy, mSPM, activity, progressBar, changingButtons);
                    } catch (Exception e) {
                        String toast = "Update your app. App will close.";
                        String log = "Success but cant parse JSON: " + e.getMessage();
                        UI.setProgressStatus(activity, false, progressBar, changingButtons, toast, log);
                        e.printStackTrace();
                    }
                }else{
                    String errorBody;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        errorBody = "Cant get errorBody: " + e.getMessage();
                        e.printStackTrace();
                    }
                    String toast = "Update your app. App will close.";
                    String log = "ERROR-BODY: " + errorBody;
                    UI.setProgressStatus(activity, false, progressBar, changingButtons, toast, log);
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String toast = "Update your app. App will close.";
                String log = "onFailure: " + t.getMessage();
                UI.setProgressStatus(activity, false, progressBar, changingButtons, toast, log);
                t.printStackTrace();
            }
        });

    }

    private static void obtainUserInfo(APIStrategy apiStrategy, final SharedPreferencesManagement mSPM,
                                       final Activity activity, final ProgressBar progressBar,
                                       final Button[] changingButtons) {

        Call<User> userRequest = apiStrategy.obtainUserInfo( mSPM.getAccessToken() );

        userRequest.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mSPM.setUserId( response.body().getId() );
                mSPM.setUsername( String.valueOf(response.body().getName()) );
                String toast = "Login Succeeded";
                String log = "UserInfo: " + String.valueOf(response.body().getName()) + " " +
                        String.valueOf(response.body().getId());
                UI.setProgressStatus(activity, false, progressBar, changingButtons, toast, log);
                Intent intent = new Intent(activity, EntryActivity.class);
                activity.startActivity(intent);
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String toast = "Update your app. App will close.";
                String log = "UserInfo - Error: " + t.getMessage();
                UI.setProgressStatus(activity, false, progressBar, changingButtons, toast, log);
            }
        });
    }

}