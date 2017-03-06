package com.ruslan_website.travelblog.utils.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.ruslan_website.travelblog.R;
import com.ruslan_website.travelblog.utils.http.api.APIFactory;
import com.ruslan_website.travelblog.utils.http.api.APIStrategy;
import com.ruslan_website.travelblog.utils.http.model.Entry;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "GCM";
    private SharedPreferencesManagement mSPM;

    APIFactory apiFactory;
    APIStrategy apiStrategy;

    public RegistrationIntentService() {
        super(TAG);
        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }

        apiFactory = new APIFactory( mSPM.getBackendOption() );
        apiStrategy = apiFactory.getApiStrategy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // GCM-1b: Register for GCM. This call goes out to the network to retrieve the token, subsequent calls are local.
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            mSPM.setGCMToken(token);
            Log.i(TAG, "Registration Token: " + mSPM.getGCMToken());
            uploadTokenToBackend(mSPM.getGCMToken());
        } catch (Exception e) {
            Log.i(TAG, "Failed to complete token refresh");
            e.printStackTrace();
        }
    }

    private void uploadTokenToBackend(String gcmToken) {
        Call<ResponseBody> uploadGcmTokenRequest = apiStrategy.uploadGcmToken(gcmToken);

        uploadGcmTokenRequest.enqueue(new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.i("GCM Token Upload", response.message());
                }else{
                    Log.i("GCM Token Upload", String.valueOf(response.code()) );
                    Log.i("GCM Token Upload", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("GCM Token Upload", t.getMessage());
            }
        });
    }
}
