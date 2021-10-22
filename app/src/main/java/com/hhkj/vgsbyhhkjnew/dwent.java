package com.hhkj.vgsbyhhkjnew;


import android.graphics.Path;
import android.graphics.RectF;


import java.util.ArrayList;
import java.util.List;

public class dwent {
    int etype;
    String info;
    long handle;
    int color;
    boolean skip;
    boolean hasinfosit;
    boolean hasmodifydisrect;
    boolean selectf;

    dwent(long hd, int et, String inf, int c, ArrayList<avfindo> ai) {
        handle = hd;
        etype = et;
        info = inf;
        blk_hastrans = false;
        color = c;

        if(ai != null) {
            ents = new ArrayList<dwent>();
            for (avfindo av : ai) {
                ents.add(new dwent(av.handle, av.et, av.infos, av.color, av.ents));
            }
        }
        skip = false;
        hasinfosit = false;
        hasmodifydisrect = false;
        selectf = false;
        textinfo = "";
        blk_name = "";

        attr_name = "";
        attr_val = "";
        CounterClockWise = true;
    }

    List<String> infos;
    List<dwent> ents;
    double x;
    double y;

    Double[] disrect = {Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};

    double line_sec_x;
    double line_sec_y;

    double circle_r;
    double ellipls_ratio;

    String textinfo;
    double text_height;
    int text_halign;
    int text_valign;

    int mtext_attachpoint;

    ArrayList<Double> lwpolyline_pts;
    float[] lwpolyline_fpts;

    boolean blk_hastrans;
    double blk_xscale;
    double blk_yscale;
    double blk_angle;
    String blk_name;

    String attr_name;
    String attr_val;
    //    Hashtable<String, dwent> atts;
    //double attr_rotation;     统一用blk_angle吧。
    double attr_widthfator;

    boolean bneeddraw = false;

    double[] arc_ps={0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    double arc_startangle;
    double arc_endangle;
    boolean CounterClockWise;
    boolean fromhatch;

    ArrayList<Path> hatchpaths;

    boolean runcachflag;
    RectF cachrectf;
    Float tsa;
    Float swa;
}
