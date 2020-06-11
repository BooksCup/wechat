package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.bc.wechat.R;
import com.bc.wechat.utils.StatusBarUtil;

/**
 * 开始绑定QQ号
 *
 * @author zhou
 */
public class QqIdLinkBeginActivity extends BaseActivity {

    private Button mLinkQqBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_qq_id_begin);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(QqIdLinkBeginActivity.this, R.color.bottom_text_color_normal);

        mLinkQqBtn = findViewById(R.id.btn_link_qq);
        mLinkQqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QqIdLinkBeginActivity.this, QqIdLinkActivity.class));
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
