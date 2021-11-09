package com.hhkj.vgsbyhhkjnew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

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
    private Context context;
    float viewLastScal = 1f;
    float SCALE_MAX = viewLastScal * 25;//最大放大倍数
    float SCALE_MIX = 1f;//最大放大倍数
    private ScaleGestureDetector mScaleGestureDetector = null;

    public TestView1(Context context) {
        super(context);
    }

    public TestView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
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
    private float viewWidth; //  View放缩后的宽度
    private float viewHeight; // View放缩后的高度

    public void initData() {

    }

    private int width; //  屏幕的宽度
    private int height; // 屏幕的高度
    float k = 30f;
    float minK = 30f;
    float areaPersent = 10f;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        list = new ArrayList<bora>();
        k = k * viewLastScal;
        for (int i = 1; i < 5; i++) {
            //计算矩形的点击区域
            float x1 = i * k;
            float y1 = i * k;
            float x2 = width * viewLastScal - i * k;
            float y2 = height * viewLastScal - i * k;
            ArrayList<Float[]> area = new ArrayList<Float[]>();
            area.add(new Float[]{x1 - areaPersent, y1 - areaPersent, x1 + areaPersent, y2 + areaPersent});//左
            area.add(new Float[]{x1 - areaPersent, y1 - areaPersent, x2 + areaPersent, y1 + areaPersent});//上
            area.add(new Float[]{x2 - areaPersent, y1 - areaPersent, x2 + areaPersent, y2 + areaPersent});//右
            area.add(new Float[]{x1 - areaPersent, y2 - areaPersent, x2 + areaPersent, y2 + areaPersent});//下
            list.add(new bora(i, new Float[]{x1, y1, x2, y2}, area));
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
        float left = getLeft();
        float top = getTop();
        float scaleFactor = detector.getScaleFactor();
        Log.e(TAG, "scaleFactor：" + scaleFactor);
        viewLastScal = viewLastScal * scaleFactor;
        Log.e(TAG, "viewLastScal：" + viewLastScal);
        if (viewLastScal > SCALE_MAX) {//超限，太大了
            viewLastScal = SCALE_MAX;
        } else if (viewLastScal < SCALE_MIX) {//太小了
            viewLastScal = SCALE_MIX;
        }
        k = k * scaleFactor;
        if (k < minK) {
            k = minK;
        }
        for (int i = 0; i < list.size(); i++) {
            //计算缩放后的可点击区域：将矩形增加宽度区域为四个矩形（四条边加粗形成四个矩形）
            float x1 = (i + 1) * k;
            float y1 = (i + 1) * k;
            float x2 = width * viewLastScal - (i + 1) * k;
            float y2 = height * viewLastScal - (i + 1) * k;
            list.get(i).value = new Float[]{x1, y1, x2, y2};
            list.get(i).area.clear();
            ArrayList<Float[]> area = new ArrayList<Float[]>();
            area.add(new Float[]{x1 - areaPersent, y1 - areaPersent, x1 + areaPersent, y2 + areaPersent});//左
            area.add(new Float[]{x1 - areaPersent, y1 - areaPersent, x2 + areaPersent, y1 + areaPersent});//上
            area.add(new Float[]{x2 - areaPersent, y1 - areaPersent, x2 + areaPersent, y2 + areaPersent});//右
            area.add(new Float[]{x1 - areaPersent, y2 - areaPersent, x2 + areaPersent, y2 + areaPersent});//下
            list.get(i).area.addAll(area);
        }
        viewWidth = width * viewLastScal;
        viewHeight = height * viewLastScal;

        //在放大的过程中，跟随手指的中心位置，同步计算移动的方向和距离
        //左端距中心位置的x值的放缩量，即是水平方向需要挪动的量，判断边界即可
        Log.e(TAG, "getLeft：" + getLeft());

        float dx = pivotX * (scaleFactor - 1);//dx>0即放大，需要向左移动dx来保证中心点不变，dx<0即缩小，需要向右移动dx来保证中心点不变
        float dy = pivotY * (scaleFactor - 1);//dy>0即放大，需要向上移动dy来保证中心点不变，dy<0即缩小，需要向下移动dy来保证中心点不变
        int l, r, t, b;
        l = (int) (getLeft() - dx);
        r = (int) (l + viewWidth);
        t = (int) (getTop() - dy);
        b = (int) (t + viewHeight);
        Log.e(TAG, "l-：" + l + ";t-:" + t + ";r-:" + r + ";b-:" + b);
        // 如果你的需求是可以划出边界 此时你要计算可以划出边界的偏移量
        // 最大不能超过自身宽度或者是高度
        if (dx > 0) {//往左滑动
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
        if (dy > 0) {
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
        Log.e(TAG, "放缩：" + "l：" + l + ";t:" + t + ";r:" + r + ";b:" + b);
        this.layout(l, t, r, b); // 重置view在layout 中位置

        invalidate();

           /* this.setPivotX(pivotX);
            this.setPivotY(pivotY);*/

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
    private long currentMS, currentMS1, currentMS2;
    private float pivotX, pivotY;

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
                    currentMS2 = System.currentTimeMillis();
                    if (currentMS2 - currentMS1 < 500) {
                        return true;//解决放缩两指未同时离开屏幕的抖动
                    }
                    Log.e(TAG, "ACTION_MOVE");
                    dx = event.getRawX() - downX;
                    dy = event.getRawY() - downY;
                    Log.e(TAG, "dx：" + dx + ";dy:" + dy);
                    int l, r, t, b; // 上下左右四点移动后的偏移量
                    //计算偏移量 设置偏移量 = 20 时 为判断点击事件和滑动事件的峰值
                    if (Math.abs(dx) > 20 || Math.abs(dy) > 20) {
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
                        //遍历点击的图元，找出id

                        int id = getModelId(event.getX(), event.getY());
                        Log.e(TAG, "单击id:" + id);
                        if (id != -1) {
                            Toast.makeText(context, "您选中了" + id, Toast.LENGTH_LONG).show();
                        }
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
            pivotX = x;
            pivotY = y;
            currentMS1 = System.currentTimeMillis();
        }
        return true;
    }


    /**
     * 获取选中的模型Id
     */
    private int getModelId(float x, float y) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getArea().size(); j++) {
                //判断是否落在矩形区域
                float a = list.get(i).getArea().get(j)[0];
                float b = list.get(i).getArea().get(j)[1];
                float c = list.get(i).getArea().get(j)[2];
                float d = list.get(i).getArea().get(j)[3];
                if (a < x && x < c && b < y && y < d) {
                    return list.get(i).id;
                }
            }
        }
        return -1;
    }


    // 处理点击事件和滑动时间冲突时使用 返回是否拖动标识
    public boolean isDrag() {
        return isDrag;
    }


    //===========================================================================================
    class bora {
        public int id;

        public bora(int id, Float[] value, ArrayList<Float[]> area) {
            this.id = id;
            this.value = value;
            this.area = area;
        }

        public Float[] value = new Float[4];
        public ArrayList<Float[]> area;

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

        public ArrayList<Float[]> getArea() {
            return area;
        }

        public void setArea(ArrayList<Float[]> area) {
            this.area = area;
        }
    }
}
