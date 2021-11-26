package com.hhkj.vgsbyhhkjnew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hhkj.vgsbyhhkjnew.bean.BaseStar;
import com.hhkj.vgsbyhhkjnew.bean.BaseStarAdapter;
import com.hhkj.vgsbyhhkjnew.test.BaseBO;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

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
    private Context context = ViewActivity.this;
    QMUITipDialog tipDialog;
    private static final String TAG = ViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fileName = getIntent().getStringExtra("filename");
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在渲染,请稍候")
                .create();
        tipDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/devicemanagementclient/data/ajdz/vgs/transformation/" + fileName;
                String jsonString = readFile(path);
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(BaseStar.class, new BaseStarAdapter())
                        .create();
                ArrayList<Shape> shapes = gson.fromJson(jsonString, new TypeToken<ArrayList<Shape>>() {
                }.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(R.layout.view_activity);
                        CoreView coreView = findViewById(R.id.parentView);
                        //LinearLayout groupView = findViewById(R.id.groupView);
                        coreView.setData(shapes);
                        coreView.initData();
                        coreView.invalidate();
                        tipDialog.dismiss();

                    }
                });
            }
        }).start();

       /* GestureViewManager bind = GestureViewManager.bind(this, groupView, coreView);
        bind.setFullGroup(true);*/
    }

    public static String readFile(String filePath) {
        StringBuffer sb = new StringBuffer();
        try {
            readToBuffer(sb, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tipDialog.dismiss();
    }
}
