package com.hhkj.vgsbyhhkjnew.bean;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew.bean
 * @ClassName: TextGeometry
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/10/8 14:56
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class TextGeometry extends BaseStar {
    String text;
    String color;
    float textSize;
    float scalTextSize;
    String RectHeight;
    String RectWidth;
    String RectLeft;
    String RectTop;
    String TextAlign;//1 居中  2右对齐
    Float[] value = new Float[4];
    Float[] area;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getScalTextSize() {
        return scalTextSize;
    }

    public void setScalTextSize(float scalTextSize) {
        this.scalTextSize = scalTextSize;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

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

    public String getTextAlign() {
        return TextAlign;
    }

    public void setTextAlign(String textAlign) {
        TextAlign = textAlign;
    }

    public Float[] getArea() {
        return area;
    }

    public void setArea(Float[] area) {
        this.area = area;
    }
}
