package com.bc.wechat.fragment;

import android.graphics.Paint;
import android.text.TextPaint;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    /**
     * 渲染标题粗细程度
     *
     * @param textView 标题textView
     */
    protected void setTitleStrokeWidth(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // 控制字体加粗的程度
        paint.setStrokeWidth(0.8f);
    }

}
