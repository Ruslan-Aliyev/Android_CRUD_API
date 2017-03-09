package com.ruslan_website.travelblog;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ruslan_website.travelblog.utils.AnimatorUtil;
import com.ruslan_website.travelblog.utils.common.Auth;
import com.ruslan_website.travelblog.utils.common.Network;
import com.ruslan_website.travelblog.utils.common.UI;
import com.ruslan_website.travelblog.utils.gcm.GCM;
import com.ruslan_website.travelblog.utils.http.api.APIFactory;
import com.ruslan_website.travelblog.utils.http.api.APIStrategy;
import com.ruslan_website.travelblog.utils.http.model.User;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;
import com.ruslan_website.travelblog.utils.view.RoundVideo;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements SurfaceHolder.Callback, Animator.AnimatorListener {

    private SharedPreferencesManagement mSPM;

    private APIFactory apiFactory;
    private APIStrategy apiStrategy;

    private GCM gcm;

    private static final int PERMISSION_QUERY_CODE = 123;
    private String[] permissions;

    @BindView(R.id.root) RelativeLayout root;
    @BindView(R.id.userEmail) EditText userEmail;
    @BindView(R.id.userPassword)  EditText userPassword;
    @BindView(R.id.bRegister) Button bRegister;
    @BindView(R.id.bLogin) Button bLogin;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.plane) ImageView plane;
    private Button[] changingButtons;

    private RoundVideo sv;
    private MediaPlayer svMp;

    private AnimatorUtil animatorUtil;
    private ObjectAnimator bounce1, bounce2, bounce3, planeMoveX, planeMoveY;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sv = (RoundVideo) findViewById(R.id.sv);
        SurfaceHolder holder = sv.getHolder();
        holder.addCallback(this);
        svMp = MediaPlayer.create(this, R.raw.flight);

        tts = new TextToSpeech(LoginActivity.this, new TextToSpeech.OnInitListener(){
            public void onInit(int status){
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.US);
                    tts.speak("Welcome to travel blog", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        ButterKnife.bind(this);

        // String imagePath = getExternalFilesDir(DIRECTORY_PICTURES).getAbsolutePath() + "/pictures/";
        // plane.setImageBitmap(BitmapFactory.decodeFile(new File(imagePath + "plane.jpg").getAbsolutePath()));

        changingButtons = new Button[]{bRegister, bLogin};

        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }
        mSPM.setBackendOption("laravel");

        apiFactory = new APIFactory( mSPM.getBackendOption() );
        apiStrategy = apiFactory.getApiStrategy();

        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                ,Manifest.permission.ACCESS_COARSE_LOCATION
                ,Manifest.permission.ACCESS_FINE_LOCATION
        };

        getPermissions(LoginActivity.this, permissions);

        animate();

        if (gcm == null) {
            gcm = GCM.getInstance();
        }
        gcm.init(LoginActivity.this);

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
                    //Toast.makeText(LoginActivity.this, "Required permissions obtained", Toast.LENGTH_SHORT).show();
                }
            }else{
                if (ContextCompat.checkSelfPermission(activity.getBaseContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {

                } else {
                    //Toast.makeText(LoginActivity.this, "Required permissions obtained", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == PERMISSION_QUERY_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(LoginActivity.this, "Required permissions obtained", Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(LoginActivity.this, "Fail to get required permissions", Toast.LENGTH_SHORT).show();
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        svMp.setDisplay(holder);
        svMp.setVolume(0,0);
        svMp.setLooping(true);
        svMp.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void animate() {

        if(animatorUtil == null) {
            animatorUtil = AnimatorUtil.getInstance();
        }

//        root.post(new Runnable(){
//            @Override
//            public void run() {
//                // animation codes
//            }
//        });

        plane.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Don't forget to remove your listener when you are done with it.
                        plane.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // Bounce scales are for the scale animation of Welcome Screen's blue round start button
                        float BOUNCE_SCALE_1 = 0;
                        float BOUNCE_SCALE_2 = 1.1f;
                        float BOUNCE_SCALE_3 = 0.9f;
                        float BOUNCE_SCALE_4 = 1;

                        bounce1 = animatorUtil.scale(plane, BOUNCE_SCALE_1, BOUNCE_SCALE_2, BOUNCE_SCALE_1, BOUNCE_SCALE_2, 500);
                        bounce2 = animatorUtil.scale(plane, BOUNCE_SCALE_2, BOUNCE_SCALE_3, BOUNCE_SCALE_2, BOUNCE_SCALE_3, 150);
                        bounce3 = animatorUtil.scale(plane, BOUNCE_SCALE_3, BOUNCE_SCALE_4, BOUNCE_SCALE_3, BOUNCE_SCALE_4, 150);

                        AnimatorSet bounce = new AnimatorSet();
                        bounce.play(bounce1).before(bounce2);
                        bounce.play(bounce3).after(bounce2);

                        float initX = (float) plane.getX();
                        float initY = (float) plane.getY();
                        float finalX = 0;
                        float finalY = initY - 300;
                        planeMoveX = animatorUtil.move(plane, "x", initX, finalX, 2000);
                        planeMoveY = animatorUtil.move(plane, "y", initY, finalY, 2000);
                        planeMoveX.addListener(LoginActivity.this);
                        planeMoveY.addListener(LoginActivity.this);

                        AnimatorSet welcomeAnimations = new AnimatorSet();
                        welcomeAnimations.play(planeMoveX).after(bounce);
                        welcomeAnimations.play(planeMoveX).with(planeMoveY);
                        welcomeAnimations.start();
                    }
                });
    }

    @Override
    public void onAnimationStart(Animator animation) {

        if(animation.equals(planeMoveY)){
            plane.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(animation.equals(planeMoveY)){
            plane.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
