package com.ruslan_website.travelblog.utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.gms.maps.MapView;

public class RoundMap extends MapView {

    private Path clipPath;
    private final int DIMENSION = 200; // square

    public RoundMap(Context context) {
        super(context);
        init();
    }

    public RoundMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundMap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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