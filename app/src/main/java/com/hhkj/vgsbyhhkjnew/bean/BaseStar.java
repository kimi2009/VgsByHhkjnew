package com.hhkj.vgsbyhhkjnew.bean;

import java.io.Serializable;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew.bean
 * @ClassName: Star
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/9/29 16:04
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public abstract class BaseStar implements Serializable {

    String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
