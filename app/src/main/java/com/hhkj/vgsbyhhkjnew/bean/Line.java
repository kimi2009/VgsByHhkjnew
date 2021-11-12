package com.hhkj.vgsbyhhkjnew.bean;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew.bean
 * @ClassName: Line
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/9/29 16:06
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class Line extends BaseStar {
    String points;
    ArrayList<Point> pointsValue = new ArrayList<Point>();
    ArrayList<Float> linepoints = new ArrayList<Float>();
    private ArrayList<float[]> area;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public ArrayList<Point> getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(ArrayList<Point> pointsValue) {
        this.pointsValue = pointsValue;
    }

    public ArrayList<Float> getLinepoints() {
        return linepoints;
    }

    public void setLinepoints(ArrayList<Float> linepoints) {
        this.linepoints = linepoints;
    }

    public ArrayList<float[]> getArea() {
        return area;
    }

    public void setArea(ArrayList<float[]> area) {
        this.area = area;
    }
}
