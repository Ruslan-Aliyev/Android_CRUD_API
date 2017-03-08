package com.ruslan_website.travelblog.utils.webview;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

public class WebviewHandler {

    private SharedPreferencesManagement mSPM;
    private WebView mWebView;
    private Activity mActivity;
    private Fragment mFragment;

    private ValueCallback<Uri[]> mFilePathCallback;
    public final static int FILECHOOSER_RESULTCODE = 1;

    public WebviewHandler() {
        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }
    }
    static private class SingletonHelper {
        private final static WebviewHandler INSTANCE = new WebviewHandler();
    }
    public static WebviewHandler getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void init(WebView webview, Fragment fragment){
        mWebView = webview;
        mActivity = fragment.getActivity();
        mFragment = fragment;

        mWebView.loadUrl( mSPM.getPHPContactFormUrl() );
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLoadWithOverviewMode(true);

        mWebView.setPadding(0, 0, 0, 0);
        //mWebView.setInitialScale(getScale());
    }

//    private int getScale(){
//        Display display = ((WindowManager) parentActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int width = display.getWidth();
//        Double val = new Double(width)/new Double(WEBPAGE_WIDTH);
//        val = val * 100d;
//        return val.intValue();
//    }

    public void handlePhoneEmail(){
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if( url.startsWith("http:") || url.startsWith("https:") ) {
                    return false;
                }

                // Otherwise allow the OS to handle it
                else if (url.startsWith("tel:")) {
                    Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    mActivity.startActivity(tel);
                    return true;
                }
                else if (url.startsWith("mailto:")) {

                    String[] urlParts = url.split(":");
                    String emailAddress = urlParts[1].split("\\?")[0];
                    String subject = urlParts[1].split("\\?")[1].split("=")[1];
                    Log.i("Email", emailAddress+" "+subject );

                    Intent mailOverride = new Intent(Intent.ACTION_SEND);
                    mailOverride.setType("message/rfc822");
                    mailOverride.putExtra(Intent.EXTRA_EMAIL,new String[] { emailAddress });
                    mailOverride.putExtra(Intent.EXTRA_SUBJECT, subject);
                    mailOverride.putExtra(Intent.EXTRA_TEXT   , "");
                    mActivity.startActivity(mailOverride);
                    return true;
                }
                return true;
            }
        });
    }

    //https://gauntface.com/blog/2014/10/17/what-you-need-to-know-about-the-webview-in-l
    public void handleFileUpload(){
        mWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                // Check no existing callbacks
                if(mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                // Set up the intent to get an existing image
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                mFragment.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILECHOOSER_RESULTCODE);
                return true;
            }
        });
    }

    public void onHandleFileUploadActivityResult(int resultCode, Intent intent){
        if (null == mFilePathCallback) {
            return;
        }

        Uri[] results = null;

        if(resultCode == Activity.RESULT_OK) {
            String dataString = intent.getDataString();

            if (dataString != null) {
                results = new Uri[]{Uri.parse(dataString)};
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }
}

