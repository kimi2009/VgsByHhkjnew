package com.hhkj.vgsbyhhkjnew;

import android.os.Environment;

public class Constants {
    public static String FTP_HOST_IP1 = "172.16.19.127";
    //public static String FTP_HOST_IP = "172.16.19.106";
    //public static String FTP_HOST_IP = "172.16.18.68";
    public static String FTP_BASE_URL = "/dtx/";
    public static String FTP_USERNAME = "pad";
    public static String FTP_PSW = "123";

    public static String BASEFILEURL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/devicemanagementclient/data/";
    public static String STATIONCODE="ajdz/";
    public static String FTP_DB = "hhkj.db";
    public static String BASE_HTML = "file:///android_asset/echarts/";

    public static void sout(String msg){
        System.out.println("===="+msg);
    }

    public static final int REQUEST_SUCCESS = 1;
    public static final int REQUEST_ERROR = 0;
    public static final int NOT_NETWORK = -1;

    public static String BASEWBURL = "http://172.16.23.114:3080/";

    /**
     * 广播
     */
    public static final String UPDATE_BROADCAST = "com.cr.dhan.dailyreport.PROGRESS";
    public static final String UPDATE_SUCCESS = "com.cr.dhan.dailyreport.SUCCESS";
    /**
     * 本地路径
     */
    public static final String BASE_UPDATE_URL = "temp";
}
