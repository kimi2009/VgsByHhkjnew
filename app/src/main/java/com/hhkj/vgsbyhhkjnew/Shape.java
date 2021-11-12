package com.hhkj.vgsbyhhkjnew;

import com.hhkj.vgsbyhhkjnew.bean.BaseStar;

import java.io.Serializable;
import java.util.ArrayList;

public class Shape implements Serializable {
    private String name;
    private int type;//0.组合 1.线段 2.多边形 3.矩形 4.椭圆 5.文字
    private String TypeId;
    ArrayList<Entity> sons = new ArrayList<Entity>();
    public String RotateAngle;
    public float angle;
    public Float x;
    public Float y;
    public String id;
    public Boolean isFill;
    public String LineColor;
    public String fillColor;
    public String ParentElementId;
    public BaseStar star;
    public ArrayList<Shape> shapes = new ArrayList<Shape>();
    Float[] area ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRotateAngle() {
        return RotateAngle;
    }

    public void setRotateAngle(String rotateAngle) {
        RotateAngle = rotateAngle;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public BaseStar getStar() {
        return star;
    }

    public void setStar(BaseStar star) {
        this.star = star;
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public void setShapes(ArrayList<Shape> shapes) {
        this.shapes = shapes;
    }

    public String getParentElementId() {
        return ParentElementId;
    }

    public void setParentElementId(String parentElementId) {
        ParentElementId = parentElementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLineColor() {
        return LineColor;
    }

    public void setLineColor(String lineColor) {
        LineColor = lineColor;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeId() {
        return TypeId;
    }

    public void setTypeId(String typeId) {
        TypeId = typeId;
    }

    public Boolean getFill() {
        return isFill;
    }

    public void setFill(Boolean fill) {
        isFill = fill;
    }


    public ArrayList<Entity> getSons() {
        return sons;
    }

    public void setSons(ArrayList<Entity> sons) {
        this.sons = sons;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float[] getArea() {
        return area;
    }

    public void setArea(Float[] area) {
        this.area = area;
    }
}
