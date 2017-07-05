package com.steelkiwi.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.steelkiwi.library.R;

/**
 * Created by yaroslav on 6/28/17.
 */

public class BoardView extends View implements BoardCounter {

    private Paint defaultBackgroundPaint;
    private Paint strokeBackgroundPaint;
    private TextPaint counterPaint;
    private int width;
    private int height;
    private int count;
    private int strokeWidth;

    public BoardView(Context context) {
        super(context);
        initDefaultBackgroundPaint();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefaultBackgroundPaint();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultBackgroundPaint();
    }

    private void initDefaultBackgroundPaint() {
        strokeWidth = getResources().getDimensionPixelSize(R.dimen.view_stroke_size);
        defaultBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultBackgroundPaint.setStyle(Paint.Style.FILL);

        strokeBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeBackgroundPaint.setStyle(Paint.Style.STROKE);
        strokeBackgroundPaint.setStrokeWidth(strokeWidth);

        counterPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        setCount(1);
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
        canvas.drawCircle(centerX, centerY, centerX - strokeWidth, defaultBackgroundPaint);
        canvas.drawCircle(centerX, centerY, centerX - strokeWidth, strokeBackgroundPaint);

        drawText(canvas, centerX, centerY);
    }

    private void drawText(final Canvas canvas, int centerX, int centerY) {
        String count = String.valueOf(getCount());
        final float textWidth = counterPaint.measureText(count);
        final float textX = Math.round(centerX - textWidth * .5f);
        final float textY = Math.round(centerY + getTextHeight(count) * .5f);
        canvas.drawText(String.valueOf(getCount()), textX, textY, counterPaint);
    }

    private float getTextHeight(String text) {
        Rect bounds = new Rect();
        counterPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    @Override
    public void reset() {
        count = 1;
        invalidate();
    }

    @Override
    public void increment() {
        count++;
        invalidate();
    }

    @Override
    public void decrement() {
        if(count > 0) {
            count--;
        }
        invalidate();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    public void setTextParameters(float size, int color) {
        counterPaint.setColor(color);
        counterPaint.setTextSize(size);
        invalidate();
    }

    public void setDefaultBackgroundColor(int color1, int color2) {
        defaultBackgroundPaint.setColor(color1);
        strokeBackgroundPaint.setColor(color2);
        invalidate();
    }
}
