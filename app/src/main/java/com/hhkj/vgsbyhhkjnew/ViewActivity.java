package com.hhkj.vgsbyhhkjnew;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.hhkj.vgsbyhhkjnew.destureview.GestureViewManager;

import java.util.ArrayList;

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
        ArrayList<Shape> shapes= (ArrayList<Shape>) getIntent().getSerializableExtra("data");
        LinearLayout groupView = findViewById(R.id.groupView);
        CoreView coreView = findViewById(R.id.parentView);
        coreView.setData(shapes);
        coreView.initData();
        coreView.invalidate();
        GestureViewManager bind = GestureViewManager.bind(this, groupView, coreView);
        bind.setFullGroup(true);
    }
}
