package com.ruslan_website.travelblog.utils.storage;


import android.content.Context;
import android.content.SharedPreferences;

import com.ruslan_website.travelblog.utils.TravelBlogApplication;
import com.ruslan_website.travelblog.utils.common.PathCombiner;

public class SharedPreferencesManagement {

    private static final String SP_TAG = "TravelBlog";
    private static final int SP_MODE = Context.MODE_PRIVATE;
    private SharedPreferences mSP;
    private SharedPreferences.Editor mPE;
    private Context mCtx;


    public SharedPreferencesManagement() {
        this.mCtx = TravelBlogApplication.getContext();
        this.mSP = this.mCtx.getSharedPreferences(SP_TAG, SP_MODE);
        this.mPE = this.mSP.edit();
    }
    static private class SingletonHelper {
        private final static SharedPreferencesManagement INSTANCE = new SharedPreferencesManagement();
    }
    public static SharedPreferencesManagement getInstance() {
        return SingletonHelper.INSTANCE;
    }



    public void setClientId(int clientId) {
        this.mPE.putInt("clientId", clientId);
        this.mPE.commit();
    }
    public int getClientId() {
        return this.mSP.getInt("clientId", 2);
    }

    public void setClientSecret(String clientSecret) {
        this.mPE.putString("clientSecret", clientSecret);
        this.mPE.commit();
    }
    public String getClientSecret() {
        return this.mSP.getString("clientSecret", "9KHx3WW04RF0Gf2msS7dsKkPOpg6DNeH9uu7OvJh");
    }

    public void setUserEmail(String userEmail) {
        this.mPE.putString("userEmail", userEmail);
        this.mPE.commit();
    }
    public String getUserEmail() {
        return this.mSP.getString("userEmail", null);
    }

    public void setUserPassword(String userPassword) {
        this.mPE.putString("userPassword", userPassword);
        this.mPE.commit();
    }
    public String getUserPassword() {
        return this.mSP.getString("userPassword", null);
    }

    public void setBackendOption(String backendOption){
        this.mPE.putString("backendOption", backendOption);
        this.mPE.commit();
    }
    public String getBackendOption() {
        return this.mSP.getString("backendOption", "laravel");
    }

    public String getLaravelUrl() {
        return this.mSP.getString("url", "http://ruslan-website.com/");
    }
    public String getCakeUrl() {
        return this.mSP.getString("url", "");
    }


    public void setAccessToken(String accessToken) {
        this.mPE.putString("accessToken", accessToken);
        this.mPE.commit();
    }
    public String getAccessToken() {
        return this.mSP.getString("accessToken", null);
    }

    public void setRefreshToken(String refreshToken) {
        this.mPE.putString("refreshToken", refreshToken);
        this.mPE.commit();
    }
    public String getRefreshToken() {
        return this.mSP.getString("refreshToken", null);
    }

    public void setTokenType(String tokenType) {
        this.mPE.putString("tokenType", tokenType);
        this.mPE.commit();
    }
    public String getTokenType() {
        return this.mSP.getString("tokenType", null);
    }

    public void setTokenExpiry(int tokenExpiry) {
        this.mPE.putInt("tokenExpiry", tokenExpiry);
        this.mPE.commit();
    }
    public int getTokenExpiry() {
        return this.mSP.getInt("tokenExpiry", 0);
    }

    public int getUserId() {
        return this.mSP.getInt("userId", 1);
    }
    public void setUserId(int userId) {
        this.mPE.putInt("userId", userId);
        this.mPE.commit();
    }

    public void setUsername(String username) {
        this.mPE.putString("username", username);
        this.mPE.commit();
    }
    public String getUsername() {
        return this.mSP.getString("username", null);
    }

    public String getAppImgDirName() {
        return this.mSP.getString("appImgDirName", SP_TAG);
    }
    public String getAppTempImgDirName() {
        return this.mSP.getString("appTempImgDirName", PathCombiner.combine(SP_TAG, "temp") );
    }
}
