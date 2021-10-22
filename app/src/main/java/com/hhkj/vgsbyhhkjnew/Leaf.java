package com.hhkj.vgsbyhhkjnew;

import java.util.ArrayList;

/**
 * @ProjectName: VgsByHhkj
 * @Package: com.hhkj.vgsbyhhkj.jx
 * @ClassName: Leaf
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/6/24 15:58
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
class Leaf {
    public int type;//1线条、2圆...
    public String x;
    public String y;
    public String id;
    public String parentId;
    public int rotate;//旋转角度
    public int radius;//半径
    public int startAngle;//起始角度
    public int endAngle;//终到角度
    public ArrayList<Point> points=new ArrayList<Point>();

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(int endAngle) {
        this.endAngle = endAngle;
    }
}
