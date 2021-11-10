package com.hhkj.vgsbyhhkjnew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hhkj.vgsbyhhkjnew.bean.EllipseGeometry;
import com.hhkj.vgsbyhhkjnew.bean.Line;
import com.hhkj.vgsbyhhkjnew.bean.Point;
import com.hhkj.vgsbyhhkjnew.bean.PolygonGeometry;
import com.hhkj.vgsbyhhkjnew.bean.RectGeometry;
import com.hhkj.vgsbyhhkjnew.bean.TextGeometry;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew
 * @ClassName: CoreView
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/7/27 11:40
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class CoreView extends View implements ScaleGestureDetector.OnScaleGestureListener {
    private Context context;
    private ScaleGestureDetector mScaleGestureDetector = null;
    float SCALE_MAX;//最大放大倍数
    float SCALE_MIN;//最小缩小倍数

    public CoreView(Context context) {
        this(context, null);
    }

    public CoreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
        init();//准备工作
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public CoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    // 线画笔
    private Paint magicPaint;


    private void init() {
        System.out.println("===init");
        magicPaint = new Paint();
        magicPaint.setAntiAlias(true);
        magicPaint.setStrokeWidth(0);
        magicPaint.setStyle(Paint.Style.STROKE);
        magicPaint.setColor(Color.parseColor("#FFFFFFFF"));
        initData();
    }


    float maxx;
    float maxy;
    ArrayList<Shape> shapes;

    public void setData(ArrayList<Shape> shapes) {
        this.shapes = shapes;
    }

    public void initData() {
        System.out.println("===initData");
        if (shapes == null || !(shapes.size() > 0)) {
            return;
        }
        ArrayList xList = new ArrayList<Float>();
        ArrayList yList = new ArrayList<Float>();
        initBoundary(0, 0, shapes, xList, yList);

        maxx = (float) Collections.max(xList);
        maxy = (float) Collections.max(yList);

    }

    //递归计算所有图元的坐标值 用于计算边界
    private void initBoundary(float baseX, float baseY, ArrayList<Shape> shapeLists, ArrayList<Float> xList, ArrayList<Float> yList) {
        //System.out.println("===initBoundary");
        for (Shape shape : shapeLists) {
            switch (shape.getType()) {//0.组合
                case 0:
                    initBoundary(baseX + shape.x, baseY + shape.y, shape.shapes, xList, yList);
                    break;
                case 1://1.线段
                    if (((Line) shape.getStar()).getPoints() == null) {
                        continue;
                    }
                    String[] pointsArray = ((Line) shape.getStar()).getPoints().split(",");
                    for (int i = 0; i < pointsArray.length; i++) {
                        if (i % 2 == 0) {
                            xList.add(baseX + shape.x + Float.parseFloat(pointsArray[i]));
                        } else {
                            yList.add(baseY + shape.y + Float.parseFloat(pointsArray[i]));
                        }
                    }
                    break;
                case 2://2.多边形
                    if (((PolygonGeometry) shape.getStar()).getPoints() == null) {
                        continue;
                    }
                    String[] pointsArray1 = ((PolygonGeometry) shape.getStar()).getPoints().split(",");
                    for (int i = 0; i < pointsArray1.length; i++) {
                        if (i % 2 == 0) {
                            xList.add(baseX + shape.x + Float.parseFloat(pointsArray1[i]));
                        } else {
                            yList.add(baseY + shape.y + Float.parseFloat(pointsArray1[i]));
                        }
                    }
                    break;
                case 3: //3.矩形
                    if (TextUtils.isEmpty(((RectGeometry) shape.getStar()).getRectWidth()) || TextUtils.isEmpty(((RectGeometry) shape.getStar()).getRectHeight())) {
                        continue;
                    }
                    xList.add(baseX + shape.x + Float.parseFloat(((RectGeometry) shape.getStar()).getRectLeft()) + Float.parseFloat(((RectGeometry) shape.getStar()).getRectWidth()));
                    yList.add(baseY + shape.y + Float.parseFloat(((RectGeometry) shape.getStar()).getRectTop()) + Float.parseFloat(((RectGeometry) shape.getStar()).getRectHeight()));
                    break;
                case 4://4.椭圆
                    if (TextUtils.isEmpty(((EllipseGeometry) shape.getStar()).getRectWidth()) || TextUtils.isEmpty(((EllipseGeometry) shape.getStar()).getRectHeight())) {
                        continue;
                    }
                    xList.add(baseX + shape.x + Float.parseFloat(((EllipseGeometry) shape.getStar()).getRectLeft()) + Float.parseFloat(((EllipseGeometry) shape.getStar()).getRectWidth()));
                    yList.add(baseY + shape.y + Float.parseFloat(((EllipseGeometry) shape.getStar()).getRectTop()) + Float.parseFloat(((EllipseGeometry) shape.getStar()).getRectHeight()));
                    break;
                case 5://5.文字
                    if (TextUtils.isEmpty(((TextGeometry) shape.getStar()).getRectWidth()) || TextUtils.isEmpty(((TextGeometry) shape.getStar()).getRectHeight())) {
                        continue;
                    }
                    xList.add(baseX + shape.x + Float.parseFloat(((TextGeometry) shape.getStar()).getRectLeft()) + Float.parseFloat(((TextGeometry) shape.getStar()).getRectWidth()));
                    yList.add(baseY + shape.y + Float.parseFloat(((TextGeometry) shape.getStar()).getRectTop()) + Float.parseFloat(((TextGeometry) shape.getStar()).getRectHeight()));
                    break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initScaleValue();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawVgsView(canvas, shapes);
    }

    float scalpercent = 0.8f;
    float textpercent = 1.3f;
    float initScale;
    private int width; //  测量宽度 屏幕的宽度
    private int height; // 测量高度 屏幕的高度
    private float viewLastScal;//View的叠加放缩量

    private void initScaleValue() {
        System.out.println("===doScaleValue");
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        System.out.println("===width:" + width + ";height:" + height);
        float scalX = width / maxx;
        float scalY = height / maxy;
        initScale = scalX > scalY ? scalY : scalX;
        SCALE_MIN = initScale;
        SCALE_MAX = initScale * 50;
        viewLastScal = initScale;//初始化叠加放缩量

        if (initScale <= 1) {//图像过大，则进行缩小，图像过小，仅放大0.8倍
            scale(0, initScale, 0, 0, shapes);
        } else {
            scale(0, initScale * scalpercent, 0, 0, shapes);
        }
        viewWidth = width;//首次初始化，view的宽高设置为屏幕的宽高
        viewHeight = height;
    }

    //递归计算所有图元的坐标值 以及旋转角度
    private void scale(float baseAngle, float scal, float baseX, float baseY, ArrayList<Shape> shapeLists) {
        for (Shape shape : shapeLists) {
            if (!TextUtils.isEmpty(shape.RotateAngle)) {
                float angle = baseAngle + Float.parseFloat(shape.RotateAngle);
                shape.setAngle(angle);
            }
            switch (shape.getType()) {//0.组合
                case 0:
                    scale(baseAngle + Float.parseFloat(shape.RotateAngle), scal, baseX + shape.x, baseY + shape.y, shape.shapes);
                    break;
                case 1://1.线段
                    Line line = (Line) shape.getStar();
                    if (line.getPoints() == null) {
                        continue;
                    }
                    String[] pointsArray = line.getPoints().split(",");
                    ArrayList<Point> pointsValue = line.getPointsValue();
                    pointsValue.clear();
                    Point point = new Point();
                    for (int i = 0; i < pointsArray.length; i++) {
                        if (i % 2 == 0) {
                            point = new Point();
                            point.setX(scal * (baseX + shape.x + Float.parseFloat(pointsArray[i])));
                        } else {
                            point.setY(scal * (baseY + shape.y + Float.parseFloat(pointsArray[i])));
                            pointsValue.add(point);
                        }
                    }
                    //封装android自定义drawLines需要格式的数组  2*[(x-1)*2]   (xy两个点)*[(x-1 条线段)*中间重复点2倍关系]
                    ArrayList<Float> linepoints = line.getLinepoints();
                    linepoints.clear();
                    for (int i = 0; i < pointsValue.size(); i++) {
                        if (i == 0) {
                            linepoints.add(pointsValue.get(i).getX());
                            linepoints.add(pointsValue.get(i).getY());
                        } else if (i == pointsValue.size() - 1) {
                            linepoints.add(pointsValue.get(i).getX());
                            linepoints.add(pointsValue.get(i).getY());
                        } else {
                            linepoints.add(pointsValue.get(i).getX());
                            linepoints.add(pointsValue.get(i).getY());
                            linepoints.add(pointsValue.get(i).getX());
                            linepoints.add(pointsValue.get(i).getY());
                        }
                    }
                    break;
                case 2://2.多边形
                    PolygonGeometry polygonGeometry = (PolygonGeometry) shape.getStar();
                    if (polygonGeometry.getPoints() == null) {
                        continue;
                    }
                    String[] polygonGeometryPointsArray = polygonGeometry.getPoints().split(",");
                    ArrayList<Point> polygonGeometryPointsValue = polygonGeometry.getPolygonGeometryPointsValue();
                    polygonGeometryPointsValue.clear();
                    Point polygonGeometryPoint = new Point();
                    for (int i = 0; i < polygonGeometryPointsArray.length; i++) {
                        if (i % 2 == 0) {
                            polygonGeometryPoint = new Point();
                            polygonGeometryPoint.setX(scal * (baseX + shape.x + Float.parseFloat(polygonGeometryPointsArray[i])));
                        } else {
                            polygonGeometryPoint.setY(scal * (baseY + shape.y + Float.parseFloat(polygonGeometryPointsArray[i])));
                            polygonGeometryPointsValue.add(polygonGeometryPoint);
                        }
                    }
                    //封装android自定义drawLines需要格式的数组  2*[(x-1)*2]   (xy两个点)*[(x-1 条线段)*中间重复点2倍关系]
                    ArrayList<Float> polygonGeometryLinepoints = polygonGeometry.getPolygonGeometryLinepoints();
                    polygonGeometryLinepoints.clear();
                    for (int i = 0; i < polygonGeometryPointsValue.size(); i++) {
                        if (i == 0) {
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getX());
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getY());
                        } else if (i == polygonGeometryPointsValue.size() - 1) {
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getX());
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getY());
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getX());
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getY());
                            //多边形首尾相接
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(0).getX());
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(0).getY());
                        } else {
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getX());
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getY());
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getX());
                            polygonGeometryLinepoints.add(polygonGeometryPointsValue.get(i).getY());
                        }
                    }
                    break;
                case 3://3.矩形
                    RectGeometry rectGeometry = (RectGeometry) shape.getStar();
                    if (TextUtils.isEmpty(((RectGeometry) shape.getStar()).getRectWidth()) || TextUtils.isEmpty(((RectGeometry) shape.getStar()).getRectHeight())) {
                        continue;
                    }
                    float x1 = Float.parseFloat(((RectGeometry) shape.getStar()).getRectLeft());
                    float y1 = Float.parseFloat(((RectGeometry) shape.getStar()).getRectTop());
                    float x2 = Float.parseFloat(((RectGeometry) shape.getStar()).getRectWidth());
                    float y2 = Float.parseFloat(((RectGeometry) shape.getStar()).getRectHeight());
                    rectGeometry.getValue()[0] = scal * (baseX + shape.x + x1);
                    rectGeometry.getValue()[1] = scal * (baseY + shape.y + y1);
                    rectGeometry.getValue()[2] = scal * (baseX + shape.x + x1 + x2);
                    rectGeometry.getValue()[3] = scal * (baseY + shape.y + y1 + y2);
                    break;
                case 4://4.椭圆
                    EllipseGeometry ellipseGeometry = (EllipseGeometry) shape.getStar();
                    if (TextUtils.isEmpty(((EllipseGeometry) shape.getStar()).getRectWidth()) || TextUtils.isEmpty(((EllipseGeometry) shape.getStar()).getRectHeight())) {
                        continue;
                    }
                    float x1ellipseGeometry = Float.parseFloat(((EllipseGeometry) shape.getStar()).getRectLeft());
                    float y1ellipseGeometry = Float.parseFloat(((EllipseGeometry) shape.getStar()).getRectTop());
                    float x2ellipseGeometry = Float.parseFloat(((EllipseGeometry) shape.getStar()).getRectWidth());
                    float y2ellipseGeometry = Float.parseFloat(((EllipseGeometry) shape.getStar()).getRectHeight());
                    ellipseGeometry.getValue()[0] = scal * (baseX + shape.x + x1ellipseGeometry);
                    ellipseGeometry.getValue()[1] = scal * (baseY + shape.y + y1ellipseGeometry);
                    ellipseGeometry.getValue()[2] = scal * (baseX + shape.x + x1ellipseGeometry + x2ellipseGeometry);
                    ellipseGeometry.getValue()[3] = scal * (baseY + shape.y + y1ellipseGeometry + y2ellipseGeometry);
                    break;
                case 5://5.文字
                    TextGeometry textGeometry = (TextGeometry) shape.getStar();
                    if (TextUtils.isEmpty(((TextGeometry) shape.getStar()).getRectWidth()) || TextUtils.isEmpty(((TextGeometry) shape.getStar()).getRectHeight())) {
                        continue;
                    }
                    textGeometry.setScalTextSize(scal * textpercent * textGeometry.getTextSize());
                    float x1textGeometry = Float.parseFloat(((TextGeometry) shape.getStar()).getRectLeft());
                    float y1textGeometry = Float.parseFloat(((TextGeometry) shape.getStar()).getRectTop());
                    float x2textGeometry = Float.parseFloat(((TextGeometry) shape.getStar()).getRectWidth());
                    float y2textGeometry = Float.parseFloat(((TextGeometry) shape.getStar()).getRectHeight());
                    textGeometry.getValue()[0] = scal * (baseX + shape.x + x1textGeometry);
                    textGeometry.getValue()[1] = scal * (baseY + shape.y + y1textGeometry);
                    textGeometry.getValue()[2] = scal * (baseX + shape.x + x1textGeometry + x2textGeometry);
                    textGeometry.getValue()[3] = scal * (baseY + shape.y + y1textGeometry + y2textGeometry);
                    break;
            }
        }
    }

    //画图
    private void drawVgsView(Canvas canvas, ArrayList<Shape> shapeLists) {
        //System.out.println("===drawVgsView");
        if (shapeLists == null || !(shapeLists.size() > 0)) {
            return;
        }
        for (Shape shape : shapeLists) {
            canvas.save();

            switch (shape.getType()) {//0.组合
                case 0:
                    drawVgsView(canvas, shape.shapes);
                    break;
                case 1://1.线段
                    Line line = (Line) shape.getStar();
                    if (line.getLinepoints().size() == 0) {
                        continue;
                    }
                    float[] floats = new float[line.getLinepoints().size()];
                    for (int i = 0; i < line.getLinepoints().size(); i++) {
                        floats[i] = line.getLinepoints().get(i);
                    }
                    //旋转
                    float rotateCenterX = floats[0] + (floats[floats.length - 2] - floats[0]) / 2;
                    float rotateCenterY = floats[1] + (floats[floats.length - 1] - floats[1]) / 2;
                    canvas.rotate(shape.angle, rotateCenterX, rotateCenterY);
                    if (!TextUtils.isEmpty(line.getColor())) {
                        magicPaint.setColor(Color.parseColor(line.getColor()));
                    }
                    //画
                    canvas.drawLines(floats, magicPaint);

                    break;
                case 2://2.多边形
                    PolygonGeometry polygonGeometry = (PolygonGeometry) shape.getStar();
                    if (polygonGeometry.getPolygonGeometryLinepoints().size() == 0) {
                        continue;
                    }
                    float[] floats1 = new float[polygonGeometry.getPolygonGeometryLinepoints().size()];
                    for (int i = 0; i < polygonGeometry.getPolygonGeometryLinepoints().size(); i++) {
                        floats1[i] = polygonGeometry.getPolygonGeometryLinepoints().get(i);
                    }
                    //计算旋转中心：多边形的旋转中心为包围多边形的矩形中心，需要计算矩形的顶点坐标，再计算矩形中心点
                    Point p = playpolygonCenterPoint(polygonGeometry.getPolygonGeometryPointsValue());
                    canvas.rotate(shape.angle, p.getX(), p.getY());
                    if (!TextUtils.isEmpty(polygonGeometry.getColor())) {
                        magicPaint.setColor(Color.parseColor(polygonGeometry.getColor()));
                    }
                    //画
                    canvas.drawLines(floats1, magicPaint);
                    break;
                case 3://3.矩形
                    RectGeometry rectGeometry = (RectGeometry) shape.getStar();
                    if (TextUtils.isEmpty(rectGeometry.getRectWidth()) || TextUtils.isEmpty(rectGeometry.getRectHeight())) {
                        continue;
                    }
                    Point p1 = playRectCenterPoint(rectGeometry.getValue());
                    canvas.rotate(shape.angle, p1.getX(), p1.getY());
                    if (!TextUtils.isEmpty(rectGeometry.getColor())) {
                        magicPaint.setColor(Color.parseColor(rectGeometry.getColor()));
                    }
                    if (shape.getFill()) {
                        magicPaint.setStyle(Paint.Style.FILL);
                        magicPaint.setColor(Color.parseColor(shape.fillColor));
                    }
                    canvas.drawRect(new RectF(rectGeometry.getValue()[0], rectGeometry.getValue()[1], rectGeometry.getValue()[2], rectGeometry.getValue()[3]), magicPaint);
                    break;
                case 4://4.椭圆
                    EllipseGeometry ellipseGeometry = (EllipseGeometry) shape.getStar();
                    if (TextUtils.isEmpty(ellipseGeometry.getRectWidth()) || TextUtils.isEmpty(ellipseGeometry.getRectHeight())) {
                        continue;
                    }
                    Point p1ellipseGeometry = playRectCenterPoint(ellipseGeometry.getValue());
                    canvas.rotate(shape.angle, p1ellipseGeometry.getX(), p1ellipseGeometry.getY());
                    if (!TextUtils.isEmpty(ellipseGeometry.getColor())) {
                        magicPaint.setColor(Color.parseColor(ellipseGeometry.getColor()));
                    }
                    if (shape.getFill()) {
                        magicPaint.setStyle(Paint.Style.FILL);
                        magicPaint.setColor(Color.parseColor(shape.fillColor));
                    }
                    canvas.drawOval(new RectF(ellipseGeometry.getValue()[0], ellipseGeometry.getValue()[1], ellipseGeometry.getValue()[2], ellipseGeometry.getValue()[3]), magicPaint);
                    break;
                case 5://5.文字
                    TextGeometry textGeometry = (TextGeometry) shape.getStar();
                    if (TextUtils.isEmpty(textGeometry.getRectWidth()) || TextUtils.isEmpty(textGeometry.getRectHeight())) {
                        continue;
                    }
                    Point p1textGeometry = playRectCenterPoint(textGeometry.getValue());
                    canvas.rotate(shape.angle, p1textGeometry.getX(), p1textGeometry.getY());
                    magicPaint.setTextSize(textGeometry.getScalTextSize());
                    RectF rectF = new RectF(textGeometry.getValue()[0], textGeometry.getValue()[1], textGeometry.getValue()[2], textGeometry.getValue()[3]);
                    //canvas.drawRect(rectF, magicPaint);
                    //计算baseline
                    Paint.FontMetrics fontMetrics = magicPaint.getFontMetrics();
                    float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
                    float baseline = rectF.centerY() + distance;
                    if (!TextUtils.isEmpty(textGeometry.getColor())) {
                        magicPaint.setColor(Color.parseColor(textGeometry.getColor()));
                    }
                    if (textGeometry.getTextAlign().equals("1")) {
                        magicPaint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(textGeometry.getText(), p1textGeometry.getX(), baseline, magicPaint);
                    } else if (textGeometry.getTextAlign().equals("2")) {
                        magicPaint.setTextAlign(Paint.Align.RIGHT);
                        canvas.drawText(textGeometry.getText(), textGeometry.getValue()[2], baseline, magicPaint);
                    }

                    break;
            }
            //重置画笔颜色
            magicPaint.setColor(Color.parseColor("#FFFFFFFF"));
            magicPaint.setStyle(Paint.Style.STROKE);
            canvas.restore();
        }

    }


    /**
     * 计算包裹这组多边形坐标的矩形的中心点
     *
     * @param polygonGeometryPointsValue
     */
    private Point playpolygonCenterPoint(ArrayList<Point> polygonGeometryPointsValue) {
        Point p = new Point();
        ArrayList xList = new ArrayList<Float>();
        ArrayList yList = new ArrayList<Float>();
        for (int i = 0; i < polygonGeometryPointsValue.size(); i++) {
            xList.add(polygonGeometryPointsValue.get(i).getX());
            yList.add(polygonGeometryPointsValue.get(i).getY());
        }
        float minx0 = (float) Collections.min(xList);
        float miny0 = (float) Collections.min(yList);
        float maxx0 = (float) Collections.max(xList);
        float maxy0 = (float) Collections.max(yList);
        p.setX((minx0 + maxx0) / 2);
        p.setY((miny0 + maxy0) / 2);
        return p;
    }

    /**
     * 计算矩形的中心点
     *
     * @param value
     * @return
     */
    private Point playRectCenterPoint(Float[] value) {
        Point p = new Point();
        p.setX((value[0] + value[2]) / 2);
        p.setY((value[1] + value[3]) / 2);
        return p;
    }


    private float[] String2Float(String[] strings) {
        float[] f = new float[strings.length];
        for (int i = 0; i < strings.length; i++) {
            f[i] = Float.parseFloat(strings[i]);
        }
        return f;
    }

    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private static final String TAG = CoreView.class.getSimpleName();
    //----------------------------------------------------------------以下为图像放缩

    float areaPersent = 10f;

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scaleFactor = detector.getScaleFactor();
        Log.e(TAG, "scaleFactor：" + scaleFactor);
        viewLastScal = viewLastScal * scaleFactor;
        Log.e(TAG, "viewLastScal：" + viewLastScal);
        if (viewLastScal > SCALE_MAX) {//超限，太大了
            viewLastScal = SCALE_MAX;
            Toast.makeText(context, "您已放大到最大级别", Toast.LENGTH_SHORT).show();
            return true;
        } else if (viewLastScal < SCALE_MIN) {//太小了
            viewLastScal = SCALE_MIN;
        }
        //计算放缩后的图元数据和图元点击区域
        doScaleValue();
        /*for (int i = 0; i < list.size(); i++) {
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
        }*/
        viewWidth = width * (viewLastScal / initScale);
        viewHeight = height * (viewLastScal / initScale);

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
        return true;
    }

    private void doScaleValue() {
        //放缩
        scale(0, viewLastScal, 0, 0, shapes);
        //计算图元的点击区域
        doClickZone();
    }

    /**
     * 计算图元的点击区域
     */
    private void doClickZone() {

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }


    //是否拖动标识
    private boolean isDrag = false;
    private float downX; //点击时的x坐标
    private float downY;  // 点击时的y坐标
    private long currentMS, currentMS1, currentMS2;
    private float pivotX, pivotY;
    private float viewWidth; //  View放缩后的宽度
    private float viewHeight; // View放缩后的高度

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
            mScaleGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    /**
     * 获取选中的模型Id
     */
    private int getModelId(float x, float y) {


        return -1;
    }
}
