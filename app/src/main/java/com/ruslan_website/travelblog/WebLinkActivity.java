package com.ruslan_website.travelblog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class WebLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if(data != null) {
            List<String> params = data.getPathSegments();
            String first = params.get(0);
            String second = params.get(1);
            Log.i("Param", first + second);
        }

        Intent intent = new Intent(WebLinkActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
