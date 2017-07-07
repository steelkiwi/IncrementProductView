package com.steelkiwi.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.steelkiwi.library.animator.ArcAnimator;
import com.steelkiwi.library.animator.Side;
import com.steelkiwi.library.listener.OnStateListener;
import com.steelkiwi.library.util.BoardItemType;
import com.steelkiwi.library.util.ConfirmationState;
import com.steelkiwi.library.util.Constants;
import com.steelkiwi.library.util.ViewState;
import com.steelkiwi.library.view.ConfirmationBoardView;
import com.steelkiwi.library.view.BoardItemView;
import com.steelkiwi.library.view.BoardView;


/**
 * Created by yaroslav on 6/28/17.
 */

public class IncrementProductView extends ViewGroup implements View.OnClickListener {

    private static final float RADIUS_SCALE_FACTOR = .23f;
    private static final float BOARD_ITEM_SCALE = .8f;
    private static final int START_DELAY_PERIOD = 40;
    private static final int DELAY_100 = 100;
    private static final int DELAY_300 = 300;
    private static final int DELAY_500 = 600;
    private static final float END_SCALE = 1f;

    private Paint defaultBackgroundPaint;
    private Paint increaseButtonPaint;
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
    // view for manage board state
    private ConfirmationBoardView confirmationBoardView;
    // radius for confirmation board view
    private int mainViewRadius;
    // parent center of X axis
    private int centerX;
    // parent center of Y axis
    private int centerY;
    // radius for parent circle
    private int defaultRadius;
    // main icon bitmap
    private Drawable mainIconDrawable;
    // icon for increment view
    private Bitmap incrementIcon;
    // icon for decrement view
    private Bitmap decrementIcon;
    // icon for confirmation view
    private Bitmap addIcon;
    private Bitmap confirmIcon;
    // default background color
    private int defaultBackgroundColor;
    // highlight background color
    private int highLightBackgroundColor;
    // board view background color
    private int boardBackgroundColor;
    // board view text size
    private float boardTextSize;
    // board view text color
    private int boardTextColor;
    // stroke width
    private int strokeWidth;


    public IncrementProductView(Context context) {
        super(context);
        init(null);
    }

