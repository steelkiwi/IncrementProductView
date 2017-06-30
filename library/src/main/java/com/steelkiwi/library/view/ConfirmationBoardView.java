package com.steelkiwi.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by yaroslav on 6/28/17.
 */

public class ConfirmationBoardView extends View {

    private Paint defaultBackgroundPaint;
    private Bitmap bitmap;
    private int width;
    private int height;

    public ConfirmationBoardView(Context context) {
        super(context);
        initDefaultBackgroundPaint();
    }

    public ConfirmationBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefaultBackgroundPaint();
    }

    public ConfirmationBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultBackgroundPaint();
    }

    private void initDefaultBackgroundPaint() {
        defaultBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = reconcileSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        height = reconcileSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int reconcileSize(int contentSize, int measureSpec) {
        final int mode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch(mode) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                if (contentSize < specSize) {
                    return contentSize;
                } else {
                    return specSize;
                }
            case MeasureSpec.UNSPECIFIED:
            default:
                return contentSize;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = width / 2;
        int centerY = height / 2;
        canvas.drawCircle(centerX, centerY, centerX - 1, defaultBackgroundPaint);
        if(bitmap != null) {
            canvas.drawBitmap(bitmap, centerX - bitmap.getWidth() / 2, centerY - bitmap.getHeight() / 2, defaultBackgroundPaint);
        }
    }

    public void setBitmapResource(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        defaultBackgroundPaint.setColor(backgroundColor);
        invalidate();
    }
}
