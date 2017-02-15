package com.ruslan_website.travelblog.utils.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ruslan_website.travelblog.utils.TravelBlogApplication;

public class Network {

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) TravelBlogApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
