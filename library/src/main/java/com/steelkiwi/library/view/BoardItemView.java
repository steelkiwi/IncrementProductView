package com.steelkiwi.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.steelkiwi.library.util.BoardItemType;
import com.steelkiwi.library.R;

/**
 * Created by yaroslav on 6/28/17.
 */

public class BoardItemView extends View {

    private Paint defaultBackgroundPaint;
    private BoardItemType type;
    private Bitmap incrementBitmap;
    private Bitmap decrementBitmap;
    private int width;
    private int height;
    private int padding;

    public BoardItemView(Context context) {
        super(context);
        init();
    }

    public BoardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        padding = getResources().getDimensionPixelSize(R.dimen.parent_stroke_size);
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
        // calculate canvas center positions
        int centerX = width / 2;
        int centerY = height / 2;
        // draw background
        drawBackground(canvas, centerX, centerY);
        // draw bitmaps
        drawBitmaps(canvas, centerX, centerY);
    }

    private void drawBackground(final Canvas canvas, int centerX, int centerY) {
        canvas.drawCircle(centerX, centerY, centerX - padding, defaultBackgroundPaint);
    }

    private void drawBitmaps(final Canvas canvas, int centerX, int centerY) {
        if(type == BoardItemType.INCREMENT_TYPE) {
            int x = centerX - getIncrementBitmap().getWidth() / 2;
            int y = centerY - getIncrementBitmap().getHeight() / 2;
            canvas.drawBitmap(getIncrementBitmap(), x, y, defaultBackgroundPaint);
        } else {
            int x = centerX - getDecrementBitmap().getWidth() / 2;
            int y = centerY - getDecrementBitmap().getHeight() / 2;
            canvas.drawBitmap(getDecrementBitmap(), x, y, defaultBackgroundPaint);
        }
    }

    public void setType(BoardItemType type) {
        this.type = type;
    }

    public Bitmap getIncrementBitmap() {
        return incrementBitmap;
    }

    public void setIncrementBitmap(Bitmap incrementBitmap) {
        this.incrementBitmap = incrementBitmap;
    }

    public Bitmap getDecrementBitmap() {
        return decrementBitmap;
    }

    public void setDecrementBitmap(Bitmap decrementBitmap) {
        this.decrementBitmap = decrementBitmap;
    }

    public void setDefaultBackgroundColor(int defaultBackgroundColor) {
        defaultBackgroundPaint.setColor(defaultBackgroundColor);
        invalidate();
    }
}
