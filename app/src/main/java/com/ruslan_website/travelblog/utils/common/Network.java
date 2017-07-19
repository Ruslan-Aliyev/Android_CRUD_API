package com.ruslan_website.travelblog.utils.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.ruslan_website.travelblog.R;
import com.ruslan_website.travelblog.utils.TravelBlogApplication;

public class Network {

    public static boolean isConnected() {
        Context context = TravelBlogApplication.getContext();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.i("connected to wifi: ", activeNetwork.getTypeName());
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.i("connected to 3g/4g: ", activeNetwork.getTypeName());
            }
        } else {
            Log.i("not connected", "not connected to the internet");
        }

        Log.i("connected?", String.valueOf(activeNetwork != null && activeNetwork.isConnectedOrConnecting()));
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
