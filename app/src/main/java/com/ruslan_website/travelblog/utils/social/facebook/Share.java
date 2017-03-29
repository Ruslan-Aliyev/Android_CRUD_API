package com.ruslan_website.travelblog.utils.social.facebook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class Share {

    private Share() {}
    static private class SingletonHelper {
        private final static Share INSTANCE = new Share();
    }
    public static Share getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private ShareDialog shareDialog;
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    public void shareBook(final String name, final String description, final String isbn, final Activity activity, final FacebookShareCallbackInterface shareResponseObserver){

        shareDialog = new ShareDialog(activity);

        if (shareDialog.canShow(ShareLinkContent.class)) {

            // Create an object
            ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "books.book")
                    .putString("og:title", name)
                    .putString("og:description", description)
                    .putString("books:isbn", isbn)
                    .build();

            // Create an action
            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                    .setActionType("books.reads")
                    .putObject("book", object)
                    .build();

            // Create the content
            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                    .setPreviewPropertyName("book")
                    .setAction(action)
                    .build();

            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {
                    shareResponseObserver.onCancel("Facebook Link Share Cancelled");
                }
                @Override
                public void onError(FacebookException error) {
                    shareResponseObserver.onError("Facebook Link Share Error", error);
                }
                @Override
                public void onSuccess(Sharer.Result result) {
                    shareResponseObserver.onSuccess("Facebook Link Share Success", result);
                }
            });

            shareDialog.show(activity, content);
        }
    }

    public void shareLink(final String url, final Activity activity, final FacebookShareCallbackInterface shareResponseObserver){

        shareDialog = new ShareDialog(activity);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
                .build();

        if (shareDialog.canShow(ShareLinkContent.class)) {

            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {
                    shareResponseObserver.onCancel("Facebook Link Share Cancelled");
                }
                @Override
                public void onError(FacebookException error) {
                    shareResponseObserver.onError("Facebook Link Share Error", error);
                }
                @Override
                public void onSuccess(Sharer.Result result) {
                    shareResponseObserver.onSuccess("Facebook Link Share Success", result);
                }
            });

            shareDialog.show(activity, content);
        }
    }

    public void shareImage(final Bitmap image, final Activity activity, final FacebookShareCallbackInterface shareResponseObserver){

        shareDialog = new ShareDialog(activity);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        if (shareDialog.canShow(ShareLinkContent.class)) {

            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {
                    shareResponseObserver.onCancel("Facebook Link Share Cancelled");
                }
                @Override
                public void onError(FacebookException error) {
                    shareResponseObserver.onError("Facebook Link Share Error", error);
                }
                @Override
                public void onSuccess(Sharer.Result result) {
                    shareResponseObserver.onSuccess("Facebook Link Share Success", result);
                }
            });

            shareDialog.show(activity, content);
        }
    }

    public void onFacebookShareLoginActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
