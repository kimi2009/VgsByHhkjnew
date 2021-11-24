package com.hhkj.vgsbyhhkjnew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hhkj.vgsbyhhkjnew.test.Animals;
import com.hhkj.vgsbyhhkjnew.test.BaseBO;
import com.hhkj.vgsbyhhkjnew.test.BaseBoAdapter;
import com.hhkj.vgsbyhhkjnew.test.Person;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;


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
        //  tipDialog.show();
        Button jx = findViewById(R.id.jx);
        initPermission();
        jx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tipDialog.dismiss();
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
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BaseBO.class, new BaseBoAdapter())
                .create();
        ArrayList<BaseBO> kk = new ArrayList<BaseBO>();
        Person baseBO = new Person();
        baseBO.setName("张三");
        baseBO.setAge(5);
        baseBO.setSex(true);
        kk.add(baseBO);
        Animals baseBO1 = new Animals();
        baseBO1.setName("李四");
        baseBO1.setAge(8);
        baseBO1.setLb("狗");
        kk.add(baseBO1);
        String jsonString = gson.toJson(kk, new TypeToken<ArrayList<BaseBO>>() {
        }.getType());
        ArrayList<BaseBO> bb = gson.fromJson(jsonString, new TypeToken<ArrayList<BaseBO>>() {
        }.getType());
        for (int i = 0; i < bb.size(); i++) {
            System.out.println("-++-:" + bb.get(i).getName());
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