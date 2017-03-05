package com.ruslan_website.travelblog.utils.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

public class GCM {

    private Activity mActivity;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private SharedPreferencesManagement mSPM;

    public GCM() {
        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }
    }
    static private class SingletonHelper {
        private final static GCM INSTANCE = new GCM();
    }
    public static GCM getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void init(Activity activity){
        mActivity = activity;

        if (checkPlayServices() && mSPM.getGCMToken() == null) {
            // GCM-1a: Start IntentService to register this application with GCM.
            Intent intent = new Intent(mActivity, RegistrationIntentService.class);
            mActivity.startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(mActivity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("GCM", "This device is dont support GCM");
            }
            return false;
        }
        return true;
    }
}
