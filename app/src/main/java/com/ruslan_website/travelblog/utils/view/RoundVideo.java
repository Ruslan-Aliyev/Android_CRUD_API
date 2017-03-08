package com.ruslan_website.travelblog.utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class RoundVideo extends SurfaceView {

    private Path clipPath;
    private final int DIMENSION = 200; // square, 200px

    public RoundVideo(Context context) {
        super(context);
        init();
    }

    public RoundVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Log.i("AA", attrs.getAttributeValue("android", "layout_width"));
        init();
    }

    public RoundVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //Log.i("AAA", attrs.getAttributeValue("android", "layout_width"));
        init();
    }

    private void init() {
        clipPath = new Path();
        clipPath.addCircle(DIMENSION, DIMENSION, DIMENSION, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(clipPath);
        super.dispatchDraw(canvas);
    }
}