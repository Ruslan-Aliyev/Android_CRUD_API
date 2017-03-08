package com.ruslan_website.travelblog.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewPropertyAnimator;

public class AnimatorUtil {

    //Brightness
    private static final int DURATION_ANIMATION_REVEAL = 200;
    private static final int DURATION_ANIMATION_ROTATE = 1500;

    private static AnimatorUtil sAnimatorUtil;

    //Brightness
    private Animator mRevealAnimator;

    public static AnimatorUtil getInstance() {
        if (sAnimatorUtil == null) {
            sAnimatorUtil = new AnimatorUtil();
        }

        return sAnimatorUtil;
    }

//    public Animator animateCircularReveal(@NonNull View animateView) {
//
//        animateView.clearAnimation();
//        if (mRevealAnimator != null) {
//            mRevealAnimator.cancel();
//        }
//
//        int cx = (animateView.getLeft() + animateView.getRight());
//        int cy = (animateView.getTop());
//
//        // to find  radius when icon is tapped for showing layout
//        int startRadius = 0;
//        int endRadius = Math.max(animateView.getWidth(), animateView.getHeight());
//
//        mRevealAnimator = ViewAnimationUtils
//                .createCircularReveal(animateView, cx, cy, startRadius, endRadius);
//        mRevealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        mRevealAnimator.setDuration(AnimatorUtil.DURATION_ANIMATION_REVEAL);
//
//        return mRevealAnimator;
//    }

    public ViewPropertyAnimator animateRotateClockWise(@NonNull final View animateView) {

        animateView.setRotation(0);
        ViewPropertyAnimator rotateAnimator = animateView
                .animate()
                .setDuration(AnimatorUtil.DURATION_ANIMATION_ROTATE)
                .rotation(360f);

        return rotateAnimator;
    }

    public ViewPropertyAnimator animateScaleLarger(@NonNull final View animateView) {

        animateView.setScaleX(0f);
        animateView.setScaleY(0f);
        ViewPropertyAnimator scaleAnimator = animateView
                .animate()
                .setDuration(AnimatorUtil.DURATION_ANIMATION_ROTATE)
                .scaleX(1.f)
                .scaleY(1.f);

        return scaleAnimator;
    }

    // Ruslan's 02/10/2016 codes starts here

    public ObjectAnimator scale(View view, float initWidth, float finalWidth, float initHeight, float finalHeight, int duration){
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, initWidth, finalWidth),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, initHeight, finalHeight));
        animator.setDuration(duration);
        return animator;
    }

    public ObjectAnimator move(View view, String property, float initPos, float finalPos, int duration){
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                view,
                property,
                initPos,
                finalPos);
        animator.setDuration(duration);
        return animator;
    }
}
