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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ruslan_website.travelblog.utils.common.Auth;
import com.ruslan_website.travelblog.utils.common.Network;
import com.ruslan_website.travelblog.utils.common.UI;
import com.ruslan_website.travelblog.utils.gcm.GCM;
import com.ruslan_website.travelblog.utils.http.api.APIFactory;
import com.ruslan_website.travelblog.utils.http.api.APIStrategy;
import com.ruslan_website.travelblog.utils.http.model.User;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferencesManagement mSPM;

    private APIFactory apiFactory;
    private APIStrategy apiStrategy;

    private GCM gcm;

    private static final int PERMISSION_QUERY_CODE = 123;
    private String[] permissions;

    @BindView(R.id.userEmail) EditText userEmail;
    @BindView(R.id.userPassword)  EditText userPassword;
    @BindView(R.id.bRegister) Button bRegister;
    @BindView(R.id.bLogin) Button bLogin;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    private Button[] changingButtons;

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

        changingButtons = new Button[]{bRegister, bLogin};

        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }
        mSPM.setBackendOption("laravel");

        apiFactory = new APIFactory( mSPM.getBackendOption() );
        apiStrategy = apiFactory.getApiStrategy();

        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                //,Manifest.permission.WRITE_CONTACTS
        };

        getPermissions(LoginActivity.this, permissions);

        if (gcm == null) {
            gcm = GCM.getInstance();
        }
        gcm.init(LoginActivity.this);

        if(!Network.isConnected()) {
            Toast.makeText(LoginActivity.this, "Travel Blog needs internet", Toast.LENGTH_LONG).show();
            return;
        }

        if(mSPM.getAccessToken() != null){
            Intent intent = new Intent(LoginActivity.this, EntryActivity.class);
            startActivity(intent);
        }
    }

    private void getPermissions(Activity activity, String[] permissions) {
        for(int i = 0; i < permissions.length; i++){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{permissions[i]}, PERMISSION_QUERY_CODE);
                    return;
                } else {
                    Toast.makeText(LoginActivity.this, "Required permissions obtained", Toast.LENGTH_SHORT).show();
                }
            }else{
                if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(LoginActivity.this, "Required permissions obtained", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == PERMISSION_QUERY_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(LoginActivity.this, "Required permissions obtained", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this, "Fail to get required permissions", Toast.LENGTH_SHORT).show();
                return;
            }
        }
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
        String type = "normal";
        String username = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        String toast = "Logging in ...";
        String log = "Login button pressed";
        UI.setProgressStatus(LoginActivity.this, true, progressBar, changingButtons, toast, log);

        Auth.login(apiStrategy, mSPM, LoginActivity.this, progressBar, changingButtons, clientId,
                clientSecret, grantType, type, username, password);
    }

    @OnClick(R.id.bRegister)
    public void toRegister(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
