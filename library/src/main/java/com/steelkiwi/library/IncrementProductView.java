package com.steelkiwi.library;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.steelkiwi.library.listener.OnProductChangeListener;
import com.steelkiwi.library.util.BoardItemType;
import com.steelkiwi.library.util.ViewState;
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

    private Paint defaultBackgroundPaint;
    private Paint increaseButtonPaint;
    private ViewState state = ViewState.IDLE;
    private OnProductChangeListener onProductChangeListener;
    // view to show count of increments
    private BoardView boardChild;
    // view to increment count
    private BoardItemView incrementChild;
    // view to decrement count
    private BoardItemView decrementChild;
    // start animation delay
    private long startDelay = 0;
    //
    private ImageView showBoardViews;
    private int radius;
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
        // init views
        initBoardViews();
        // set click listener
        setOnClickListener(this);

    }

    private void addShadow(float radius) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, increaseButtonPaint);
        }
        increaseButtonPaint.setShadowLayer(radius, 0.0f, radius, ContextCompat.getColor(getContext(), R.color.shadow_color));
    }

    private void initDefaultValues() {
        // empty
    }

    private void initBoardViews() {
        boardChild = new BoardView(getContext());
        boardChild.setAlpha(0f);
        incrementChild = new BoardItemView(getContext());
        incrementChild.setAlpha(0f);
        incrementChild.setType(BoardItemType.INCREMENT_TYPE);
        incrementChild.setOnClickListener(onIncrementClick);
        decrementChild = new BoardItemView(getContext());
        decrementChild.setAlpha(0f);
        decrementChild.setType(BoardItemType.DECREMENT_TYPE);
        decrementChild.setOnClickListener(onDecrementClick);
        showBoardViews = new ImageView(getContext());
        showBoardViews.setImageResource(R.drawable.plus);
    }

    private void initDefaultBackgroundPaint() {
        defaultBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultBackgroundPaint.setColor(Color.parseColor("#435E69"));
        defaultBackgroundPaint.setStyle(Paint.Style.STROKE);
        defaultBackgroundPaint.setStrokeWidth(STROKE_WIDTH);
    }

    private void initIncreaseButtonPaint() {
        increaseButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        increaseButtonPaint.setColor(Color.parseColor("#435E69"));
        increaseButtonPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(incrementChild);
        addView(decrementChild);
        addView(boardChild);
        addView(showBoardViews);
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
        radius = (int) (centerX * RADIUS_SCALE_FACTOR);
        // default radius for main circle
        defaultRadius = centerX - radius - STROKE_WIDTH * 2;
        // calculate size for board view
        int viewSize = radius * 2;
        // measure board view
        boardChild.measure(viewSize, viewSize);
        // calculate size for increment and decrement view
        int scaleViewSize = (int) (viewSize * BOARD_ITEM_SCALE);
        // measure views
        incrementChild.measure(scaleViewSize, scaleViewSize);
        decrementChild.measure(scaleViewSize, scaleViewSize);
        measureChild(showBoardViews, scaleViewSize, scaleViewSize);
        addShadow(3);
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
        showBoardViews.layout(left, top, right, bottom);
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
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 2;
        onParentClick(cx, cy, radius);
    }

    private OnClickListener onIncrementClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            scaleBoardView(incrementChild);
            if(boardChild != null) {
                boardChild.increment();
                if(onProductChangeListener != null) {
                    onProductChangeListener.onCountChange(boardChild.getCount());
                }
            }
        }
    };

    private OnClickListener onDecrementClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            scaleBoardView(decrementChild);
            if(boardChild != null) {
                boardChild.decrement();
                if(onProductChangeListener != null) {
                    onProductChangeListener.onCountChange(boardChild.getCount());
                }
            }
        }
    };

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
                set.setDuration(100);
                set.start();
            }
        });
        set.playTogether(scaleXAnimator, scaleYAnimator);
        set.setDuration(100);
        set.start();
    }

    private void onParentClick(float cx, float cy, float radius) {
        if(state == ViewState.IDLE) {
            changeBackgroundsColor("#4BBEC2");
            // expand views
            expandBoardViews(cx, cy, radius);
            // change parent state to have ability to close expanded views
            state = ViewState.EXPAND;
        } else {
            // reset product count
            boardChild.reset();
            idleBoardView(cx, cy, radius);
            state = ViewState.IDLE;
        }
    }

    private void changeBackgroundsColor(String color) {
        defaultBackgroundPaint.setColor(Color.parseColor(color));
        increaseButtonPaint.setColor(Color.parseColor(color));
        invalidate();
    }

    private void expandBoardViews(float cx, float cy, float radius) {
        startDelay = 0;
        // animate views to expand
        animateExpandView(incrementChild, cx, cy, radius, 55, 105, getStartDelay());
        incrementStartDelay();
        animateExpandView(boardChild, cx, cy, radius, 90, 120, getStartDelay());
        incrementStartDelay();
        animateExpandView(decrementChild, cx, cy, radius, 125, 90, getStartDelay());
        // animate view alpha
        animateViewAlpha(boardChild, 1f, 200);
        animateViewAlpha(incrementChild, 1f, 200);
        animateViewAlpha(decrementChild, 1f, 200);
    }

    private void idleBoardView(float cx, float cy, float radius) {
        // animate views
        animateExpandView(incrementChild, cx, cy, radius, 180, 105, 0);
        animateExpandView(boardChild, cx, cy, radius, 180, 120, 0)
                .addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        changeBackgroundsColor("#435E69");
                    }
                });
        animateExpandView(decrementChild, cx, cy, radius, 180, 90, 0);

        // animate view alpha
        animateViewAlpha(boardChild, 0f, 200);
        animateViewAlpha(incrementChild, 0f, 200);
        animateViewAlpha(decrementChild, 0f, 200);
    }


    private ArcAnimator animateExpandView(View view, float cx, float cy, float radius, float degree, float animationDegree, long startDelay) {
        float angle = (float) Math.toRadians(degree);
        float stopX = (float) (cx + (radius - this.radius - STROKE_WIDTH * 2) * Math.sin(angle));
        float stopY = (float) (cy - (radius - this.radius - STROKE_WIDTH * 2) * Math.cos(angle));
        ArcAnimator arcAnimator = ArcAnimator.createArcAnimator(view, stopX, stopY, animationDegree, Side.RIGHT);
        arcAnimator.setDuration(500);
        arcAnimator.setStartDelay(startDelay);
        arcAnimator.setInterpolator(new DecelerateInterpolator());
        arcAnimator.start();
        return arcAnimator;
    }

    private void animateViewAlpha(View view, float alpha, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", alpha);
        animator.setDuration(duration);
        animator.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawCircle(centerX, centerY + defaultRadius, radius + STROKE_WIDTH, increaseButtonPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, defaultRadius, defaultBackgroundPaint);
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
        startDelay += 60;
    }

    public long getStartDelay() {
        return startDelay;
    }

    public void setOnProductChangeListener(OnProductChangeListener onProductChangeListener) {
        this.onProductChangeListener = onProductChangeListener;
    }
}
