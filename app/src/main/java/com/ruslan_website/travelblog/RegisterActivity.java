package com.ruslan_website.travelblog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ruslan_website.travelblog.utils.TravelBlogApplication;
import com.ruslan_website.travelblog.utils.common.Auth;
import com.ruslan_website.travelblog.utils.common.UI;
import com.ruslan_website.travelblog.utils.http.api.APIFactory;
import com.ruslan_website.travelblog.utils.http.api.APIStrategy;
import com.ruslan_website.travelblog.utils.social.facebook.Facebook;
import com.ruslan_website.travelblog.utils.social.google.Google;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks  {

    private SharedPreferencesManagement mSPM;

    APIFactory apiFactory;
    APIStrategy apiStrategy;

    @BindView(R.id.name) EditText name;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.pw1) EditText pw1;
    @BindView(R.id.pw2) EditText pw2;
    @BindView(R.id.register) Button register;
    @BindView(R.id.bBackToLogin) Button bBackToLogin;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.fbSignin) ImageButton fbSignin;
    @BindView(R.id.ggSignin) ImageButton ggSignin;
    private Context context;
    private Facebook facebook;
    private Google google;
    private Button[] changingButtons;

    String userName;
    String userEmail;
    String password;
    String type;
    String socialId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        logKeyHash();
    }

    private void logKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("KeyHash NameNotFound:", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.i("KeyHash NoAlgorithm:", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        ButterKnife.bind(this);

        changingButtons = new Button[]{register, bBackToLogin};

        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }

        apiFactory = new APIFactory( mSPM.getBackendOption() );
        apiStrategy = apiFactory.getApiStrategy();

        if(context == null){
            context = TravelBlogApplication.getContext();
        }
        if(facebook == null){
            facebook = Facebook.getInstance();
        }
        facebook.init(context);

        if(google == null){
            google = Google.getInstance();
        }
        google.init(this);
    }

    @OnClick(R.id.bBackToLogin)
    public void bBackToLogin(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.register)
    public void register(View view){

        if( name.getText().toString().length() == 0 ){
            name.setError( "Name is required!" );
            return;
        }
        if( email.getText().toString().length() == 0 ){
            email.setError( "Email is required!" );
            return;
        }
        if( pw1.getText().toString().length() < 6 ){
            pw1.setError( "Password must be 6 letters long minimum!" );
            return;
        }
        if( pw2.getText().toString().length() < 6 ){
            pw2.setError( "Confirm Password must be 6 letters long minimum!" );
            return;
        }

        if( !android.util.Patterns.EMAIL_ADDRESS.matcher( email.getText().toString() ).matches() ){
            email.setError( "Wrong email format!" );
            return;
        }
        if( !pw1.getText().toString().equals( pw2.getText().toString() ) ){
            pw2.setError( "Passwords don't match!" );
            return;
        }

        userName = name.getText().toString();
        userEmail = email.getText().toString();
        password = pw1.getText().toString();
        type = "normal";
        socialId = "";

        String toast = "Registering ...";
        String log = "Register button pressed";
        UI.setProgressStatus(RegisterActivity.this, true, progressBar, changingButtons, toast, log);

        createNewUser(userName, userEmail, password, type, socialId);
    }

    @OnClick(R.id.fbSignin)
    public void fbSignin(View view){

        String toast = "Signing into Facebook ...";
        String log = "Facebook SignIn button pressed";
        UI.setProgressStatus(RegisterActivity.this, true, progressBar, changingButtons, toast, log);

        if(facebook.isLoggedIn()){
            facebook.logout();
        }

        facebook.signIn(RegisterActivity.this, new com.ruslan_website.travelblog.utils.social.facebook.Callback(){
            @Override
            public void onSuccess(JSONObject result, Bitmap avatar) {
                processFacebookResult(result, avatar);
            }
            @Override
            public void onError(String errorMsg) {
                String toast = "Update your app. App will close.";
                String log = "Facebook signin error: " + errorMsg;
                UI.setProgressStatus(RegisterActivity.this, false, progressBar, changingButtons, toast, log);
            }
        });
    }

    private void processFacebookResult(JSONObject result, Bitmap avatar){

        if(result == null && avatar == null){
            String toast = "Update your app. App will close.";
            String log = "Facebook result or avatar null";
            UI.setProgressStatus(RegisterActivity.this, false, progressBar, changingButtons, toast, log);
        }

        try {
            userName = result.getString("name");
            userEmail = result.getString("email");
            password = "random"; // to be improved later
            type = "facebook";
            socialId = result.getString("id");
            createNewUser(userName, userEmail, password, type, socialId);
        } catch (JSONException e) {
            String toast = "Update your app. App will close.";
            String log = "Facebook result JSON parse problem: " + e.getMessage();
            UI.setProgressStatus(RegisterActivity.this, false, progressBar, changingButtons, toast, log);
            e.printStackTrace();
        }
    }

    @OnClick(R.id.ggSignin)
    public void ggSignin(View view){

        String toast = "Signing into Google ...";
        String log = "Google SignIn button pressed";
        UI.setProgressStatus(RegisterActivity.this, true, progressBar, changingButtons, toast, log);

        google.googleSignIn(new com.ruslan_website.travelblog.utils.social.google.Callback(){

            @Override
            public void onSuccess(String socialId, String userName, String userEmail, String accessToken) {
                createNewUser(userName, userEmail, "random", "google", socialId);
            }

            @Override
            public void onError(String errorMsg) {
                String toast = "Update your app. App will close.";
                String log = "Google signin err: " + errorMsg;
                UI.setProgressStatus(RegisterActivity.this, false, progressBar, changingButtons, toast, log);
            }
        });
    }

    private void createNewUser(String userName, final String userEmail, final String password, final String type, String socialId) {

        Log.i("NewUserCheck: ", userName + " " + userEmail + " " + password + " " + type + " " + socialId);

        Call<ResponseBody> newUserRequest = apiStrategy.createNewUser(userName, userEmail, password, type, socialId);

        newUserRequest.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if( response.code() == 200 && response.isSuccessful() ){

                    String clientId = String.valueOf(mSPM.getClientId());
                    String clientSecret = mSPM.getClientSecret();
                    String grantType = "password";

                    Auth.login(apiStrategy, mSPM, RegisterActivity.this, progressBar, changingButtons, clientId,
                            clientSecret, grantType, type, userEmail, password);
                }else{
                    String toast = "Update your app. App will close.";
                    String log = "Backend create user response unsuccessful" + response.message();
                    UI.setProgressStatus(RegisterActivity.this, false, progressBar, changingButtons, toast, log);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String toast = "Update your app. App will close.";
                String log = "Create user unsuccessful" + t.getMessage();
                UI.setProgressStatus(RegisterActivity.this, false, progressBar, changingButtons, toast, log);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == google.GOOGLE_LOGIN_INTENT_CODE) {
            google.onGoogleSigninResult(requestCode, resultCode, data);
        }else {
            facebook.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        google.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        google.onConnected(bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        google.onConnectionSuspended(i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        google.onConnectionFailed(connectionResult);
    }
}
