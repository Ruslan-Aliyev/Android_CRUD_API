package com.ruslan_website.travelblog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ruslan_website.travelblog.utils.common.Network;
import com.ruslan_website.travelblog.utils.http.model.User;
import com.ruslan_website.travelblog.utils.http.service.TokenService;
import com.ruslan_website.travelblog.utils.http.service.UserService;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferencesManagement mSPM;

    private TokenService tokenService;
    private UserService userService;

    private static final int PERMISSION_QUERY_CODE = 123;
    private String[] permissions;

    @BindView(R.id.userEmail) EditText userEmail;
    @BindView(R.id.userPassword)  EditText userPassword;
    @BindView(R.id.bLogin) Button bLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        ButterKnife.bind(this);

        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }

        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        getPermissions(LoginActivity.this, permissions);

        if(!Network.isConnected()) {
            Toast.makeText(LoginActivity.this, "Travel Blog needs internet", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void getPermissions(Activity activity, String[] permissions) {
        for(int i = 0; i < permissions.length; i++){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{permissions[i]}, PERMISSION_QUERY_CODE);
                    return;
                } else {
                    prepareObtainToken();
                }
            }else{
                if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                } else {
                    prepareObtainToken();
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == PERMISSION_QUERY_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                prepareObtainToken();
            }else{

            }
        }
    }

    private void prepareObtainToken() {

        if( mSPM.getAccessToken() != null ){
            obtainUserInfo();
        }

        OkHttpClient client = makeHttpClient();
        tokenService = new Retrofit.Builder()
                .baseUrl( mSPM.getUrl() )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TokenService.class);

    }

    private OkHttpClient makeHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    @OnClick(R.id.bLogin)
    public void login(View view){

        if( userEmail.getText().toString().length() == 0 ){
            userEmail.setError( "User Email is required!" );
        }
        if( userPassword.getText().toString().length() == 0 ){
            userPassword.setError( "User Password is required!" );
        }

        String clientId = String.valueOf(mSPM.getClientId());
        String clientSecret = mSPM.getClientSecret();
        String grantType = "password";
        String username = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        Toast.makeText(LoginActivity.this, "Loading ...", Toast.LENGTH_LONG).show();

        obtainToken(clientId, clientSecret, grantType, username, password);
    }

    private void obtainToken(String clientId, String clientSecret, String grantType, String username, String password) {

        Call<ResponseBody> tokenRequest = tokenService.obtain(clientId, clientSecret, grantType, username, password);

        tokenRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if( response.code() == 200 && response.isSuccessful() ){
                    try {
                        JSONObject jsonObj = new JSONObject(response.body().string());
                        mSPM.setAccessToken(jsonObj.getString("access_token"));
                        Log.i("token", jsonObj.getString("access_token"));
                        obtainUserInfo();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Update your app. App will close.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Update your app. App will close.", Toast.LENGTH_LONG).show();
                    try {
                        Log.i("ERROR-BODY", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("onFailure", t.getMessage());
                t.printStackTrace();
            }
        });

    }

    private void obtainUserInfo() {

        OkHttpClient client = makeHttpClient();
        userService = new Retrofit.Builder()
                .baseUrl( mSPM.getUrl() )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserService.class);

        Call<User> userReq = userService.obtain("application/json", "Bearer " + mSPM.getAccessToken());

        userReq.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mSPM.setUserId( response.body().getId() );
                mSPM.setUsername( String.valueOf(response.body().getName()) );
                Log.i("UserInfo", String.valueOf(response.body().getName()) + " " + String.valueOf(response.body().getId()));
                Intent intent = new Intent(LoginActivity.this, EntryActivity.class);
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Update your app. App will close.", Toast.LENGTH_LONG).show();
                Log.i("UserInfo - Error", t.getMessage());
            }
        });
    }
}
