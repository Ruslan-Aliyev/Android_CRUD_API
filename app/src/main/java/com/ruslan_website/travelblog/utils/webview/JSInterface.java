package com.ruslan_website.travelblog.utils.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.ruslan_website.travelblog.EntryActivity;
import com.ruslan_website.travelblog.utils.http.model.Entry;

public class JSInterface {
    Context mContext;

    public JSInterface(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void goBack(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext, EntryActivity.class);
        mContext.startActivity(intent);
    }
}