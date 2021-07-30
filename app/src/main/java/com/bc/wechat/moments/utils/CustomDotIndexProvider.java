package com.bc.wechat.moments.utils;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;


import com.bc.wechat.R;

import java.util.List;

import byc.imagewatcher.ImageWatcher;


public class CustomDotIndexProvider implements ImageWatcher.IndexProvider {
    private boolean initLayout;
    private IndicatorView indicatorView;

    @Override
    public View initialView(Context context) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        indicatorView = new IndicatorView(context);
        indicatorView.setLayoutParams(lp);

        DisplayMetrics d = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(d);
        int size = (int) (50 * d.density + 0.5);
        lp.setMargins(0, 0, 0, size);

        initLayout = false;
        return indicatorView;
    }

    @Override
    public void onPageChanged(ImageWatcher imageWatcher, int position, List<Uri> dataList) {
        if (!initLayout) {
            initLayout = true;
            indicatorView.reset(dataList.size(), position, R.drawable.b_gray_dcdcdc_oval, R.drawable.b_yellow_ffb100_oval);
        } else {
            indicatorView.select(position, R.drawable.b_gray_dcdcdc_oval, R.drawable.b_yellow_ffb100_oval);
        }
    }
}
