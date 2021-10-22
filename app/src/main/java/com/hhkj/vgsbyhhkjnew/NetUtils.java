package com.hhkj.vgsbyhhkjnew;

import java.text.DecimalFormat;

/**
 * 网络操作工具类
 *
 * @author Administrator
 */
public class NetUtils {

    public static final int HIGH_FRONT = 1;// 高位在前,地位在后
    public static final int LOW_FRONT = 0;// 低位在前,高位在后

    // short转换为byte[2]数组  高位在前,地位在后
    public static byte[] short2Bytes(short s) {
        byte[] b = new byte[2];
        b[0] = (byte) ((s & 0xff00) >> 8);
        b[1] = (byte) (s & 0x00ff);
        return b;
    }

    public static short byte2short(byte[] b) {
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l <<= 8;
            l |= (b[i] & 0xff);
        }
        return l;
    }

    /**
     * 整数转4字节数组
     *
     * @param num  待转换的整数
     * @param sign 转换标志,1表示遵循高位在前,低位在后的转换规则;0反之
     * @return
     */
    public static byte[] int2Bytes(int num, int sign) {
        byte[] data = new byte[4];
        switch (sign) {
            case NetUtils.HIGH_FRONT:
                data[0] = (byte) ((num & 0xff000000) >> 24);
                data[1] = (byte) ((num & 0x00ff0000) >> 16);
                data[2] = (byte) ((num & 0x0000ff00) >> 8);
                data[3] = (byte) (num & 0x000000ff);
                break;
            case NetUtils.LOW_FRONT:
                data[0] = (byte) (num & 0xff);
                data[1] = (byte) ((num & 0xff00) >> 8);
                data[2] = (byte) ((num & 0xff0000) >> 16);
                data[3] = (byte) ((num & 0xff000000) >> 24);
                break;
        }
        return data;
    }


    /**
     * @param data 待转换的字节数组
     * @param sign 转换标志位
     * @return
     */
    public static int bytes2Int(byte[] data, int sign) {
        int num = 0;
        switch (sign) {
            case NetUtils.HIGH_FRONT:
                num = ((data[0] << 24) & 0xff000000);
                num |= ((data[1] << 16) & 0x00ff0000);
                num |= ((data[2] << 8) & 0x0000ff00);
                num |= ((data[3]) & 0x000000ff);
                break;
            case NetUtils.LOW_FRONT:
                num = data[0] & 0xff;
                num |= (data[1] << 8) & 0xff00;
                num |= (data[2] << 16) & 0xff0000;
                num |= (data[3] << 24) & 0xff000000;
                break;
        }
        return num & 0x0FFFFFFFF;
    }


    public static int bytes2IntA(byte[] data, int sign) {
        int num = 0;
        switch (sign) {
            case NetUtils.HIGH_FRONT:
                num = ((data[0] << 8) & 0x0000ff00);
                num |= ((data[1]) & 0x000000ff);
                break;
            case NetUtils.LOW_FRONT:
                num = data[0] & 0xff;
                num |= (data[1] << 8) & 0xff00;
                break;
        }
        return num & 0x0FFFF;
    }

    public static byte[] long2Bytes(long num, int sign) {
        byte[] data = new byte[8];
        switch (sign) {
            case HIGH_FRONT:
                data[0] = (byte) ((num >> 56) & 0xff);
                data[1] = (byte) ((num >> 48) & 0xff);
                data[2] = (byte) ((num >> 40) & 0xff);
                data[3] = (byte) ((num >> 32) & 0xff);
                data[4] = (byte) ((num >> 24) & 0xff);
                data[5] = (byte) ((num >> 16) & 0xff);
                data[6] = (byte) ((num >> 8) & 0xff);
                data[7] = (byte) ((num) & 0xff);
                break;
            case LOW_FRONT:
                data[0] = (byte) (num & 0xff);
                data[1] = (byte) ((num >> 8) & 0xff);
                data[2] = (byte) ((num >> 16) & 0xff);
                data[3] = (byte) ((num >> 24) & 0xff);
                data[4] = (byte) ((num >> 32) & 0xff);
                data[5] = (byte) ((num >> 40) & 0xff);
                data[6] = (byte) ((num >> 48) & 0xff);
                data[7] = (byte) ((num >> 56) & 0xff);
                break;
        }
        return data;
    }

    public static long bytes2Long(byte[] data, int sign) {
        long num = 0l;
        switch (sign) {
            case HIGH_FRONT:
                num = (((long) data[0] << 56) & 0xff00000000000000L);
                num |= (((long) data[1] << 48) & 0xff000000000000L);
                num |= (((long) data[2] << 40) & 0xff0000000000L);
                num |= (((long) data[3] << 32) & 0xff00000000L);
                num |= (((long) data[4] << 24) & 0xff000000L);
                num |= (((long) data[5] << 16) & 0xff0000L);
                num |= (((long) data[6] << 8) & 0xff00L);
                num |= (((long) data[7]) & 0xffL);
                break;
            case LOW_FRONT:
                num |= ((long) data[0] & 0xffL);
                num |= (((long) data[1] << 8) & 0xff00L);
                num |= (((long) data[2] << 16) & 0xff0000L);
                num |= (((long) data[3] << 24) & 0xff000000L);
                num |= (((long) data[4] << 32) & 0xff00000000L);
                num |= (((long) data[5] << 40) & 0xff0000000000L);
                num |= (((long) data[6] << 48) & 0xff000000000000L);
                num |= (((long) data[7] << 56) & 0xff00000000000000L);
                break;
        }
        return num;
    }

    public static double bytes2Double(byte[] b) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (b[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);

    }

    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    /*public static void main(String[] args) {
        short num = 160;
        System.out.print("整数" + num + "转换为字节数组,高位在前,低位在后:\t[");
        printfBytes(getByteArray(num));
        System.out.print("]\t");
        System.out.println(getShort(getByteArray(num),0));
    }*/
    public static void main(String[] args) {


        byte[] kk = {0x20, (byte) 0xBC, (byte) 0xBE, 0x4C};
        System.out.println("getFloat:" + getFloat(kk));
        System.out.println("getFloat:" + (float) (Math.round(NetUtils.getFloat(kk) * 100) / 100));
        int num = 500;


        System.out.print("整数" + num + "转换为字节数组,高位在前,低位在后:\t[");
        printfBytes(int2Bytes(num, NetUtils.HIGH_FRONT));
        System.out.print("]\t");
        System.out.println("反转换为整数: "
                + bytes2Int(int2Bytes(num, HIGH_FRONT), HIGH_FRONT));

        System.out.print("整数" + num + "转换为字节数组,低位在前,高位在后:\t[");
        printfBytes(int2Bytes(num, NetUtils.LOW_FRONT));
        System.out.print("]\t");
        System.out.println("反转为整数: "
                + bytes2Int(int2Bytes(num, LOW_FRONT), LOW_FRONT));


        long num2 = 1234567890987654321l;
        System.out.print("长整数" + num2 + "转换为字节数组,高位在前,低位在后:\t[");
        printfBytes(long2Bytes(num2, HIGH_FRONT));
        System.out.print("]\t");
        System.out.println("反转换为整数: "
                + bytes2Long(long2Bytes(num2, HIGH_FRONT), HIGH_FRONT));
        System.out.print("长整数" + num2 + "转换为字节数组,低位在前,高位在后:\t[");
        printfBytes(long2Bytes(num2, LOW_FRONT));
        System.out.print("]\t");
        System.out.println("反转换为整数: "
                + bytes2Long(long2Bytes(num2, LOW_FRONT), LOW_FRONT));
    }

    public static void printfBytes(byte[] data) {
        for (byte b : data) {
            System.out.print((int) b + " ");
        }
    }

    // 300
	/*public static void main(String[] args){
		byte[] bytes = new byte[2];
		bytes[0]=0x01;
		bytes[1]=0x2c;
		int byteStr = byte2IntA(bytes);
		System.out.println("num:"+byteStr);
		*//*int num = Integer.parseInt(byteStr+"",16);
		System.out.println("num:"+num);*//*
		System.out.println("num:"+(bytes[1]& 0xFF));
	}*/
    public static int byte2IntA(byte[] data) {
        int num = 0;
        num = ((data[0] << 8) & 0x0000ff00);
        num |= ((data[1]) & 0x000000ff);
        return num;
		/*char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[data.length * 2];

		for (int j = 0; j < data.length; j++) {
			int v = data[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		String result = new String(hexChars);
		result = result.replace(" ", "");
		return Integer.parseInt(result,16);*/
    }

    public static DecimalFormat df = new DecimalFormat("0.00");
    public static DecimalFormat df1 = new DecimalFormat("0.0");
    public static DecimalFormat df2 = new DecimalFormat("0.000");

    /*高字节在前，低字节在后
            是实际数值的100倍
    上微机处理时，小数点后保留2位有效数字*/
    public static String realValue(byte[] data) {
        return df.format(byte2IntA(data) / 100f);
    }

    /*高字节在前，低字节在后
            是实际数值的10倍
    上微机处理时，小数点后保留1位有效数字*/
    public static String realValue1(byte[] data) {
        return df1.format(byte2IntA(data) / 10f);
    }

    /*高字节在前，低字节在后
            是实际数值的1000倍
    上微机处理时，小数点后保留3位有效数字*/
    public static String realValue2(byte[] data) {
        return df2.format(byte2IntA(data) / 1000f);
    }
}
