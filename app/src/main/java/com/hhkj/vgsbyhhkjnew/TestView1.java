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

    float viewLastScal = 2f;
    float SCALE_MAX = viewLastScal * 25;//最大放大倍数
    float SCALE_MIX = 1f;//最大放大倍数
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
        initData();
    }

    ArrayList<bora> list;
    private float viewWidth; //  测量宽度 FreeView的宽度
    private float viewHeight; // 测量高度 FreeView的高度

    public void initData() {

    }

    private int width; //  测量宽度 FreeView的宽度
    private int height; // 测量高度 FreeView的高度
    float k = 30f;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        list = new ArrayList<bora>();
        for (int i = 1; i < 6; i++) {
            list.add(new bora(i, new Float[]{i * k, i * k, width * viewLastScal - i * k, height * viewLastScal - i * k}));
        }
        viewWidth = width * viewLastScal;
        viewHeight = height * viewLastScal;
        Log.e(TAG, "width：" + width + ";height:" + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < list.size(); i++) {
            canvas.drawRect(list.get(i).getValue()[0], list.get(i).getValue()[1], list.get(i).getValue()[2], list.get(i).getValue()[3], magicPaint);
        }
        Log.e(TAG, "lf：" + getLeft() + ";tp:" + getTop() + ";rg:" + getRight() + ";bt:" + getBottom());
    }

    private static final String TAG = TestView1.class.getSimpleName();

    //===========================================================================================
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        Log.e(TAG, "scaleFactor：" + scaleFactor);
        viewLastScal = viewLastScal * scaleFactor;
        Log.e(TAG, "lastScalSize：" + viewLastScal);
        if (viewLastScal > SCALE_MAX || viewLastScal < SCALE_MIX) {
            //超限，不做操作
        } else {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).value = new Float[]{i * k, i * k, width * viewLastScal - i * k, height * viewLastScal - i * k};
            }
            viewWidth = width * viewLastScal;
            viewHeight = height * viewLastScal;
            invalidate();
        }
        return true;

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
    private long currentMS;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouch");
        Log.e(TAG, "lf：" + getLeft() + ";tp:" + getTop() + "rg:" + getRight() + "bt:" + getBottom());
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        float x = 0, y = 0;
        float dx = 0;
        float dy = 0;
        if (pointerCount == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://单击
                    Log.e(TAG, "ACTION_DOWN==");
                    isDrag = false;
                    downX = event.getRawX(); // 点击触屏时的x坐标 用于离开屏幕时的x坐标作计算
                    downY = event.getRawY(); // 点击触屏时的y坐标 用于离开屏幕时的y坐标作计算
                    currentMS = System.currentTimeMillis();//long currentMS     获取系统时间
                    break;
                case MotionEvent.ACTION_MOVE://拖动
                    Log.e(TAG, "ACTION_MOVE");
                    dx = event.getRawX() - downX;
                    dy = event.getRawY() - downY;
                    Log.e(TAG, "dx：" + dx + ";dy:" + dy);
                    int l, r, t, b; // 上下左右四点移动后的偏移量
                    //计算偏移量 设置偏移量 = 3 时 为判断点击事件和滑动事件的峰值
                    if (Math.abs(dx) > 3 || Math.abs(dy) > 3) {
                        // 偏移量的绝对值大于 3 为 滑动时间 并根据偏移量计算四点移动后的位置
                        l = (int) (getLeft() + dx);
                        r = (int) (l + viewWidth);
                        t = (int) (getTop() + dy);
                        b = (int) (t + viewHeight);
                        Log.e(TAG, "l-：" + l + ";t-:" + t + ";r-:" + r + ";b-:" + b);
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
                            if (b < height) {
                                b = height;
                                t = (int) (height - viewHeight);
                            }
                        } else {
                            if (t > 0) {
                                t = 0;
                                b = (int) viewHeight;
                            }
                        }
                        Log.e(TAG, "l：" + l + ";t:" + t + ";r:" + r + ";b:" + b);
                        downX = event.getRawX();
                        downY = event.getRawY();
                        this.layout(l, t, r, b); // 重置view在layout 中位置
                        Log.e(TAG, "滑动事件");
                        isDrag = true;  // 重置 拖动为 true
                    } else {
                        isDrag = false; //为点击事件
                    }
                    // invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    long moveTime = System.currentTimeMillis() - currentMS;//移动时间
                    //判断是否继续传递信号
                    if (moveTime < 200 && (Math.abs(dx) < 20 || Math.abs(dy) < 20)) {
                        Log.e(TAG, "单击事件：x:" + event.getX() + ";Y:" + event.getY());
                        isDrag = false; //为点击事件

                        return true;
                    }
                    break;
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
            Log.e(TAG, "中心点：x：" + x + ";y:" + y);
            //根据中心点移动view





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
