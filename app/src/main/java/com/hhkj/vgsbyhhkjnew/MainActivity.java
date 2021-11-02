package com.hhkj.vgsbyhhkjnew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.hhkj.vgsbyhhkjnew.bean.EllipseGeometry;
import com.hhkj.vgsbyhhkjnew.bean.Line;
import com.hhkj.vgsbyhhkjnew.bean.PolygonGeometry;
import com.hhkj.vgsbyhhkjnew.bean.RectGeometry;
import com.hhkj.vgsbyhhkjnew.bean.TextGeometry;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    Utils utils;
    private Context context = MainActivity.this;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button jx = findViewById(R.id.jx);
        initPermission();
        jx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analysisData(readFile());
            }
        });
        Button drawbyself = findViewById(R.id.drawbyself);
        drawbyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ZoomTestActivity.class);
                startActivity(intent);
            }
        });
        Button test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TestActivity.class);
                startActivity(intent);
            }
        });
        utils = new Utils();
        gson = new Gson();
    }

    public byte[] readFile() {
        DataInputStream dis = null;
        try {
            File binaryFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/devicemanagementclient/data/ajdz/vgs/source/25HZ轨道1送1.hzt");//25HZ轨道1送1
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
        /*Set<Map.Entry<Integer, String>> set = KeyData.entrySet();
        Iterator<Map.Entry<Integer, String>> it = set.iterator();
        String yyy = "";
        while (it.hasNext()) {
            Map.Entry<Integer, String> entry = it.next();
            Integer key = entry.getKey();
            String value = entry.getValue();
            yyy += "{Key:" + key + ";value:" + value + "},";
        }
        System.out.println(yyy);*/

//blog.csdn.net/xiaohaizi15/article/details/106753736/
        /*for (Map.Entry<Integer, String> m :KeyData.entrySet())  {

            if (m.getValue().equals("Id")) {

                System.out.println("{{{:"+m.getKey());

            }}*/
        //解析
        Entity entity = new Entity();
        //entity = analysis(entity, bb);
        entity = analysis(entity, bb);
        String json = gson.toJson(entity);
        System.out.println("===");
        //Entity entityroot = new Entity();
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
        //整理树形结构
        /*ArrayList<Shape> shapesTree = new ArrayList<Shape>();
        ArrayList<Shape> sps=page.getShapes();
        sps.get(0);*/

       /* for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).getType() == 1) {
                System.out.println("====" + i);
            }
        }*/
        System.out.println("----");
        /*String avfindos = exchangetoJson(entityroot.getPage().getShapes(), new ArrayList<avfindo>());
        System.out.println("----");*/
        //skiaView skiaView= new skiaView(context,null);
     /*   processCadFile(
                context,
                avfindos,
                Constants.BASEFILEURL + Constants.STATIONCODE + "cad/transformation/" + "cache_测测测.avg"
        );
        Intent intent = new Intent(context, CadBridgeActivity.class);
        startActivity(intent);*/
        //ppp(shapes);
        Intent intent = new Intent(context, ViewActivity.class);
        intent.putExtra("data", shapes);
        startActivity(intent);
    }

    private void ppp(ArrayList<Shape> shapes) {
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).getType() == 1) {
                System.out.println("====:" + i + ";" + shapes.get(i).id);
            } else if (shapes.get(i).getType() == 0) {
                ppp(shapes.get(i).shapes);
            }
        }
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
                                            shape.setRotateAngle(otherParameters5.get("RotateAngle"));
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
                                                shape.setLineColor(Integer.toHexString(Integer.parseInt(otherParameters5.get("LineColor"))));
                                                String color=Integer.toHexString(Integer.parseInt(otherParameters5.get("FillColor")));
                                                shape.setFillColor("#" + color.substring(color.length() - 8, color.length()));
                                                shape.setFill(Boolean.parseBoolean(otherParameters5.get("IsFill")));
                                            }

                                        }
                                    }
                                    break;
                                case "GeometryComponent"://组合
                                    shape.setType(0);
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
                                            shape.setType(1);
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
                                            shape.setType(2);
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
                                            shape.setType(3);
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
                                            shape.setType(4);
                                        }
                                    }
                                    break;
                                case "TextGeometryComponent"://文字
                                    for (int i = 0; i < son5.getSons().size(); i++) {
                                        Entity son6 = son5.getSons().get(i);
                                        if (son6.getName().equals("Properties")) {
                                            TextGeometry textGeometry = new TextGeometry();
                                            /*if (!TextUtils.isEmpty(shape.getLineColor())) {
                                                String color = shape.getLineColor();
                                                textGeometry.setColor("#"+color.substring(color.length() - 8, color.length()));
                                            }*/
                                            textGeometry.setText(son6.getOtherParameters().get("Text"));

                                            String color = Integer.toHexString(Integer.parseInt(son6.getOtherParameters().get("TextFontColor")));
                                            textGeometry.setColor("#" + color.substring(color.length() - 8, color.length()));
                                            textGeometry.setTextSize(Float.parseFloat(son6.getOtherParameters().get("TextFontSize")));
                                            textGeometry.setTextAlign(son6.getOtherParameters().get("TextAlign"));
                                            textGeometry.setRectHeight(son6.getOtherParameters().get("RectHeight"));
                                            textGeometry.setRectWidth(son6.getOtherParameters().get("RectWidth"));
                                            textGeometry.setRectLeft(son6.getOtherParameters().get("RectLeft"));
                                            textGeometry.setRectTop(son6.getOtherParameters().get("RectTop"));
                                            shape.setStar(textGeometry);
                                            shape.setType(5);
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
                    //shape.setProperties(son4.getOtherParameters());
                    //shape.setProperties(son4.getOtherParameters());
                    shape.setId(son4.getOtherParameters().get("Id"));
                    shape.setParentElementId(son4.getOtherParameters().get("ParentElementId"));
                }
            }
        }
        shapes.add(shape);
    }

    private String hex10To16(int lineColor) {
        return String.format("%08X", lineColor);
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
                    /*if (KeyData.get(KeyIndex).equals("Shapes")) {
                        System.out.println("=============");
                    }*/
                    rs = analysis(rs, bb);
                    entity.getSons().add(rs);
                    break;
                case 2:
                    int valueIndex = getIntValue(bb.get(), bb);
                    /*System.out.println("====KeyIndex:"+KeyIndex+";Key:"+KeyData.get(KeyIndex)+";valueIndex:"+valueIndex+";value:"+KeyData.get(valueIndex));
                    if(KeyData.get(KeyIndex)=="Id"){
                        System.out.println("====Id:"+KeyData.get(valueIndex)+";valueIndex:"+valueIndex);
                    }*/
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


    public String format(String aa) {
        if (aa.length() == 1) {
            return "0" + aa;
        } else {
            return aa;
        }
    }

    //25HZ轨道1送1.hzt
    public void aa() {
        try {
            FileTransitionUtil.binaryToText(Environment.getExternalStorageDirectory().getAbsolutePath() + "/devicemanagementclient/data/ajdz/vgs/source/25HZ轨道1送1.hzt", Environment.getExternalStorageDirectory().getAbsolutePath() + "/devicemanagementclient/data/ajdz/vgs/txt/25HZ轨道1送1.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPermission() {
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
            //是否登录
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }
}