package com.hhkj.vgsbyhhkjnew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

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
public class CoreView extends View {

    public CoreView(Context context) {
        this(context, null);
    }

    public CoreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();//准备工作
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        doScaleValue();
        drawVgsView(canvas, shapes);
    }

    float scalpercent = 0.8f;
    float textpercent = 1.3f;

    private void doScaleValue() {
        System.out.println("===doScaleValue");
        int width = getWidth();
        int height = getHeight();
        System.out.println("===width:" + width + ";height:" + height);
        float scalX = width / maxx;
        float scalY = height / maxy;
        float scal = scalX > scalY ? scalY : scalX;
        if (scal <= 1) {//图像过大，则进行缩小，图像过小，仅放大0.8倍
            scale(0, scal, 0, 0, shapes);
        } else {
            scale(0, scal * scalpercent, 0, 0, shapes);
        }
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
                    if(shape.getFill()){
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
                    if(shape.getFill()){
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
}