    public IncrementProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public IncrementProductView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        retrieveAttributes(attrs);
        initDefaultBackgroundPaint();
        initIncreaseButtonPaint();
        initBoardViews();

    }

    private void retrieveAttributes(AttributeSet attributes) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributes, R.styleable.IncrementProductView);
        setParentMiddleIcon(typedArray.getDrawable(R.styleable.IncrementProductView_ipv_middle_icon));
        setIncrementIcon(typedArray.getDrawable(R.styleable.IncrementProductView_ipv_increment_icon));
        setDecrementIcon(typedArray.getDrawable(R.styleable.IncrementProductView_ipv_decrement_icon));
        setAddIcon(typedArray.getDrawable(R.styleable.IncrementProductView_ipv_add_icon));
        setConfirmIcon(typedArray.getDrawable(R.styleable.IncrementProductView_ipv_confirm_icon));
        setDefaultBackgroundColor(typedArray.getColor(R.styleable.IncrementProductView_ipv_default_background_color,
                ContextCompat.getColor(getContext(), R.color.default_background_color)));
        setHighLightBackgroundColor(typedArray.getColor(R.styleable.IncrementProductView_ipv_highlight_background_color,
                ContextCompat.getColor(getContext(), R.color.highlight_background_color)));
        setBoardBackgroundColor(typedArray.getColor(R.styleable.IncrementProductView_ipv_counter_background_color,
                ContextCompat.getColor(getContext(), R.color.counter_background_color)));
        setBoardTextColor(typedArray.getColor(R.styleable.IncrementProductView_ipv_text_color,
                ContextCompat.getColor(getContext(), android.R.color.white)));
        setBoardTextSize(typedArray.getDimensionPixelSize(R.styleable.IncrementProductView_ipv_text_size,
                getResources().getDimensionPixelSize(R.dimen.text_size)));
        typedArray.recycle();
    }

    private void initBoardViews() {
        boardChild = new BoardView(getContext());
        boardChild.setDefaultBackgroundColor(getBoardBackgroundColor(), getHighLightBackgroundColor());
        boardChild.setTextParameters(getBoardTextSize(), getBoardTextColor());
        // init increment view
        incrementChild = new BoardItemView(getContext());
        incrementChild.setType(BoardItemType.INCREMENT_TYPE);
        incrementChild.setDefaultBackgroundColor(getHighLightBackgroundColor());
        incrementChild.setIncrementBitmap(getIncrementIcon());
        incrementChild.setDecrementBitmap(getDecrementIcon());
        incrementChild.setOnClickListener(onIncrementClick);
        // init decrement view
        decrementChild = new BoardItemView(getContext());
        decrementChild.setType(BoardItemType.DECREMENT_TYPE);
        decrementChild.setDefaultBackgroundColor(getHighLightBackgroundColor());
        decrementChild.setIncrementBitmap(getIncrementIcon());
        decrementChild.setDecrementBitmap(getDecrementIcon());
        decrementChild.setOnClickListener(onDecrementClick);
        // init button to confirmation
        confirmationBoardView = new ConfirmationBoardView(getContext());
        confirmationBoardView.setBitmapResource(getAddIcon());
        confirmationBoardView.setBackgroundColor(getDefaultBackgroundColor());
        confirmationBoardView.setOnClickListener(this);
    }

    private void initDefaultBackgroundPaint() {
        strokeWidth = getResources().getDimensionPixelSize(R.dimen.parent_stroke_size);
        defaultBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultBackgroundPaint.setColor(getDefaultBackgroundColor());
        defaultBackgroundPaint.setStyle(Paint.Style.STROKE);
        defaultBackgroundPaint.setStrokeWidth(strokeWidth);
    }

    private void initIncreaseButtonPaint() {
        increaseButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        increaseButtonPaint.setColor(getDefaultBackgroundColor());
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
        defaultRadius = centerX - mainViewRadius - strokeWidth * 2;
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
                // if count is 0 close board view
                if(boardChild.getCount() == 0) {
                    confirmationState = ConfirmationState.OPEN;
                    manageBoardViewState(true);
                    onClose();
                }
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

    private void onClose() {
        if(onStateListener != null) {
            onStateListener.onClose();
        }
    }

    private void scaleBoardView(final View view) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", BOARD_ITEM_SCALE);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", BOARD_ITEM_SCALE);
        set.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                AnimatorSet set = new AnimatorSet();
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", END_SCALE);
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", END_SCALE);
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
            changeBackgroundsColor(getHighLightBackgroundColor());
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

    private void changeBackgroundsColor(int color) {
        defaultBackgroundPaint.setColor(color);
        increaseButtonPaint.setColor(color);
        confirmationBoardView.setBackgroundColor(color);
        confirmationBoardView.setBitmapResource(getConfirmIcon());
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
                        changeBackgroundsColor(getDefaultBackgroundColor());
                        confirmationBoardView.setBackgroundColor(getDefaultBackgroundColor());
                        confirmationBoardView.setBitmapResource(getAddIcon());
                    }
                });
        animateExpandView(decrementChild, cx, cy, radius,
                Constants.Degree.DEGREE_180, Constants.Degree.DEGREE_40, 0, DELAY_300);
    }


    private ArcAnimator animateExpandView(View view, float cx, float cy, float radius,
                                          float degree, float animationDegree, long startDelay, long delay) {
        float angle = (float) Math.toRadians(degree);
        float stopX = (float) (cx + (radius - mainViewRadius - strokeWidth * 2) * Math.sin(angle));
        float stopY = (float) (cy - (radius - mainViewRadius - strokeWidth * 2) * Math.cos(angle));
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
        drawMainCircle(canvas);
        drawMainIcon(canvas);
    }

    private void drawMainCircle(final Canvas canvas) {
        canvas.drawCircle(centerX, centerY, defaultRadius, defaultBackgroundPaint);
    }

    private void drawMainIcon(final Canvas canvas) {
        if(mainIconDrawable != null) {
            Bitmap bitmap = convertToBitmap(mainIconDrawable, (int) (defaultRadius * 1.2f), (int) (defaultRadius * 1.2f));
            canvas.drawBitmap(bitmap, centerX - bitmap.getWidth() / 2,
                    centerY - bitmap.getHeight() / 2, defaultBackgroundPaint);
        }
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

    private Bitmap convertToBitmap(Drawable drawable, int width, int height) {
        if(drawable != null) {
            Bitmap mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mutableBitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return mutableBitmap;
        }
        return null;
    }

    private int calculateBitmapSize() {
        return getWidth() / 2;
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

    public void setParentMiddleIcon(Drawable drawable) {
        mainIconDrawable = drawable;
    }

    public Bitmap getIncrementIcon() {
        return incrementIcon;
    }

    public void setIncrementIcon(Drawable drawable) {
        if(drawable != null) {
            this.incrementIcon = convertToBitmap(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else {
            this.incrementIcon = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        }
    }

    public Bitmap getDecrementIcon() {
        return decrementIcon;
    }

    public void setDecrementIcon(Drawable drawable) {
        if(drawable != null) {
            this.decrementIcon = convertToBitmap(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else {
            this.decrementIcon = BitmapFactory.decodeResource(getResources(), R.drawable.minus);
        }
    }

    public Bitmap getAddIcon() {
        return addIcon;
    }

    public void setAddIcon(Drawable drawable) {
        if(drawable != null) {
            this.addIcon = convertToBitmap(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else {
            this.addIcon = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        }
    }

    public Bitmap getConfirmIcon() {
        return confirmIcon;
    }

    public void setConfirmIcon(Drawable drawable) {
        if (drawable != null) {
            this.confirmIcon = convertToBitmap(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else {
            this.confirmIcon = BitmapFactory.decodeResource(getResources(), R.drawable.done);
        }
    }

    public int getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    public void setDefaultBackgroundColor(int defaultBackgroundColor) {
        this.defaultBackgroundColor = defaultBackgroundColor;
    }

    public int getHighLightBackgroundColor() {
        return highLightBackgroundColor;
    }

    public void setHighLightBackgroundColor(int highLightBackgroundColor) {
        this.highLightBackgroundColor = highLightBackgroundColor;
    }

    public int getBoardBackgroundColor() {
        return boardBackgroundColor;
    }

    public void setBoardBackgroundColor(int boardBackgroundColor) {
        this.boardBackgroundColor = boardBackgroundColor;
    }

    public float getBoardTextSize() {
        return boardTextSize;
    }

    public void setBoardTextSize(float boardTextSize) {
        this.boardTextSize = boardTextSize;
    }

    public int getBoardTextColor() {
        return boardTextColor;
    }

    public void setBoardTextColor(int boardTextColor) {
        this.boardTextColor = boardTextColor;
    }
}
