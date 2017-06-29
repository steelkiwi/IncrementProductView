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
    private Paint strokeBackgroundPaint;
    private BoardItemType type;
    private Bitmap incrementBitmap;
    private Bitmap decrementBitmap;
    private int width;
    private int height;

    public BoardItemView(Context context) {
        super(context);
        initDefaultBackgroundPaint();
    }

    public BoardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefaultBackgroundPaint();
    }

    public BoardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultBackgroundPaint();
    }

    private void initDefaultBackgroundPaint() {
        defaultBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultBackgroundPaint.setColor(Color.parseColor("#4BBEC2"));
        defaultBackgroundPaint.setStyle(Paint.Style.FILL);

        strokeBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeBackgroundPaint.setColor(Color.parseColor("#4BBEC2"));
        strokeBackgroundPaint.setStyle(Paint.Style.STROKE);
        strokeBackgroundPaint.setStrokeWidth(3);

//        setBackgroundColor(Color.RED);

        incrementBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        decrementBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.minus);
//        bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
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
        canvas.drawCircle(centerX, centerY, centerX - 5, defaultBackgroundPaint);
        canvas.drawCircle(centerX, centerY, centerX - 5, strokeBackgroundPaint);
        if(type == BoardItemType.INCREMENT_TYPE) {
            int x = centerX - incrementBitmap.getWidth() / 2;
            int y = centerY - incrementBitmap.getHeight() / 2;
            canvas.drawBitmap(incrementBitmap, x, y, strokeBackgroundPaint);
        } else {
            int x = centerX - decrementBitmap.getWidth() / 2;
            int y = centerY - decrementBitmap.getHeight() / 2;
            canvas.drawBitmap(decrementBitmap, x, y, strokeBackgroundPaint);
        }
    }

    public void setType(BoardItemType type) {
        this.type = type;
    }
}
