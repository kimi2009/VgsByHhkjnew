package com.hhkj.vgsbyhhkjnew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hhkj.SvgAdapter;
import com.hhkj.svgInfo;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew
 * @ClassName: VgsListActivity
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/11/23 10:38
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class VgsListActivity extends Activity {
    Context context = VgsListActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vgslist_activity);
        initView();
        initData();
    }

    QMUITipDialog tipDialog;
    ArrayList<svgInfo> mlist;
    SvgAdapter adapter;

    private void initView() {
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在转换数据,请稍候")
                .create();
        RecyclerView vgs_list = findViewById(R.id.vgs_list);
        vgs_list.setLayoutManager(new LinearLayoutManager(this));
        mlist = new ArrayList<svgInfo>();
        adapter = new SvgAdapter(mlist);
        vgs_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra("filename", mlist.get(position).getName());
                startActivity(intent);
            }
        });
    }


    private void initData() {
        translate();
    }

    private void translate() {
        SvgUtils svgUtils = new SvgUtils(VgsListActivity.this);

        File file = new File(getSourcePath());
        File[] files = file.listFiles();
        if (files.length > 0) {
            tipDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //遍历文件夹
                    for (File file1 : files) {
                        System.out.println("====正在解析：" + file1.getName());
                        String a = file1.getName();
                        svgUtils.processSvgFile(a.substring(0, a.length() - 4), getSourcePath(), getCachePath());
                        //删除源文件
                        file1.delete();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                            //刷新页面
                            refreshPage();
                        }
                    });
                }
            }).start();

        } else {
            //刷新页面
            refreshPage();
        }

    }

    private void refreshPage() {
        File file = new File(getCachePath());
        File[] files = file.listFiles();
        for (File file1 : files) {
            svgInfo svgInfo = new svgInfo();
            svgInfo.setName(file1.getName());
            String a = file1.getName();
            svgInfo.setShowName(a.substring(0, a.length() - 4));
            mlist.add(svgInfo);
        }
        adapter.notifyDataSetChanged();
    }

    private String getSourcePath() {
        String path = Constants.BASEFILEURL + Constants.STATIONCODE + "vgs/source/";
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        return path;
    }

    private String getCachePath() {
        String path = Constants.BASEFILEURL + Constants.STATIONCODE + "vgs/transformation/";
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        return path;
    }


}
