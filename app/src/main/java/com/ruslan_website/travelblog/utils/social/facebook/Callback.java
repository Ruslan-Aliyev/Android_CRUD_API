package com.ruslan_website.travelblog.utils.social.facebook;

import android.graphics.Bitmap;

import org.json.JSONObject;

public interface Callback {
    public void onSuccess(JSONObject result, Bitmap profilePic);
    public void onError(String errorMsg);
}
