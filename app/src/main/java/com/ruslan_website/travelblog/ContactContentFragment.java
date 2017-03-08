package com.ruslan_website.travelblog;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ruslan_website.travelblog.utils.email.Mail;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;
import com.ruslan_website.travelblog.utils.view.RoundIcon;
import com.ruslan_website.travelblog.utils.webview.JSInterface;
import com.ruslan_website.travelblog.utils.webview.WebviewHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactContentFragment extends Fragment implements SurfaceHolder.Callback {

    private static final int EMAIL_CODE = 11;

    @BindView(R.id.animatedThumb) RoundIcon animatedThumb;
    @BindView(R.id.rootRatingBar) RelativeLayout rootRatingBar;
    @BindView(R.id.line) ImageView line;
    @BindView(R.id.ratingSeekBar) SeekBar ratingSeekBar;
    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.bSendRating) Button bSendRating;
    @BindView(R.id.feedback) TextView feedback;
    private float ratingValue = -1;
    private MediaPlayer mMediaPlayer;
    private SharedPreferencesManagement mSPM;
    private Activity parentActivity;
    private WebviewHandler webviewHandler;
    private int currProgress;
    private static final int STEP = 25;

    @OnClick(R.id.bSendRating)
    public void sendRating(View view) {
        Thread sendEmailDirectlyThread = new Thread(new Runnable(){
            @Override
            public void run() {
                sendEmailDirectly();
            }
        });
        sendEmailDirectlyThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contact_content_fragment, container, false);
        ButterKnife.bind(this, view);

        parentActivity = super.getActivity();

        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }

        if (webviewHandler == null) {
            webviewHandler = WebviewHandler.getInstance();
        }
        webviewHandler.init(webView, ContactContentFragment.this);
        webviewHandler.handleFileUpload();
        webView.addJavascriptInterface(new JSInterface(parentActivity), "Android");

        webView.setVisibility(View.VISIBLE);
        rootRatingBar.setVisibility(View.GONE);

        SurfaceHolder holder = animatedThumb.getHolder();
        holder.addCallback(this);
        mMediaPlayer = MediaPlayer.create(parentActivity, R.raw.emoticonface);

        rootRatingBar.post(new Runnable(){
            @Override
            public void run(){
                initView();
            }
        });

        ratingSeekBar.setProgressDrawable(ContextCompat.getDrawable(parentActivity, R.drawable.line_invisible));
        ratingSeekBar.setThumb(ContextCompat.getDrawable(parentActivity, R.drawable.invisible_thumb));
        ratingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currProgress = progress;
                int max = seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                float percent = ((float)currProgress)/(float)max;
                int width = seekBar.getWidth() - 2*offset;
                int x =((int)((width*percent+offset) - (animatedThumb.getWidth()/2) ));
                animatedThumb.setX(x);

                int duration = mMediaPlayer.getDuration();
                int videoPos = duration*currProgress/max;
                Log.i("Video", String.valueOf(videoPos) + " = " + String.valueOf(duration) + " * " + String.valueOf(currProgress) + " / " + String.valueOf(max));
                mMediaPlayer.seekTo(videoPos);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currProgress = ((int)Math.round(currProgress/STEP ))*STEP;
                seekBar.setProgress(currProgress);
                ratingValue = (currProgress/STEP) + 1;
                Log.i("Feedback", String.valueOf(ratingValue));
                feedback.setText("Feedback: " + String.valueOf(ratingValue));
            }
        });

        return view;
    }

    public void change(int optionNumber){
        switch(optionNumber){
            case 1:
                webView.setVisibility(View.VISIBLE);
                rootRatingBar.setVisibility(View.GONE);
                webView.loadUrl( mSPM.getPHPContactFormUrl() );
                break;
            case 2:
                webView.setVisibility(View.VISIBLE);
                rootRatingBar.setVisibility(View.GONE);
                webView.loadUrl( mSPM.getAjaxContactFormUrl() );
                break;
            case 3:
                webView.setVisibility(View.GONE);
                rootRatingBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initView(){
        animatedThumb.setX(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == WebviewHandler.FILECHOOSER_RESULTCODE) {
            webviewHandler.onHandleFileUploadActivityResult(resultCode, intent);
        }else{
            super.onActivityResult(requestCode, resultCode, intent);
        }
        return;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
        mMediaPlayer.seekTo(0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void sendEmailDirectly(){

        Message msg = Message.obtain();
        msg.what = EMAIL_CODE;

        if(ratingValue == -1){
            msg.obj = "Drag face icon horizontally to make a rating first";
            handler.sendMessage(msg);
            return;
        }

        Mail m = new Mail( mSPM.getEmailServerUsername() , mSPM.getEmailServerPassword() );
        String[] toArr = { mSPM.getContactEmail() };
        m.setTo(toArr);
        m.setFrom( mSPM.getUserEmail() );
        m.setSubject("Travel Blog Rating");
        m.setBody( mSPM.getUsername() + " gave app a rating of " + String.valueOf(ratingValue) );

        try {
            //m.addAttachment("/sdcard/filelocation");

            if(m.send()) {
                msg.obj = "Rating sent";
                handler.sendMessage(msg);
            } else {
                msg.obj = "Rating not sent";
                handler.sendMessage(msg);
            }
        } catch(Exception e) {
            msg.obj = "Rating not sent";
            handler.sendMessage(msg);
            Log.e("DirectEmail", "Could not send email", e);
        }
    }

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case EMAIL_CODE:
                    Toast.makeText(parentActivity , String.valueOf(msg.obj), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(parentActivity, EntryActivity.class);
                    parentActivity.startActivity(intent);
                    break;
            }
        }
    };
}