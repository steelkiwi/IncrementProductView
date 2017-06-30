package com.steelkiwi.library;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.steelkiwi.library.listener.OnStateListener;
import com.steelkiwi.library.util.BoardItemType;
import com.steelkiwi.library.util.ConfirmationState;
import com.steelkiwi.library.util.Constants;
import com.steelkiwi.library.util.ViewState;
import com.steelkiwi.library.view.ConfirmationBoardView;
import com.steelkiwi.library.view.BoardItemView;
import com.steelkiwi.library.view.BoardView;

import io.codetail.animation.arcanimator.ArcAnimator;
import io.codetail.animation.arcanimator.Side;

/**
 * Created by yaroslav on 6/28/17.
 */

public class IncrementProductView extends ViewGroup implements View.OnClickListener {

    private static final int STROKE_WIDTH = 5;
    private static final float RADIUS_SCALE_FACTOR = .23f;
    private static final float BOARD_ITEM_SCALE = .8f;
    private static final int START_DELAY_PERIOD = 40;
    private static final int DELAY_100 = 100;
    private static final int DELAY_300 = 300;
    private static final int DELAY_500 = 600;

    private Paint defaultBackgroundPaint;
    private Paint increaseButtonPaint;
    // main icon bitmap
    private Bitmap mainIconBitmap;
    // default state of the view group
    private ViewState state = ViewState.IDLE;
    // confirmation view state
    private ConfirmationState confirmationState = ConfirmationState.OPEN;
    // product state listener
    private OnStateListener onStateListener;
    // view to show count of increments
    private BoardView boardChild;
    // view to increment count
    private BoardItemView incrementChild;
    // view to decrement count
    private BoardItemView decrementChild;
    // start animation delay
    private long startDelay = 0;
    //
    private ConfirmationBoardView confirmationBoardView;
    private int mainViewRadius;
    private int centerX;
    private int centerY;
    private int defaultRadius;

    public IncrementProductView(Context context) {
        super(context);
        init();
    }

