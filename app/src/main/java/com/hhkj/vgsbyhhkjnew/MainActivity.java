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
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

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


public class MainActivity extends AppCompatActivity {
    QMUITipDialog tipDialog;
    private Context context = MainActivity.this;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            tipDialog = new QMUITipDialog.Builder(context)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("正在渲染,请稍候")
                    .create();
            tipDialog.show();
        Button jx = findViewById(R.id.jx);
        initPermission();
        jx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
                Intent intent = new Intent(context, VgsListActivity.class);
                startActivity(intent);
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