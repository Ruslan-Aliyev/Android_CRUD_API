package com.ruslan_website.travelblog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ruslan_website.travelblog.utils.TravelBlogApplication;
import com.ruslan_website.travelblog.utils.social.facebook.Callback;
import com.ruslan_website.travelblog.utils.social.facebook.Facebook;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    private SharedPreferencesManagement mSPM;

    @BindView(R.id.name) EditText name;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.pw1) EditText pw1;
    @BindView(R.id.pw2) EditText pw2;
    @BindView(R.id.register) Button register;
    @BindView(R.id.bBackToLogin) Button bBackToLogin;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.fbSignin) ImageButton fbSignin;
    @BindView(R.id.ggSignin) ImageButton ggSignin;
    private Facebook facebook;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        facebookSetup();
        facebook.init(context);
    }

    private void facebookSetup(){
        if(context == null){
            context = TravelBlogApplication.getContext();
        }
        if(facebook == null){
            facebook = Facebook.getInstance();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        facebookSetup();
    }

    private void init() {
        ButterKnife.bind(this);

        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }
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
        }
        if( email.getText().toString().length() == 0 ){
            email.setError( "Email is required!" );
        }
        if( pw1.getText().toString().length() < 6 ){
            pw1.setError( "Password must be 6 letters long minimum!" );
        }
        if( pw2.getText().toString().length() < 6 ){
            pw2.setError( "Confirm Password must be 6 letters long minimum!" );
        }

        if( !android.util.Patterns.EMAIL_ADDRESS.matcher( email.getText().toString() ).matches() ){
            email.setError( "Wrong email format!" );
        }
        if( pw1.getText().toString().equals( pw2.getText().toString() ) ){
            pw2.setError( "Passwords don't match!" );
        }
    }

    @OnClick(R.id.fbSignin)
    public void fbSignin(View view){

        if(facebook.isLoggedIn()){
            facebook.logout();
        }

        facebook.login(RegisterActivity.this, new Callback(){
            @Override
            public void onSuccess(JSONObject result, Bitmap profilePic) {
                setUI(result, profilePic);
            }
            @Override
            public void onError(String errorMsg) {
                Log.d(facebook.TAG, errorMsg);
            }
        });
    }

    private void setUI(JSONObject result, Bitmap profilePic){

        if(result == null && profilePic == null){
//            editName.setText("");
//            editId.setText("");
//            editEmail.setText("");
//            pic.setImageBitmap(null);
//            return;
        }

//        try {
//            editName.setText(result.getString("name"));
//            editId.setText(result.getString("id"));
//            editEmail.setText(result.getString("email"));
//            pic.setImageBitmap(profilePic);
//        } catch (JSONException e) {
//            Log.d(facebook.TAG, "Improper Results JSON: " + e.getMessage());
//            e.printStackTrace();
//        }
    }

    @OnClick(R.id.ggSignin)
    public void ggSignin(View view){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebook.onActivityResult(requestCode, resultCode, data);
    }

}
