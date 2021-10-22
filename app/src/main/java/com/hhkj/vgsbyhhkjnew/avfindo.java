package com.hhkj.vgsbyhhkjnew;


import java.util.ArrayList;

public class avfindo {
   public long handle;
    public int et;
    public String infos="aaaaaa";
    public int color;
    public int rotate;//旋转角度
    public  ArrayList<avfindo> ents = new ArrayList<avfindo>();

    public long getHandle() {
        return handle;
    }

    public void setHandle(long handle) {
        this.handle = handle;
    }

    public int getEt() {
        return et;
    }

    public void setEt(int et) {
        this.et = et;
    }

    public String getInfos() {
        return infos;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<avfindo> getEnts() {
        return ents;
    }

    public void setEnts(ArrayList<avfindo> ents) {
        this.ents = ents;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }
}
