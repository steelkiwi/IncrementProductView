package com.steelkiwi.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Created by yaroslav on 6/29/17.
 */

public class TestDrawable1 extends Drawable {

    private final Paint paint = new Paint();

    // The angle in degress that the arrow head is inclined at.
    private static final float ARROW_HEAD_ANGLE = (float) Math.toRadians(45);
    private final float strokeWidth = 5;
    // The length of top and bottom bars when they merge into an arrow
    private final float topBottomSize;
    // The length of middle bar
    private final float mBarSize;
    // The length of the middle bar when arrow is shaped
    private final float mMiddleArrowSize;
    // The space between bars when they are parallel
    private final float mBarGap;
    // Whether bars should spin or not during progress
    private final boolean spin;
    // Use Path instead of canvas operations so that if color has transparency, overlapping sections
    // wont look different
    private final Path path = new Path();
    // The reported intrinsic size of the drawable.
    private final int drawableSize;
    // Whether we should mirror animation when animation is reversed.
    private boolean mVerticalMirror = false;
    // The interpolated version of the original progress
    private float mProgress;

    /**
     * @param context used to get the configuration for the drawable from
     */
    TestDrawable1(Context context) {
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        drawableSize = 100;
        mBarSize = 36;
        topBottomSize = 5;
        mBarGap = 3;
        spin = true;
        mMiddleArrowSize = 20;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeWidth(strokeWidth);
    }

    /**
     * If set, canvas is flipped when progress reached to end and going back to start.
     */
    public void setVerticalMirror(boolean verticalMirror) {
        mVerticalMirror = verticalMirror;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        final boolean isRtl = false;
        // Interpolated widths of arrow bars
        final float arrowSize = interpolate(mBarSize, topBottomSize, mProgress);
        final float middleBarSize = interpolate(mBarSize, mMiddleArrowSize, mProgress);
        // Interpolated size of middle bar
        final float middleBarCut = interpolate(0, strokeWidth / 2, mProgress);
        // The rotation of the top and bottom bars (that make the arrow head)
        final float rotation = interpolate(0, ARROW_HEAD_ANGLE, mProgress);

        // The whole canvas rotates as the transition happens
        final float canvasRotate = interpolate(-90, 90, mProgress);
        final float topBottomBarOffset = interpolate(mBarGap + strokeWidth, 0, mProgress);
        path.rewind();

        final float arrowEdge = -middleBarSize / 2;
        // draw middle bar
        path.moveTo(0, arrowEdge + middleBarCut);
        path.rLineTo(0, middleBarSize - middleBarCut * 2);

        final float arrowWidth = Math.round(arrowSize * Math.cos(rotation));
        final float arrowHeight = Math.round(arrowSize * Math.sin(rotation));

        path.moveTo(arrowEdge + middleBarCut, 0);
        path.rLineTo(middleBarSize - middleBarCut * 2, 0);

//        // top bar
//        path.moveTo(arrowEdge, topBottomBarOffset);
//        path.rLineTo(arrowWidth, arrowHeight);
//
//        // bottom bar
//        path.moveTo(arrowEdge, -topBottomBarOffset);
//        path.rLineTo(arrowWidth, -arrowHeight);
//        path.moveTo(0, 0);
        path.close();

        canvas.save();
        // Rotate the whole canvas if spinning, if not, rotate it 180 to get
        // the arrow pointing the other way for RTL.
        if (spin) {
            canvas.rotate(canvasRotate * ((mVerticalMirror) ? -1 : 1), bounds.centerX(), bounds.centerY());
        }
        canvas.translate(bounds.centerX(), bounds.centerY());
        canvas.drawPath(path, paint);

        canvas.restore();
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    // override
    public boolean isAutoMirrored() {
        // Draws rotated 180 degrees in RTL mode.
        return true;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getIntrinsicHeight() {
        return drawableSize;
    }

    @Override
    public int getIntrinsicWidth() {
        return drawableSize;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidateSelf();
    }

    private float interpolate(float a, float b, float t) {
        // Linear interpolate between a and b with parameter t.
        return a + (b - a) * t;
    }
}
