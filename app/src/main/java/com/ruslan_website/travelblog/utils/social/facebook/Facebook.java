package com.ruslan_website.travelblog.utils.social.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Facebook {

    private CallbackManager callbackManager;
    private boolean isLoggedIn = false;
    private URL facebookProfileURL;
    private JSONObject userInfo;
    private static final int FB_LOGIN_CODE = 1;
    private static final int FB_PIC_CODE = 2;
    public static final String TAG = "-FACEBOOK-";
    private Activity mActivity;
    private Callback mCallback;
    private AccessToken fbAccessToken;

    private static Facebook facebook = new Facebook( );

    private Facebook() { }

    public static Facebook getInstance( ) {
        return facebook;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public void init(Context context){
        FacebookSdk.sdkInitialize(context);
        callbackManager = CallbackManager.Factory.create();
    }

    public void signIn(Activity activity, Callback callback){
        mActivity = activity;
        mCallback = callback;
        Thread fbLoginThread = new Thread(new Runnable(){
            @Override
            public void run() {
                facebookLogin(mActivity);
            }
        });
        fbLoginThread.start();
    }

    private void facebookLogin(Activity activity){
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email", "user_friends") );
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFbUserInfo(loginResult);
            }
            @Override
            public void onCancel() {
                mCallback.onError("Login Cancelled");
            }
            @Override
            public void onError(FacebookException e) {
                mCallback.onError("Login Error: " + e.getMessage() );
            }
        });
    }

    private void getFbUserInfo(LoginResult loginResult){

        fbAccessToken = loginResult.getAccessToken();

        GraphRequest request = GraphRequest.newMeRequest( fbAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                Message msg = Message.obtain();
                msg.what = FB_LOGIN_CODE;
                msg.obj = jsonObject;
                handler.sendMessage(msg);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,link,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void processFbUserInfo(){
        try {
            String fbPicUrl = userInfo.getJSONObject("picture").getJSONObject("data").getString("url");
            facebookProfileURL = new URL(fbPicUrl);
            Thread getPicThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getProfilePic();
                }
            });
            getPicThread.start();
        } catch (JSONException e) {
            mCallback.onError("Improper JSON: " + e.getMessage() );
            e.printStackTrace();
        } catch (MalformedURLException e) {
            mCallback.onError("Improper URL: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    private void getProfilePic(){
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(facebookProfileURL.openConnection().getInputStream());
            Message msg = Message.obtain();
            msg.what = FB_PIC_CODE;
            msg.obj = bitmap;
            handler.sendMessage(msg);
        } catch (IOException e) {
            mCallback.onError("Fail to download picture: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case FB_LOGIN_CODE:
                    userInfo = (JSONObject) msg.obj;
                    processFbUserInfo();
                    break;
                case FB_PIC_CODE:
                    isLoggedIn = true;
                    mCallback.onSuccess(userInfo, (Bitmap)msg.obj, fbAccessToken);
                    break;
            }
        }
    };

    public void logout(){
        LoginManager.getInstance().logOut();
        isLoggedIn = false;
        return;
    }
}
