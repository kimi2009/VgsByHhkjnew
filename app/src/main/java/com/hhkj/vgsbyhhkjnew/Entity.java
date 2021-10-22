package com.hhkj.vgsbyhhkjnew;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @ProjectName: VgsByHhkj
 * @Package: com.hhkj.vgsbyhhkj.jx
 * @ClassName: Entity
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/2/23 14:31
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class Entity {
    private ArrayList<Entity> sons = new ArrayList<Entity>();
    private HashMap<String, String> otherParameters = new HashMap<String, String>();
    private String name;

    private HashMap<String, String> Properties = new HashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getOtherParameters() {
        return otherParameters;
    }

    public void setOtherParameters(HashMap<String, String> otherParameters) {
        this.otherParameters = otherParameters;
    }

    public ArrayList<Entity> getSons() {
        return sons;
    }

    public void setSons(ArrayList<Entity> sons) {
        this.sons = sons;
    }

    public HashMap<String, String> getProperties() {
        return Properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        Properties = properties;
    }

}
