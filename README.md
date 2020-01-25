# AndroidApp-Laravel5-TravelBlog
Android Studio 2, Laravel 5.4

Laravel part of project: 

## Include:

- Retrofit2, REST, Download image, upload form and file

- Facebook SignIn

- Facebook Share

- Google SignIn

- GCM

- Google Maps

- View elements' customization

- WebView handle phone, email, upload, JSInterface

- Emailing via both UA and JavaMail

- Object Animator

- Speech <-> Text

- SQLite

- Swiper View (Similar to Tinder)

- QR Code

![](https://raw.githubusercontent.com/atabegruslan/Travel-Blog-Android/master/Screenshot.png)

## For Facebook Signin: Sha Key Hash:

Method 1: in CLI: `keytool -exportcert -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64`

Method 2: in Java:

```java
try {
    PackageInfo info = getPackageManager().getPackageInfo(
            getApplicationContext().getPackageName(),
            PackageManager.GET_SIGNATURES);
    for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
    }
} catch (PackageManager.NameNotFoundException e) {
    Log.i("KeyHash NameNotFound:", e.getMessage());
} catch (NoSuchAlgorithmException e) {
    Log.i("KeyHash NoAlgorithm:", e.getMessage());
}
```

### Facebook Developer website:

https://developers.facebook.com/

## For Google Signin: SHA1 certificate fingerprint:

Method 1: in CLI: `keytool -exportcert -list -v -keystore ~/.android/debug.keystore`

Method 2: in Android Studio:

![](https://raw.githubusercontent.com/atabegruslan/Travel-Blog-Android/master/ShaCertFingerprint.png)

### Google Developer websites:

https://console.developers.google.com/

https://developers.google.com/mobile/add

## GCM:

`POST https://gcm-http.googleapis.com/gcm/send`

```
Content-Type:application/json
Authorization:key=(API Key)

{
  "registration_ids" : ["(Registration Token)"],
  "data" : {
    "message":"{xxx:yyy}"
  }
}
```

## Google Map:

![](https://raw.githubusercontent.com/atabegruslan/Travel-Blog-Android/master/MapApi.png)

## Retrofit 1.9 vs Retrofit 2.0

### No more distinction in the adapter interface regarding synchronous and asynchronous requests:

#### Retrofit 2.0
```java
public interface Service {
    @GET("retrofit/{version}/get.php")
    Call<Model> get(@Path("version") String version, @Query("test_name") String test_name);
}
```

#### Retrofit 1.9
```java
public interface Service {
    @GET("/retrofit/{version}/get.php")
    public void getAsync(@Path("version") String version, @Query("test_name") String test_name, Callback<Model> response);

    @GET("/retrofit/{version}/get.php")
    public Model getSync(@Path("version") String version, @Query("test_name") String test_name);
}
```

### Becareful about constructing URLs. Ensure that `/` don't 'double up'

#### In 2.0, should be like this
```java
private static final String BASE_URL = "http://url.domain/";
...
service = new Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build().create(Service.class);
...
Call<Model> call = service.get(version, queryParam );
```

```java
public interface Service {
    @GET("retrofit/{version}/get.php")
    Call<Model> get(@Path("version") String version, @Query("query_param") String queryParam);
}
```

#### Instead of 1.9's
```java
private final String BASE_URL = "http://url.domain/";
...
restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
service = restAdapter.create(Service.class);
...
// Sync
Model model = service.getSync(version, queryParam); // In thread

// Async
service.getAsync(version, queryParam, new Callback<Model>() {
    @Override
    public void success(Model model, Response response) {...}
    @Override
    public void failure(RetrofitError error) {...}
});
```

```java
public interface Service {
    @GET("/retrofit/{version}/get.php")
    public void getAsync(@Path("version") String version, @Query("query_param") String queryParam, Callback<Model> response);

    @GET("/retrofit/{version}/get.php")
    public Model getSync(@Path("version") String version, @Query("query_param") String queryParam);
}
```

Constructed URL is `http://url.domain/retrofit/[version]/get.php?query_param=[queryParam]`

### Full Description of differences

https://github.com/atabegruslan/Android-Retrofit2Get/blob/master/Illustrations/Retrofit2.pdf
