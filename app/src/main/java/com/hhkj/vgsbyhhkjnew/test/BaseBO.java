package com.hhkj.vgsbyhhkjnew.test;

import java.io.Serializable;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew.test
 * @ClassName: BaseBO
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/11/24 14:10
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public abstract class BaseBO implements Serializable {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "BaseBO{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
