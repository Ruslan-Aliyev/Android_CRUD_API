package com.ruslan_website.travelblog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruslan_website.travelblog.utils.common.Image;
import com.ruslan_website.travelblog.utils.common.Network;
import com.ruslan_website.travelblog.utils.common.PathCombiner;
import com.ruslan_website.travelblog.utils.http.model.Entry;
import com.ruslan_website.travelblog.utils.http.service.EntryService;
import com.ruslan_website.travelblog.utils.http.service.ImageService;
import com.ruslan_website.travelblog.utils.http.service.TokenService;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntryActivity extends AppCompatActivity {

    private SharedPreferencesManagement mSPM;
    private static final int OBTAIN_IMG_BYTE_CODE = 4000;
    private EntryService entryService;
    private ImageService imageService;
    @BindView(R.id.bNewEntry) Button bNewEntry;
    @BindView(R.id.bRefresh) Button bRefresh;
    @BindView(R.id.greet) TextView greet;
    @BindView(R.id.entries) LinearLayout entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        ButterKnife.bind(this);

        if (mSPM == null) {
            mSPM = SharedPreferencesManagement.getInstance();
        }

        greet.setText("Hi " + mSPM.getUsername());
        greet.setTextSize(30);
        greet.setGravity(Gravity.CENTER);
        greet.setTypeface(null, Typeface.BOLD);

        if(Network.isConnected()) {
            if (entries.getChildCount() > 0) entries.removeAllViews();
            obtainEntry();
        }
    }

    private OkHttpClient makeHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    private void obtainEntry() {

        OkHttpClient client = makeHttpClient();

        entryService = new Retrofit.Builder()
                .baseUrl( mSPM.getUrl() )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EntryService.class);

        Call<List<Entry>> entryRequest = entryService.obtain("application/json", "Bearer " + mSPM.getAccessToken());

        entryRequest.enqueue(new Callback<List<Entry>>(){
            @Override
            public void onResponse(Call<List<Entry>> call, Response<List<Entry>> response) {
                obtainImage(response);
            }
            @Override
            public void onFailure(Call<List<Entry>> call, Throwable t) {
                Log.i("Entry - Error", t.getMessage());
            }
        });

    }

    private void obtainImage(Response<List<Entry>> response) {

        for (final Entry entry: response.body()) {

            String[] imgUrlParts = entry.getImg_url().split("/");
            final String imgName = imgUrlParts[imgUrlParts.length - 1];

            OkHttpClient client = makeHttpClient();

            imageService = new Retrofit.Builder()
                    .baseUrl( mSPM.getUrl() )
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ImageService.class);

            Call<ResponseBody> imageRequest = imageService.download(imgName);

            imageRequest.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        makeEntryList(response.body(), imgName, entry);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i("Download Image Error", t.getMessage());
                }
            });

        }
    }

    private void makeEntryList(ResponseBody body, String imgName, Entry entry) {

        downloadImage(body, imgName);

        LinearLayout ll = new LinearLayout(EntryActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView line1 = new TextView(EntryActivity.this);
        line1.setTextSize(20);
        line1.setGravity(Gravity.CENTER);
        line1.setTypeface(null, Typeface.BOLD);
        line1.setText(entry.getPlace());
        ll.addView(line1);

        TextView line2 = new TextView(EntryActivity.this);
        line2.setTextSize(15);
        line2.setGravity(Gravity.CENTER);
        line2.setTypeface(null, Typeface.ITALIC);
        line2.setText("By " + entry.getUser().getName() + ", " + entry.getTime());
        ll.addView(line2);

        TextView line3 = new TextView(EntryActivity.this);
        line3.setTextSize(14);
        line3.setGravity(Gravity.CENTER);
        line3.setText(entry.getComments());
        ll.addView(line3);

        ImageView imageView = new ImageView(EntryActivity.this);
        //String path = Environment.getExternalStoragePublicDirectory( PathCombiner.combine( Environment.DIRECTORY_PICTURES, mSPM.getAppImgDirName() ) ).getPath();
        String path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        Log.i("ReadImgPath", path);
        File imgFile = new File(path, imgName);
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new ActionBarOverlayLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setPadding(0, 0, 0, 100);
        ll.addView(imageView);

        entries.addView(ll);
    }

    private void downloadImage(ResponseBody body, String imgName) {
//        String appImgDir = PathCombiner.combine(
//                Environment.DIRECTORY_PICTURES,
//                mSPM.getAppImgDirName()
//        );
//        File path = Environment.getExternalStoragePublicDirectory(appImgDir);
        File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        InputStream is = body.byteStream();
        Image.save(path, imgName, is);
    }

    @OnClick(R.id.bNewEntry)
    public void newEntry(View view){
        Intent intent = new Intent(EntryActivity.this, NewEntryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.bRefresh)
    public void refresh(View view){
        init();
    }

}