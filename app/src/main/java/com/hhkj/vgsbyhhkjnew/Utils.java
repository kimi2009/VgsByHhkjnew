package com.hhkj.vgsbyhhkjnew;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Utils {
    //命令代码
    public static int getMainCommand(byte[] data) {
        return data[5] & 0xFF;
    }

    //信息的数据长度
    public static int getValueLength(byte[] data) {
        byte[] destArray = new byte[2];
        System.arraycopy(data, 6, destArray, 0, 2);
        return NetUtils.byte2short(destArray);
    }

    //信息
    public static byte[] getValueInfo(byte[] data) {
        byte[] destArray = new byte[data.length - 14];
        System.arraycopy(data, 8, destArray, 0, data.length - 14);
        return destArray;
    }

    public static byte[] getCRCInfo(byte[] data) {
        byte[] destArray = new byte[2];
        System.arraycopy(data, data.length - 6, destArray, 0, 2);
        return destArray;
    }

    public static int byte2Int(byte data) {
        return data & 0xFF;
    }

    public static byte[] analysisByte(ByteBuffer bb, int num) {
        byte[] myArray = new byte[num];
        for (int i = 0; i < num; i++) {
            myArray[i] = bb.get();
        }
        return myArray;
    }

    public byte[] analysisByteWith00End(ByteBuffer bb) {
        ArrayList<Byte> gg = new ArrayList<Byte>();
        //byte[] myArray = new byte[num];
        int len = bb.limit() - bb.position();
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                byte temp = bb.get();
                if (i % 2 != 0) {
                    if (temp == 0) {
                        if (gg.get(gg.size() - 1) == 0) {
                            gg.add(temp);
                            return toPrimitives(gg.toArray(new Byte[gg.size()]));
                        } else {
                            gg.add(temp);
                        }
                    } else {
                        gg.add(temp);
                    }
                } else {
                    gg.add(temp);
                }
            } else {
                gg.add(bb.get());
            }
        }

        return toPrimitives(gg.toArray(new Byte[gg.size()]));
    }

    public byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length-2 ];//除去尾部两个00 00
        for (int i = 0; i < oBytes.length-2 ; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

    public final String hexStringOf(byte[] bs) {
        if (bs == null) {
            return "NULL";
        }

        char[] cs = new char[]{
                "0".charAt(0),
                "1".charAt(0),
                "2".charAt(0),
                "3".charAt(0),
                "4".charAt(0),
                "5".charAt(0),
                "6".charAt(0),
                "7".charAt(0),
                "8".charAt(0),
                "9".charAt(0),
                "A".charAt(0),
                "B".charAt(0),
                "C".charAt(0),
                "D".charAt(0),
                "E".charAt(0),
                "F".charAt(0)};
        char space = " ".charAt(0);
        StringBuffer sb = new StringBuffer(bs.length * 4);
        sb.append(space);
        for (int i = 0; bs != null && i < bs.length; i++) {
            byte b = bs[i];
            int bh = (b >> 4) & 0x0f;
            int bl = b & 0x0f;

            sb.append(cs[bh]);
            sb.append(cs[bl]);
            sb.append(space);
        }
        return sb.toString().trim();
    }

    public static void pinWrite(Context context, String path, Boolean isPowerOn) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write((isPowerOn ? "1" : "0").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static String getFileContent(String path) {
        File file = new File(path);
        if(!file.exists()){
            return null;
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);

        int length = inputStream.available();
        byte bytes[] = new byte[length];
        inputStream.read(bytes);
        inputStream.close();
        String str =new String(bytes, StandardCharsets.UTF_8);
        return str ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
