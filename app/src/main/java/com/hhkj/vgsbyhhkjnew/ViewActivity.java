package com.hhkj.vgsbyhhkjnew;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);
        String fileName = getIntent().getStringExtra("filename");
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在渲染,请稍候")
                .create();
        tipDialog.show();
        CoreView coreView = findViewById(R.id.parentView);
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        ArrayList<Shape> shapes = FileInputList(Environment.getExternalStorageDirectory().getAbsolutePath() + "/devicemanagementclient/data/ajdz/vgs/transformation/" + fileName);
        //LinearLayout groupView = findViewById(R.id.groupView);
        coreView.setData(shapes);
        coreView.initData();
        coreView.invalidate();
        tipDialog.dismiss();
       /* GestureViewManager bind = GestureViewManager.bind(this, groupView, coreView);
        bind.setFullGroup(true);*/
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

}
