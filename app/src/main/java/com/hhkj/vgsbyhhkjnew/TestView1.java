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
public class TestView1 extends View implements ScaleGestureDetector.OnScaleGestureListener {

    float initScal = 1f;
    float SCALE_MAX = initScal * 20;//最大放大20倍
    float lastScalSize = initScal;//上次停留的放缩大小
    private ScaleGestureDetector mScaleGestureDetector = null;

    public TestView1(Context context) {
        super(context);
    }

    public TestView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();//准备工作
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
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
    private float viewWidth; //  测量宽度 FreeView的宽度
    private float viewHeight; // 测量高度 FreeView的高度

    public void initData() {
        float h = getHeight();
        float w = getWidth();
        list = new ArrayList<bora>();
        float k = 30f;
        for (int i = 1; i < 6; i++) {
            list.add(new bora(i, new Float[]{i * k, i * k, w * 2 - i * k, h * 2 - i * k}));
        }
        viewWidth = w * 2;
        viewHeight = h * 2;
    }

    private int width; //  测量宽度 FreeView的宽度
    private int height; // 测量高度 FreeView的高度

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        Log.e(TAG, "width：" + width + ";height:" + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initData();
        for (int i = 0; i < list.size(); i++) {
            canvas.drawRect(list.get(i).getValue()[0], list.get(i).getValue()[1], list.get(i).getValue()[2], list.get(i).getValue()[3], magicPaint);
        }
        Log.e(TAG, "lf：" + getLeft() + ";tp:" + getTop() + "rg:" + getRight() + "bt:" + getBottom());
    }

    private static final String TAG = TestView1.class.getSimpleName();

    //===========================================================================================
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        Log.e(TAG, "scaleFactor：" + scaleFactor);
        lastScalSize = lastScalSize * scaleFactor;
        if (lastScalSize > SCALE_MAX || lastScalSize < initScal) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.e(TAG, "onScaleBegin");
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.e(TAG, "onScaleEnd");
    }

    //是否拖动标识
    private boolean isDrag = false;
    private float downX; //点击时的x坐标
    private float downY;  // 点击时的y坐标

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouch");
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        float x = 0, y = 0;
        if (pointerCount == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://单击
                    Log.e(TAG, "ACTION_DOWN==");
                    isDrag = false;
                    downX = event.getX(); // 点击触屏时的x坐标 用于离开屏幕时的x坐标作计算
                    downY = event.getY(); // 点击触屏时的y坐标 用于离开屏幕时的y坐标作计算
                    break;
                case MotionEvent.ACTION_MOVE://拖动
                    Log.e(TAG, "ACTION_MOVE");
                    float dx = event.getX() - downX;
                    float dy = event.getY() - downY;
                    int l, r, t, b; // 上下左右四点移动后的偏移量
                    //计算偏移量 设置偏移量 = 3 时 为判断点击事件和滑动事件的峰值
                    if (Math.abs(dx) > 3 || Math.abs(dy) > 3) {
                        // 偏移量的绝对值大于 3 为 滑动时间 并根据偏移量计算四点移动后的位置
                        l = (int) (getLeft() + dx);
                        r = (int) (l + viewWidth);
                        t = (int) (getTop() + dy);
                        b = (int) (t + viewHeight);
                        // 如果你的需求是可以划出边界 此时你要计算可以划出边界的偏移量
                        // 最大不能超过自身宽度或者是高度
                        if (dx < 0) {//往左滑动
                            if (r < width) {
                                r = width;
                                l = (int) (width - viewWidth);
                            }
                        } else {
                            if (l > 0) {
                                l = 0;
                                r = (int) viewWidth;
                            }
                        }
                        if (dy < 0) {
                            if (b<height) {
                                b=height;
                                t= (int) (height-viewHeight);
                            }
                        } else {
                            if (t>0) {
                                t=0;
                                b=(int)viewHeight;
                            }
                        }
                        /*if (l < 0) { // left 小于 0 就是滑出边界 赋值为 0 ; right 右边的坐标就是自身宽度 如果可以划出边界 left right top bottom 最小值的绝对值 不能大于自身的宽高
                            l = 0;
                            r = l + width;
                        } else if (r > width) { // 判断 right 并赋值
                            r = width;
                            l = r - width;
                        }
                        if (t < 0) { // top
                            t = 0;
                            b = t + height;
                        } else if (b > height) { // bottom
                            b = height;
                            t = b - height;
                        }*/
                       /* l = (int) (getLeft() + dx);
                        r = (int) (l + ViewWidth);
                        t = (int) (getTop() + dy);
                        b = (int) (t + ViewHeight);*/
                        this.layout(l, t, r, b); // 重置view在layout 中位置
                        isDrag = true;  // 重置 拖动为 true
                    } else {
                        isDrag = false; // 小于峰值3时 为点击事件
                    }
                    /*mLastX = x;
                    mLastY = y;*/
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    Log.e(TAG, "ACTION_UP");
                    break;
            }
        } else if (pointerCount > 1) {//放缩
            mScaleGestureDetector.onTouchEvent(event);
            // 得到多个触摸点的x与y均值
            for (int i = 0; i < pointerCount; i++) {
                x += event.getX(i);
                y += event.getY(i);
            }
            x = x / pointerCount;
            y = y / pointerCount;//中心点
        }
        return true;
    }


    // 处理点击事件和滑动时间冲突时使用 返回是否拖动标识
    public boolean isDrag() {
        return isDrag;
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