    public IncrementProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IncrementProductView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initDefaultValues();
        initDefaultBackgroundPaint();
        initIncreaseButtonPaint();
        initBoardViews();

    }

    private void initDefaultValues() {
        // empty
        mainIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box);
    }

    private void initBoardViews() {
        boardChild = new BoardView(getContext());
        // init increment view
        incrementChild = new BoardItemView(getContext());
        incrementChild.setType(BoardItemType.INCREMENT_TYPE);
        incrementChild.setOnClickListener(onIncrementClick);
        // init decrement view
        decrementChild = new BoardItemView(getContext());
        decrementChild.setType(BoardItemType.DECREMENT_TYPE);
        decrementChild.setOnClickListener(onDecrementClick);
        // init button to confirmation
        confirmationBoardView = new ConfirmationBoardView(getContext());
        confirmationBoardView.setBitmapResource(R.drawable.plus);
        confirmationBoardView.setBackgroundColor(R.color.default_background_color);
        confirmationBoardView.setOnClickListener(this);
    }

    private void initDefaultBackgroundPaint() {
        defaultBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.default_background_color));
        defaultBackgroundPaint.setStyle(Paint.Style.STROKE);
        defaultBackgroundPaint.setStrokeWidth(STROKE_WIDTH);
    }

    private void initIncreaseButtonPaint() {
        increaseButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        increaseButtonPaint.setColor(ContextCompat.getColor(getContext(), R.color.default_background_color));
        increaseButtonPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(incrementChild);
        addView(decrementChild);
        addView(boardChild);
        addView(confirmationBoardView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = reconcileSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        int height = reconcileSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);
        int size = Math.min(width, height);
        // measure children size
        measureChildren(size);
        // measure parent size
        setMeasuredDimension(size, size);
    }

    private void measureChildren(int size) {
        // calculate center of Y axis
        centerX = centerY = size / 2;
        // calculate radius for main button
        mainViewRadius = (int) (centerX * RADIUS_SCALE_FACTOR);
        // default radius for main circle
        defaultRadius = centerX - mainViewRadius - STROKE_WIDTH * 2;
        // calculate size for board view
        int viewSize = mainViewRadius * 2;
        // measure board view
        boardChild.measure(viewSize, viewSize);
        // calculate size for increment and decrement view
        int scaleViewSize = (int) (viewSize * BOARD_ITEM_SCALE);
        // measure views
        incrementChild.measure(scaleViewSize, scaleViewSize);
        decrementChild.measure(scaleViewSize, scaleViewSize);
        confirmationBoardView.measure(viewSize, viewSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutBoardChildView();
        layoutIncrementChildView();
        layoutDecrementChildView();
    }

    private void layoutBoardChildView() {
        int width = boardChild.getMeasuredWidth();
        int left = centerX - (width / 2);
        int top = centerX + defaultRadius - (width / 2);
        int right = centerX + (width / 2);
        int bottom = centerY + defaultRadius + (width / 2);
        boardChild.layout(left, top, right, bottom);
        confirmationBoardView.layout(left, top, right, bottom);
    }

    private void layoutIncrementChildView() {
        int width = incrementChild.getMeasuredWidth();
        int left = centerX - (width / 2);
        int top = centerX + defaultRadius - (width / 2);
        int right = centerX + (width / 2);
        int bottom = centerY + defaultRadius + (width / 2);
        incrementChild.layout(left, top, right, bottom);
    }

    private void layoutDecrementChildView() {
        int width = decrementChild.getMeasuredWidth();
        int left = centerX - (width / 2);
        int top = centerX + defaultRadius - (width / 2);
        int right = centerX + (width / 2);
        int bottom = centerY + defaultRadius + (width / 2);
        decrementChild.layout(left, top, right, bottom);
    }

    @Override
    public void onClick(View view) {
        if(confirmationState == ConfirmationState.CONFIRM) {
            if (boardChild != null) {
                onConfirm();
                manageBoardViewState(false);
            }
            confirmationState = ConfirmationState.OPEN;
        } else {
            manageBoardViewState(true);
            confirmationState = ConfirmationState.CONFIRM;
        }
    }

    private void manageBoardViewState(boolean isUpdate) {
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 2;
        onParentClick(cx, cy, radius, isUpdate);
    }

    private OnClickListener onIncrementClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            scaleBoardView(incrementChild);
            if(boardChild != null) {
                boardChild.increment();
                onCountChange(boardChild.getCount());
            }
        }
    };

    private OnClickListener onDecrementClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            scaleBoardView(decrementChild);
            if(boardChild != null) {
                boardChild.decrement();
                onCountChange(boardChild.getCount());
            }
        }
    };

    private void onCountChange(int count) {
        if(onStateListener != null) {
            onStateListener.onCountChange(count);
        }
    }

    private void onConfirm() {
        if(onStateListener != null) {
            onStateListener.onConfirm(boardChild.getCount());
        }
    }

    private void scaleBoardView(final View view) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", .8f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", .8f);
        set.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                AnimatorSet set = new AnimatorSet();
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f);
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f);
                set.playTogether(scaleXAnimator, scaleYAnimator);
                set.setDuration(DELAY_100);
                set.start();
            }
        });
        set.playTogether(scaleXAnimator, scaleYAnimator);
        set.setDuration(DELAY_100);
        set.start();
    }

    private void onParentClick(float cx, float cy, float radius, boolean isUpdate) {
        if(state == ViewState.IDLE) {
            if(isUpdate) {
                // update count of products when board is open
                onCountChange(boardChild.getCount());
            }
            // change background color
            changeBackgroundsColor(R.color.highlight_background_color);
            // update views to expand state
            expandBoardViews(cx, cy, radius);
            // change parent state to have ability to close expanded views
            state = ViewState.EXPAND;
        } else {
            if(isUpdate) {
                // update count of products when board is hide
                onCountChange(0);
            }
            // reset product count
            boardChild.reset();
            // update views to idle state
            idleBoardView(cx, cy, radius);
            // change parent state to have ability to expand views
            state = ViewState.IDLE;
        }
    }

    private void changeBackgroundsColor(@ColorRes int color) {
        defaultBackgroundPaint.setColor(ContextCompat.getColor(getContext(), color));
        increaseButtonPaint.setColor(ContextCompat.getColor(getContext(), color));
        confirmationBoardView.setBackgroundColor(color);
        confirmationBoardView.setBitmapResource(R.drawable.done);
        invalidate();
    }

    private void expandBoardViews(float cx, float cy, float radius) {
        startDelay = 0;
        // animate views to expand
        animateExpandView(incrementChild, cx, cy, radius,
                Constants.Degree.DEGREE_55, Constants.Degree.DEGREE_110, getStartDelay(), DELAY_500);
        incrementStartDelay();
        animateExpandView(boardChild, cx, cy, radius,
                Constants.Degree.DEGREE_90, Constants.Degree.DEGREE_80, getStartDelay(), DELAY_500);
        incrementStartDelay();
        animateExpandView(decrementChild, cx, cy, radius,
                Constants.Degree.DEGREE_125, Constants.Degree.DEGREE_40, getStartDelay(), DELAY_500);
    }

    private void idleBoardView(float cx, float cy, float radius) {
        // animate views
        animateExpandView(incrementChild, cx, cy, radius,
                Constants.Degree.DEGREE_180, Constants.Degree.DEGREE_110, 0, DELAY_300);
        animateExpandView(boardChild, cx, cy, radius,
                Constants.Degree.DEGREE_180, Constants.Degree.DEGREE_80, 0, DELAY_300)
                .addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        changeBackgroundsColor(R.color.default_background_color);
                        confirmationBoardView.setBackgroundColor(R.color.default_background_color);
                        confirmationBoardView.setBitmapResource(R.drawable.plus);
                    }
                });
        animateExpandView(decrementChild, cx, cy, radius,
                Constants.Degree.DEGREE_180, Constants.Degree.DEGREE_40, 0, DELAY_300);
    }


    private ArcAnimator animateExpandView(View view, float cx, float cy, float radius,
                                          float degree, float animationDegree, long startDelay, long delay) {
        float angle = (float) Math.toRadians(degree);
        float stopX = (float) (cx + (radius - mainViewRadius - STROKE_WIDTH * 2) * Math.sin(angle));
        float stopY = (float) (cy - (radius - mainViewRadius - STROKE_WIDTH * 2) * Math.cos(angle));
        ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(view, stopX, stopY, animationDegree, Side.RIGHT);
        arcAnimator.setDuration(delay);
        arcAnimator.setStartDelay(startDelay);
        arcAnimator.setInterpolator(new DecelerateInterpolator());
        arcAnimator.start();
        return arcAnimator;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, defaultRadius, defaultBackgroundPaint);
        canvas.drawBitmap(mainIconBitmap, centerX - mainIconBitmap.getWidth() / 2, centerY - mainIconBitmap.getHeight() / 2, defaultBackgroundPaint);
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

    private void incrementStartDelay() {
        startDelay += START_DELAY_PERIOD;
    }

    public long getStartDelay() {
        return startDelay;
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        this.onStateListener = onStateListener;
    }
}
