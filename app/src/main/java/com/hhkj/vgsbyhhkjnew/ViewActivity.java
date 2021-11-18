package com.hhkj.vgsbyhhkjnew;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew
 * @ClassName: ViewActivity
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/7/27 11:49
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class ViewActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);
        //ArrayList<Shape> shapes = (ArrayList<Shape>) getIntent().getSerializableExtra("data");

        ArrayList<Shape> shapes = FileInputList(Environment.getExternalStorageDirectory().getAbsolutePath() + "/devicemanagementclient/data/ajdz/vgs/source/" + Constants.fileName + ".txt");
        //LinearLayout groupView = findViewById(R.id.groupView);
        CoreView coreView = findViewById(R.id.parentView);
        coreView.setData(shapes);
        coreView.initData();
        coreView.invalidate();
       /* GestureViewManager bind = GestureViewManager.bind(this, groupView, coreView);
        bind.setFullGroup(true);*/
    }

    public String readFile(String filePath) {
        StringBuffer sb = new StringBuffer();
        try {
            readToBuffer(sb, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取本地文件中的list
     *
     * @param path
     * @return
     */

    @SuppressWarnings("resource")
    public static <Shape> ArrayList<com.hhkj.vgsbyhhkjnew.Shape> FileInputList(String path) {
        ArrayList<com.hhkj.vgsbyhhkjnew.Shape> list = null;
        try {
            FileInputStream inputStream = new FileInputStream(path);
            ObjectInputStream stream = new ObjectInputStream(inputStream);
            list = (ArrayList<com.hhkj.vgsbyhhkjnew.Shape>) stream.readObject();
            inputStream.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }
}
