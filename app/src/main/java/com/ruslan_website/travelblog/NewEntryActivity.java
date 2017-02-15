package com.ruslan_website.travelblog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ruslan_website.travelblog.utils.common.Image;
import com.ruslan_website.travelblog.utils.common.PathCombiner;
import com.ruslan_website.travelblog.utils.http.model.Entry;
import com.ruslan_website.travelblog.utils.http.service.EntryService;
import com.ruslan_website.travelblog.utils.storage.SharedPreferencesManagement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewEntryActivity extends AppCompatActivity {

    private SharedPreferencesManagement mSPM;
    private EntryService entryService;
    private static final int REQUEST_CAMERA = 5000;
    private static final int SELECT_FILE = 5001;
    String filePath;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.place) EditText place;
    @BindView(R.id.comments) EditText comments;
    @BindView(R.id.bImage) ImageButton bImage;
    @BindView(R.id.bSubmit) Button bSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
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

        name.setText(mSPM.getUsername());
        name.setTextSize(20);
        name.setGravity(Gravity.CENTER);
        name.setTypeface(null, Typeface.BOLD);
    }

    private OkHttpClient makeHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    @OnClick(R.id.bImage)
    public void selectImage(View view){
        selectImage();
    }

    // Pick image or take photo tutorial:
    // http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample
    private void selectImage() {

        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(NewEntryActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent(){
        Intent intent = new Intent();

        // select only images from the media storage.
        // Tip:  if you want to get images as well as videos,
        // you can use following code : intent.setType("image/* video/*");
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        // calling an implicit intent to open the gallery
        // and then calling startActivityForResult() and passing the intent.
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The requestCode is what you have passed to startActivityForResult().
        // Either REQUEST_CAMERA or SELECT_FILE

        // resultCode :
        // RESULT_OK if the operation was successful or
        // RESULT_CANCEL if the operation was somehow cancelled or unsuccessful.

        // The intent data carries the result data
        // Either the image we have captured from the camera,
        // or the image we selected from gallery.
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    // On selecting image from gallery, onSelectFromGalleryResult(data) will be called
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {

                // Android saves the images in its own database,
                // we can fetch it using different ways. Here we use MediaStore.Images.Media.getBitmap().
                // Fetching image from specific path( data.getData() ) as Bitmap by calling getBitmap() method.
                android.net.Uri selectedImage = data.getData();
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);

                saveImage(bm);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {

        // We get our data in our Intent, so we are first getting that data through data.getsExtras().get("data")
        // and then we are casting that data into Bitmap since we want it as an image.
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        saveImage(thumbnail);
    }

    private void saveImage(Bitmap bitmap){
        bitmap = Image.cropToSquare(bitmap);
        bitmap = Image.scaleDown(bitmap);
        bitmap = Image.cropToCircle(bitmap);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        // We want to make thumbnail of an image,
        // so we need to first take the ByteArrayOutputStream and than pass it into thumbnail.compress() method.
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/,  bytes);

        // Convert Bitmap to InputStream
        byte[] bitmapdata = bytes.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(bitmapdata);

//        String appTempImgDir = PathCombiner.combine(
//                Environment.DIRECTORY_PICTURES,
//                mSPM.getAppTempImgDirName()
//        );
//        File photoPath = Environment.getExternalStoragePublicDirectory(appTempImgDir);
        File photoPath = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp");
        String photoName = System.currentTimeMillis() + ".png";
        File destination = new File(photoPath, photoName);

        Image.save(photoPath, photoName, is);
        bImage.setImageBitmap(bitmap);
        filePath = destination.getPath();
    }

    @OnClick(R.id.bSubmit)
    public void submit(View view){

        if( place.getText().toString().length() == 0 ){
            place.setError( "Place is required!" );
            return;
        }
        if( comments.getText().toString().length() == 0 ){
            comments.setError( "Comments is required!" );
            return;
        }
        if( bImage.getDrawable() == null ){
            Toast.makeText(NewEntryActivity.this, "Must Pick an Image", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(NewEntryActivity.this, "Submitting ...", Toast.LENGTH_LONG).show();

        OkHttpClient client = makeHttpClient();

        entryService = new Retrofit.Builder()
                .baseUrl( mSPM.getUrl() )
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EntryService.class);

        File file = new File(filePath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mSPM.getUserId()) );
        RequestBody place1 = RequestBody.create(MediaType.parse("text/plain"), place.getText().toString() );
        RequestBody comments1 = RequestBody.create(MediaType.parse("text/plain"), comments.getText().toString() );

        Call<ResponseBody> newEntryRequest = entryService.upload(
                "application/json",
                "Bearer " + mSPM.getAccessToken(),
                image,
                userId,
                place1,
                comments1
        );

        newEntryRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getBaseContext(), "Create New Success: " + response.message(), Toast.LENGTH_SHORT).show();
                back();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Create New Fail", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                back();
            }
        });
    }

    private void back(){
        Intent intent = new Intent(NewEntryActivity.this, EntryActivity.class);
        startActivity(intent);
    }

}
