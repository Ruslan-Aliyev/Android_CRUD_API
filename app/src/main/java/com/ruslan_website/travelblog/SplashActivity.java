package com.ruslan_website.travelblog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.ruslan_website.travelblog.utils.AnimatorUtil;
import com.ruslan_website.travelblog.utils.view.RoundVideo;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements Animator.AnimatorListener, SurfaceHolder.Callback {

    @BindView(R.id.plane) ImageView plane;

    private AnimatorUtil animatorUtil;
    private ObjectAnimator bounce1, bounce2, bounce3, planeMoveX, planeMoveY;

    private TextToSpeech tts;

    private RoundVideo sv;
    private MediaPlayer svMp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        // String imagePath = getExternalFilesDir(DIRECTORY_PICTURES).getAbsolutePath() + "/pictures/";
        // plane.setImageBitmap(BitmapFactory.decodeFile(new File(imagePath + "plane.jpg").getAbsolutePath()));

        animate();

        tts = new TextToSpeech(SplashActivity.this, new TextToSpeech.OnInitListener(){
            public void onInit(int status){
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.US);

                    String text = "Welcome to travel blog";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
                    } else {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

        sv = (RoundVideo) findViewById(R.id.sv);
        SurfaceHolder holder = sv.getHolder();
        holder.addCallback(this);
        svMp = MediaPlayer.create(this, R.raw.flight);
    }

    @Override
    protected void onPause() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
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
                        float finalY = 0;
                        planeMoveX = animatorUtil.move(plane, "x", initX, finalX, 2000);
                        planeMoveY = animatorUtil.move(plane, "y", initY, finalY, 2000);
                        planeMoveX.addListener(SplashActivity.this);
                        planeMoveY.addListener(SplashActivity.this);

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

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

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
}
