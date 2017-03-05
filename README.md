# AndroidApp-Laravel5-TravelBlog
Android Studio 2, Laravel 5.4

Download APK: http://ruslan-website.com/laravel/travel_blog/apk/TravelBlog.apk

Laravel part of project: https://github.com/atabegruslan/Travel-Blog-Laravel-5

| Field | Value |
| --- | --- |
| User email | guest@guest.com |
| Password | gggggg |

## Include:

- Google SignIn

- Facebook SignIn

- GCM

![](https://raw.githubusercontent.com/atabegruslan/Travel-Blog-Android/master/Screenshot.png)

## For Google Signin: SHA1 certificate fingerprint:

Method 1: in CLI: `keytool -exportcert -list -v -keystore ~/.android/debug.keystore`

Method 2: in Android Studio:

![](https://raw.githubusercontent.com/atabegruslan/Travel-Blog-Android/master/ShaCertFingerprint.png)

### Google Developer websites:

https://console.developers.google.com/

https://developers.google.com/mobile/add

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
