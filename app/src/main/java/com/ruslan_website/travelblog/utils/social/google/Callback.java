package com.ruslan_website.travelblog.utils.social.google;

public interface Callback {
    public void onSuccess(String socialId, String userName, String userEmail, String accessToken);
    public void onError(String errorMsg);
}
