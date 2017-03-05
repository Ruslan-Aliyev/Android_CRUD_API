package com.ruslan_website.travelblog.utils.social.google;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.ruslan_website.travelblog.RegisterActivity;
import java.io.IOException;

public class Google {

    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    public int googleRequestCode;
    public final static int GOOGLE_LOGIN_INTENT_CODE = 789;
    private RegisterActivity mRegisterActivity;
    private Callback mCallback;
    private String userId;
    private String userName;
    private String userEmail;
    private String accessToken;
    private Boolean isLoggedIn = false;

    public Google() {

    }
    static private class SingletonHelper {
        private final static Google INSTANCE = new Google();
    }
    public static Google getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void init(RegisterActivity registerActivity){

        mRegisterActivity = registerActivity;

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId ()
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mRegisterActivity)
                    .addConnectionCallbacks(mRegisterActivity)
                    .enableAutoManage(mRegisterActivity, mRegisterActivity /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        if (mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    public void onConnected(Bundle bundle) {
        Log.i("Google SignIn", "Connected");
    }

    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.i("Google SignIn Failed", connectionResult.getErrorMessage() );
        if(!connectionResult.hasResolution()){
            Log.i("Google SignIn Failed", "Req Code: " + String.valueOf(googleRequestCode) );
            Log.i("Google SignIn Failed", "Err Code: " + String.valueOf( connectionResult.getErrorCode() ) );
        }
    }

    public void onStop(){
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(mRegisterActivity);
            mGoogleApiClient.disconnect();
        }
    }

    public void googleSignIn(Callback callback){
        mCallback = callback;

        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mRegisterActivity.startActivityForResult(googleSignInIntent, GOOGLE_LOGIN_INTENT_CODE);
    }

    public void googleSignOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback( new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if(status.isSuccess()){
                    isLoggedIn = false;
                }
                Log.i("Google SignOut", "-"+String.valueOf(status.isSuccess()) );
            }
        });
    }

    public void googleDisassociate(){
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback( new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if(status.isSuccess()){
                    isLoggedIn = false;
                }
                Log.i("Google Disassociate", "-"+String.valueOf(status.isSuccess()) );
            }
        });
    }

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    public void onGoogleSigninResult(int requestCode, int resultCode, Intent data) {
        googleRequestCode = requestCode;
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult( result );
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) { // Signed in successfully, authenticated
            isLoggedIn = true;
            GoogleSignInAccount acct = result.getSignInAccount();
            userId = acct.getId();
            userEmail = acct.getEmail();
            userName = acct.getDisplayName();
            new RetrieveTokenTask().execute( acct.getEmail() );
        } else { // Signed out, unauthenticated
            mCallback.onError("Google SignIn Error" + String.valueOf( result.getStatus() ) );
        }
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(mRegisterActivity.getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                mCallback.onError("IOException" + e.getMessage());
            } catch (UserRecoverableAuthException e) {
                mCallback.onError("UserRecoverableAuthEx" + e.getMessage());
            } catch (GoogleAuthException e) {
                mCallback.onError("GoogleAuthException" + e.getMessage());
            }
            return token;
        }
        @Override
        protected void onPostExecute(String t) {
            super.onPostExecute(t);
            accessToken = t;
            Log.i("Google Access Token", accessToken);
            mCallback.onSuccess(userId, userName, userEmail, accessToken);
        }
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
