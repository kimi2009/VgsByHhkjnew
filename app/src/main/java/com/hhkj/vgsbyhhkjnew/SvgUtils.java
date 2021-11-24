package com.hhkj.vgsbyhhkjnew;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hhkj.vgsbyhhkjnew.bean.BaseStar;
import com.hhkj.vgsbyhhkjnew.bean.BaseStarAdapter;
import com.hhkj.vgsbyhhkjnew.bean.EllipseGeometry;
import com.hhkj.vgsbyhhkjnew.bean.Line;
import com.hhkj.vgsbyhhkjnew.bean.PolygonGeometry;
import com.hhkj.vgsbyhhkjnew.bean.RectGeometry;
import com.hhkj.vgsbyhhkjnew.bean.TextGeometry;
import com.hhkj.vgsbyhhkjnew.test.BaseBO;
import com.hhkj.vgsbyhhkjnew.test.BaseBoAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew
 * @ClassName: SvgUtils
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/11/23 15:00
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class SvgUtils {
    private Context context;
    private Utils utils;
    private Gson gson;
    private String cachePath;
    private String fileName;

    public SvgUtils(Context context) {
        this.context = context;
        utils = new Utils();
         gson = new GsonBuilder()
                .registerTypeAdapter(BaseStar.class, new BaseStarAdapter())
                .create();
        //gson=new Gson();
    }

    public void processSvgFile(String fileName, String sourcePath, String cachePath) {
        this.fileName = fileName;
        this.cachePath = cachePath;
        analysisData(readFile(sourcePath));
    }

    public byte[] readFile(String sourcePath) {
        DataInputStream dis = null;
        try {
            File binaryFile = new File(sourcePath + fileName + ".hzt");//25HZ轨道1送1
            dis = new DataInputStream(new FileInputStream(binaryFile));
            byte[] b = new byte[(int) binaryFile.length()];
            dis.read(b);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    HashMap<Integer, String> KeyData;

    public void analysisData(byte[] data) {

        //读取2进制文件
     /* 序号	内容	   字节	           内容说明
          1 	Magic	    8	      值为0x2017112808264500
          2 	Key Count	4	      键名数量(包括类型为string的值)
          3	    Key Data	n	      键名数据包(包括类型为string的值)，见2.2键名数据包格式
          4	    Value Count	1或3或5	  值的数量，占用字节长度见1数量长度规则
          5	    Value Data	n	      值数据包，见2.3值数据包格式
*/
        ByteBuffer bb = ByteBuffer.wrap(data);
        byte[] k0 = utils.analysisByte(bb, 8);
        String magic = "";
        for (int i = k0.length - 1; i >= 0; i--) {
            magic += format(Integer.toHexString(k0[i] & 0xFF));
        }
        byte[] b = utils.analysisByte(bb, 4);
        int KeyCount = NetUtils.bytes2Int(b, NetUtils.LOW_FRONT);
        KeyData = new HashMap<Integer, String>();
        for (int i = 0; i < KeyCount; i++) {
            byte[] ferrari = utils.analysisByteWith00End(bb);
            try {
                String s = new String(ferrari, "UTF-16LE");
                KeyData.put(i, s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //解析
        Entity entity = new Entity();
        entity = analysis(entity, bb);
        String json = gson.toJson(entity);
        System.out.println("===");
        //转换数据格式
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        for (int i = 0; i < entity.getSons().size(); i++) {
            Entity son1 = entity.getSons().get(i);
            if (son1.getName() != null) {
                if (son1.getName().equals("Pages")) {
                    Entity rb = son1.getSons().get(0);
                    for (int j = 0; j < rb.getSons().size(); j++) {
                        Entity son2 = rb.getSons().get(j);
                        if (son2.getName().equals("Shapes")) {
                            for (int n = 0; n < son2.getSons().size(); n++) {
                                aaa(shapes, son2.getSons().get(n));
                            }
                        }
                    }
                }
            }
        }

        System.out.println("----");
        String path = cachePath + fileName + ".txt";
        String json1 = gson.toJson(shapes,new TypeToken<ArrayList<Shape>>() {
        }.getType());
        writeString2File(json1, path);
        //FileWriteList1(path, shapes);

    }

    private Entity analysis(Entity entity, ByteBuffer bb) {
        byte bora = bb.get();
        int valueCount = getIntValue(bora, bb);
        HashMap<String, String> otherParameters = entity.getOtherParameters();
        for (int i = 0; i < valueCount; i++) {
            int ValueType = bb.get();   //值类型， 1:子记录 2:字符串 3:整形 4:布尔型 5:字符型 6:浮点型 7:二进制
            int KeyIndex = getIntValue(bb.get(), bb);
            switch (ValueType) {
                case 1:
                    Entity rs = new Entity();
                    rs.setName(KeyData.get(KeyIndex));

                    rs = analysis(rs, bb);
                    entity.getSons().add(rs);
                    break;
                case 2:
                    int valueIndex = getIntValue(bb.get(), bb);

                    otherParameters.put(KeyData.get(KeyIndex), KeyData.get(valueIndex));
                    break;
                case 3:
                    int value = getIntValue(bb.get(), bb);
                    otherParameters.put(KeyData.get(KeyIndex), value + "");
                    break;
                case 4:
                    int flag = getIntValue(bb.get(), bb);
                    if (flag == 0) {
                        otherParameters.put(KeyData.get(KeyIndex), "false");
                    } else if (flag == 1) {
                        otherParameters.put(KeyData.get(KeyIndex), "true");
                    }
                    break;
                case 5:
                    String s = Byte.toString(bb.get());
                    otherParameters.put(KeyData.get(KeyIndex), s);
                    break;
                case 6:
                    float f = NetUtils.getFloat(utils.analysisByte(bb, 4));
                    otherParameters.put(KeyData.get(KeyIndex), f + "");
                    break;
                case 7://二进制类型暂时未用到，暂时不处理
                    int length = getIntValue(bb.get(), bb);
                    byte[] bs = utils.analysisByte(bb, length);
                    break;
            }
        }
        return entity;
    }

    private void aaa(ArrayList<Shape> shapes, Entity entity) {
        Shape shape = new Shape();
        if (entity.getOtherParameters().get("TypeId").equals("ShapeObject")) {

            for (int k = 0; k < entity.getSons().size(); k++) {
                Entity son4 = entity.getSons().get(k);
                if (son4.getName().equals("SubObjects")) {
                    for (int t = 0; t < son4.getSons().size(); t++) {
                        Entity son5 = son4.getSons().get(t);
                        HashMap<String, String> hm = son5.getOtherParameters();
                        /*
                         *  TransformComponent
                         *  ShapeStyleComponent
                         *  FlashComponent
                         *  PolylineGeometryComponent
                         *
                         *  TransformComponent
                         *  ShapeStyleComponent
                         *  FlashComponent
                         *  PolygonGeometryComponent
                         *
                         *  GeometryComponent
                         *  EllipseGeometryComponent
                         *  RectGeometryComponent
                         *
                         * */
                        if (hm != null && hm.get("TypeId") != null) {
                            switch (hm.get("TypeId")) {
                                case "TransformComponent"://基本位置
                                    for (int m = 0; m < son5.getSons().size(); m++) {
                                        Entity son6 = son5.getSons().get(m);
                                        if (son6.getName().equals("Properties")) {
                                            HashMap<String, String> otherParameters5 = son6.getOtherParameters();
                                            shape.setX(Float.parseFloat(otherParameters5.get("X")));
                                            shape.setY(Float.parseFloat(otherParameters5.get("Y")));
                                            shape.setRotateAngle(Float.parseFloat(otherParameters5.get("RotateAngle")));
                                        }
                                    }
                                    break;
                                case "ShapeStyleComponent":
                                    for (int m = 0; m < son5.getSons().size(); m++) {
                                        Entity son6 = son5.getSons().get(m);
                                        if (son6.getName().equals("Properties")) {
                                            HashMap<String, String> otherParameters5 = son6.getOtherParameters();
                                            //将数字转为16进制的色值
                                            if (!otherParameters5.get("LineColor").equals("-1")) {
                                                String color0 = Integer.toHexString(Integer.parseInt(otherParameters5.get("LineColor")));
                                                shape.setLineColor("#" + color0.substring(color0.length() - 8, color0.length()));
                                                shape.setDisFrame(Boolean.parseBoolean(otherParameters5.get("IsDisFrame")));
                                            }
                                            if (!otherParameters5.get("FillColor").equals("-1")) {
                                                String color = Integer.toHexString(Integer.parseInt(otherParameters5.get("FillColor")));
                                                if (color.length() >= 8) {
                                                    shape.setFillColor("#" + color.substring(color.length() - 8, color.length()));
                                                    shape.setFill(Boolean.parseBoolean(otherParameters5.get("IsFill")));
                                                }
                                            }
                                            shape.setLineDashStyle(Integer.parseInt(otherParameters5.get("LineDashStyle")));
                                            shape.setFillBrushType(Integer.parseInt(otherParameters5.get("FillBrushType")));
                                        }
                                    }
                                    break;
                                case "GeometryComponent"://组合
                                    shape.setTypes(0);
                                    break;
                                case "PolylineGeometryComponent"://线段
                                    for (int i = 0; i < son5.getSons().size(); i++) {
                                        Entity son6 = son5.getSons().get(i);
                                        if (son6.getName().equals("Properties")) {
                                            Line line = new Line();
                                            line.setPoints(son6.getOtherParameters().get("Points"));
                                            if (!TextUtils.isEmpty(shape.getLineColor())) {
                                                String color = shape.getLineColor();
                                                line.setColor("#" + color.substring(color.length() - 8, color.length()));
                                            }
                                            shape.setStar(line);
                                            shape.setTypes(1);
                                        }
                                    }

                                    break;
                                case "PolygonGeometryComponent"://多边形
                                    for (int i = 0; i < son5.getSons().size(); i++) {
                                        Entity son6 = son5.getSons().get(i);
                                        if (son6.getName().equals("Properties")) {
                                            PolygonGeometry polygonGeometry = new PolygonGeometry();
                                            polygonGeometry.setPoints(son6.getOtherParameters().get("Points"));
                                            if (!TextUtils.isEmpty(shape.getLineColor())) {
                                                String color = shape.getLineColor();
                                                polygonGeometry.setColor("#" + color.substring(color.length() - 8, color.length()));
                                            }
                                            shape.setStar(polygonGeometry);
                                            shape.setTypes(2);
                                        }
                                    }
                                    break;
                                case "RectGeometryComponent"://矩形
                                    for (int i = 0; i < son5.getSons().size(); i++) {
                                        Entity son6 = son5.getSons().get(i);
                                        if (son6.getName().equals("Properties")) {
                                            RectGeometry rectGeometry = new RectGeometry();
                                            if (!TextUtils.isEmpty(shape.getLineColor())) {
                                                String color = shape.getLineColor();
                                                rectGeometry.setColor("#" + color.substring(color.length() - 8, color.length()));
                                            }
                                            rectGeometry.setRectHeight(son6.getOtherParameters().get("RectHeight"));
                                            rectGeometry.setRectWidth(son6.getOtherParameters().get("RectWidth"));
                                            rectGeometry.setRectLeft(son6.getOtherParameters().get("RectLeft"));
                                            rectGeometry.setRectTop(son6.getOtherParameters().get("RectTop"));
                                            shape.setStar(rectGeometry);
                                            shape.setTypes(3);
                                        }
                                    }
                                    break;
                                case "EllipseGeometryComponent"://椭圆
                                    for (int i = 0; i < son5.getSons().size(); i++) {
                                        Entity son6 = son5.getSons().get(i);
                                        if (son6.getName().equals("Properties")) {
                                            EllipseGeometry ellipseGeometry = new EllipseGeometry();
                                            if (!TextUtils.isEmpty(shape.getLineColor())) {
                                                String color = shape.getLineColor();
                                                ellipseGeometry.setColor("#" + color.substring(color.length() - 8, color.length()));
                                            }
                                            ellipseGeometry.setRectHeight(son6.getOtherParameters().get("RectHeight"));
                                            ellipseGeometry.setRectWidth(son6.getOtherParameters().get("RectWidth"));
                                            ellipseGeometry.setRectLeft(son6.getOtherParameters().get("RectLeft"));
                                            ellipseGeometry.setRectTop(son6.getOtherParameters().get("RectTop"));
                                            shape.setStar(ellipseGeometry);
                                            shape.setTypes(4);
                                        }
                                    }
                                    break;
                                case "TextGeometryComponent"://文字
                                    for (int i = 0; i < son5.getSons().size(); i++) {
                                        Entity son6 = son5.getSons().get(i);
                                        if (son6.getName().equals("Properties")) {
                                            TextGeometry textGeometry = new TextGeometry();

                                            textGeometry.setText(son6.getOtherParameters().get("Text"));

                                            String color = Integer.toHexString(Integer.parseInt(son6.getOtherParameters().get("TextFontColor")));
                                            textGeometry.setColor("#" + color.substring(color.length() - 8, color.length()));
                                            textGeometry.setTextSize(Float.parseFloat(son6.getOtherParameters().get("TextFontSize")));
                                            textGeometry.setTextAlign(son6.getOtherParameters().get("TextAlign"));
                                            textGeometry.setWordWrapping(Integer.parseInt(son6.getOtherParameters().get("WordWrapping")));
                                            textGeometry.setRectHeight(son6.getOtherParameters().get("RectHeight"));
                                            textGeometry.setRectWidth(son6.getOtherParameters().get("RectWidth"));
                                            textGeometry.setRectLeft(son6.getOtherParameters().get("RectLeft"));
                                            textGeometry.setRectTop(son6.getOtherParameters().get("RectTop"));
                                            shape.setStar(textGeometry);
                                            shape.setTypes(5);
                                        }
                                    }
                                    break;
                                case "ArcGeometryComponent"://弧线
                                    break;
                                case "ShapeObject":
                                    //递归
                                    aaa(shape.shapes, son5);
                                    break;
                            }
                        }
                    }
                } else if (son4.getName().equals("Properties")) {
                    shape.setId(son4.getOtherParameters().get("Id"));
                    shape.setParentElementId(son4.getOtherParameters().get("ParentElementId"));
                }
            }
        }
        shapes.add(shape);
    }

    public File writeString2File(String Data, String filePath) {
        BufferedReader bufferedReader = null;

        BufferedWriter bufferedWriter = null;

        File distFile = null;

        try {
            distFile = new File(filePath);

            if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs();

            bufferedReader = new BufferedReader(new StringReader(Data));

            bufferedWriter = new BufferedWriter(new FileWriter(distFile));

            char buf[] = new char[1024]; //字符缓冲区

            int len;

            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);

            }

            bufferedWriter.flush();

            bufferedReader.close();

            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return distFile;
    }

    /**
     * 存储list到文件
     *
     * @param path
     * @param list
     */
    @SuppressWarnings("resource")
    public static <T> void FileWriteList10(String path, List<T> list) {
        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);
            stream.writeObject(list);
            stream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String format(String aa) {
        if (aa.length() == 1) {
            return "0" + aa;
        } else {
            return aa;
        }
    }

    public int getIntValue(byte bora, ByteBuffer bb) {
        if (bora == -1) {
            return NetUtils.bytes2Int(utils.analysisByte(bb, 4), NetUtils.LOW_FRONT);
        } else if (bora == -2) {
            return NetUtils.bytes2IntA(utils.analysisByte(bb, 2), NetUtils.LOW_FRONT);
        } else {
            return getUnsignedByte(bora);
        }
    }

    public int getUnsignedByte(byte data) {      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data & 0x0FF; // 部分编译器会把最高位当做符号位，因此写成0x0FF.
    }
}
