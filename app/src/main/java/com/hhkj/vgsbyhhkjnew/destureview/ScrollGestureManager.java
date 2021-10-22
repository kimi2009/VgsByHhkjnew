package com.hhkj.vgsbyhhkjnew.destureview;

import android.content.Context;
import android.view.GestureDetector;

/**
 * Created by 
 * Description :滑动手势的管理类
 */

class ScrollGestureManager extends GestureDetector {

    ScrollGestureManager(Context context, ScrollGestureListener scrollGestureListener) {
        super(context, scrollGestureListener);
    }

}