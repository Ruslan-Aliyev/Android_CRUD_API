package com.ruslan_website.travelblog.utils.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.ruslan_website.travelblog.R;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "GCM";
    private SharedPreferencesManagement mSPM;

    public RegistrationIntentService() {
        super(TAG);
        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // GCM-1b: Register for GCM. This call goes out to the network to retrieve the token, subsequent calls are local.
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            mSPM.setGCMToken(token);
            Log.i(TAG, "Registration Token: " + token);
        } catch (Exception e) {
            Log.i(TAG, "Failed to complete token refresh");
            e.printStackTrace();
        }
    }
}
