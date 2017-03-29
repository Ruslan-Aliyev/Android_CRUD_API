package com.ruslan_website.travelblog.utils.social.facebook;

import com.facebook.FacebookException;
import com.facebook.share.Sharer;

public interface FacebookShareCallbackInterface {
    public void onError(String msg, FacebookException error);
    public void onCancel(String msg);
    public void onSuccess(String msg, Sharer.Result result);
}
