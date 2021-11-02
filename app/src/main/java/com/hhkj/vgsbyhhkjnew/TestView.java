package com.hhkj.vgsbyhhkjnew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew
 * @ClassName: TestView
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/10/29 17:43
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class TestView extends View implements ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    public static final float SCALE_MAX = 15.0f;
    private static final float SCALE_MID = 5.0f;
    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    private final Matrix mScaleMatrix = new Matrix();
    /**
     * 用于双击检测
     */
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    /**
     * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
     */
    private float initScale = 4.0f;
    private boolean once = true;

    private float scale;

    private ZoomImageView.SingleTapListener singleTapListener;

    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();//准备工作
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale)
                            return true;
                        float x = e.getX();
                        float y = e.getY();
                        Log.e("DoubleTap", getScale() + " , " + initScale);
                        if (getScale() < SCALE_MID) {
                            TestView.this.postDelayed(
                                    new TestView.AutoScaleRunnable(SCALE_MID, x, y), 6);
                            isAutoScale = true;
                        } else if (getScale() >= SCALE_MID
                                && getScale() < SCALE_MAX) {
                            TestView.this.postDelayed(
                                    new TestView.AutoScaleRunnable(SCALE_MAX, x, y), 6);
                            isAutoScale = true;
                        } else {
                            TestView.this.postDelayed(
                                    new TestView.AutoScaleRunnable(initScale, x, y), 6);
                            isAutoScale = true;
                        }

                        return true;
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (singleTapListener != null) {
                            singleTapListener.doSingle();
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                });
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    // 线画笔
    private Paint magicPaint;

    private void init() {
        magicPaint = new Paint();
        magicPaint.setAntiAlias(true);
        magicPaint.setStrokeWidth(0);
        magicPaint.setStyle(Paint.Style.STROKE);
        magicPaint.setColor(Color.parseColor("#FFFFFFFF"));

    }

    ArrayList<bora> list;

    public void initData() {
        float h = getHeight();
        float w = getWidth();
        list = new ArrayList<bora>();
        float k = 40f;
        for (int i = 1; i < 11; i++) {
            list.add(new bora(i, new Float[]{i * k, i * k, w - i * k, h - i * k}));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initData();
        for (int i = 0; i < list.size(); i++) {
            canvas.drawRect(list.get(i).getValue()[0], list.get(i).getValue()[1], list.get(i).getValue()[2], list.get(i).getValue()[3], magicPaint);
        }
    }

    //===========================================================================================
    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * 自动缩放的任务
     *
     * @author zhy
     */
    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }

        }

        @Override
        public void run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                TestView.this.postDelayed(this, 6);
            } else
            // 设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }

    private void setImageMatrix(Matrix mScaleMatrix) {

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

       /* if (getDrawable() == null)
            return true;*/

        /**
         * 缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }
    private boolean isCanDrag;
    private int lastPointerCount;

    private boolean isCheckTopAndBottom = true;
    private boolean isCheckLeftAndRight = true;
    private float mLastX;
    private float mLastY;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);

        float x = 0, y = 0;
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y均值
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        lastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > getWidth() || rectF.height() > getHeight()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() || rectF.height() > getHeight()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                Log.e(TAG, "ACTION_MOVE");
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {

                    //if (getDrawable() != null) {

                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (rectF.width() < getWidth()) {
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }
                        // 如果高度小雨屏幕高度，则禁止上下移动
                        if (rectF.height() < getHeight()) {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }


                        mScaleMatrix.postTranslate(dx, dy);
                        checkMatrixBounds();
                        setImageMatrix(mScaleMatrix);
                   // }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG, "ACTION_UP");
                lastPointerCount = 0;
                break;
        }

        return true;
    }
    private int mTouchSlop;
    /**
     * 是否是推动行为
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }
    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕的
     */
    private void checkMatrixBounds() {
        RectF rect = getMatrixRectF();

        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();
        // 判断移动或缩放后，图片显示是否超出屏幕边界
        if (rect.top > 0 && isCheckTopAndBottom) {
            deltaY = -rect.top;
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom) {
            deltaY = viewHeight - rect.bottom;
        }
        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left;
        }
        if (rect.right < viewWidth && isCheckLeftAndRight) {
            deltaX = viewWidth - rect.right;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void onGlobalLayout() {
       /* Drawable d = getDrawable();
        if (d == null) return;
        if (once) {
            Log.e(TAG, d.getIntrinsicWidth() + " , " + d.getIntrinsicHeight());
            int width = getWidth();
            int height = getHeight();
            // 拿到图片的宽和高
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            scale = 1f;
            // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
            if (dw > width && dh > height) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            } else if (dw > width && dh <= height) {// 如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
                scale = width * 1.0f / dw;
            } else if (dh > height && dw <= width) {
                scale = height * 1.0f / dh;
            } else if (dh < height && dw < width) {//图片长宽都小于屏幕，则放大至屏幕的宽或者高
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }
            initScale = scale;

            Log.e(TAG, "initScale = " + initScale);
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2,
                    getHeight() / 2);
            // 图片移动至屏幕中心
            setImageMatrix(mScaleMatrix);
            once = false;
        }*/
    }
    private static final String TAG = TestView.class.getSimpleName();
    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        Log.e(TAG, "deltaX = " + deltaX + " , deltaY = " + deltaY);

        mScaleMatrix.postTranslate(deltaX, deltaY);

    }
    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
     /*   Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }*/
        return rect;
    }
    //===========================================================================================
    class bora {
        public int id;

        public bora(int id, Float[] value) {
            this.id = id;
            this.value = value;
        }

        public Float[] value = new Float[4];

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Float[] getValue() {
            return value;
        }

        public void setValue(Float[] value) {
            this.value = value;
        }
    }
}
