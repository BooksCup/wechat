package com.bc.wechat.activity;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bc.wechat.R;

/**
 * 新增状态
 *
 * @author zhou
 */
public class AddStatusActivity extends BaseActivity {

    @Override
    public int getContentView() {
        return R.layout.activity_add_status;
    }

    @Override
    public void initView() {
        transparentStatusBar();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
    }

    /**
     * 透明化状态栏
     */
    private void transparentStatusBar() {
        Window window = getWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}