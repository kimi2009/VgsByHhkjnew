package com.hhkj.vgsbyhhkjnew.bean;

import java.util.ArrayList;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew.bean
 * @ClassName: PolygonGeometry
 * @Description: 多边形
 * @Author: D.Han
 * @CreateDate: 2021/9/29 16:52
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class PolygonGeometry extends BaseStar {
    String points;
    ArrayList<Point> PolygonGeometryPointsValue = new ArrayList<Point>();
    ArrayList<Float> polygonGeometryLinepoints = new ArrayList<Float>();
    float[] area;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public ArrayList<Point> getPolygonGeometryPointsValue() {
        return PolygonGeometryPointsValue;
    }

    public void setPolygonGeometryPointsValue(ArrayList<Point> polygonGeometryPointsValue) {
        PolygonGeometryPointsValue = polygonGeometryPointsValue;
    }

    public ArrayList<Float> getPolygonGeometryLinepoints() {
        return polygonGeometryLinepoints;
    }

    public void setPolygonGeometryLinepoints(ArrayList<Float> polygonGeometryLinepoints) {
        this.polygonGeometryLinepoints = polygonGeometryLinepoints;
    }

    public float[] getArea() {
        return area;
    }

    public void setArea(float[] area) {
        this.area = area;
    }
}
