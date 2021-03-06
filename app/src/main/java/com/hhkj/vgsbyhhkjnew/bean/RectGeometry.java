package com.hhkj.vgsbyhhkjnew.bean;

import java.util.ArrayList;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew.bean
 * @ClassName: Rect
 * @Description:矩形
 * @Author: D.Han
 * @CreateDate: 2021/9/29 16:56
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class RectGeometry extends BaseStar {
    String RectHeight;
    String RectWidth;
    String RectLeft;
    String RectTop;
    Float[] value = new Float[4];
    public ArrayList<Float[]> area;

    public String getRectHeight() {
        return RectHeight;
    }

    public void setRectHeight(String rectHeight) {
        RectHeight = rectHeight;
    }

    public String getRectWidth() {
        return RectWidth;
    }

    public void setRectWidth(String rectWidth) {
        RectWidth = rectWidth;
    }

    public String getRectLeft() {
        return RectLeft;
    }

    public void setRectLeft(String rectLeft) {
        RectLeft = rectLeft;
    }

    public String getRectTop() {
        return RectTop;
    }

    public void setRectTop(String rectTop) {
        RectTop = rectTop;
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
