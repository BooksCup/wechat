package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;

/**
 * 设置备注和标签
 *
 * @author zhou
 */
public class SetRemarkAndTagActivity extends BaseActivity {

    private EditText mRemarkEt;

    // 添加电话
    private RelativeLayout mAddPhoneTmpRl;
    private RelativeLayout mAddPhoneRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_remark_and_tag);
        initView();
    }

    private void initView() {
        final String userId = getIntent().getStringExtra("userId");
        final String isFriend = getIntent().getStringExtra("isFriend");
        final String nickName = getIntent().getStringExtra("nickName");
        final String friendRemark = getIntent().getStringExtra("friendRemark");

        mRemarkEt = findViewById(R.id.et_remark);
        mAddPhoneTmpRl = findViewById(R.id.rl_add_phone_tmp);
        mAddPhoneRl = findViewById(R.id.rl_add_phone);

        if (TextUtils.isEmpty(friendRemark)) {
            // 无备注，展示昵称
            mRemarkEt.setText(nickName);
        } else {
            // 有备注，展示备注
            mRemarkEt.setText(friendRemark);
        }

        if (Constant.IS_NOT_FRIEND.equals(isFriend)) {
            // 非好友不能添加电话
            mAddPhoneTmpRl.setVisibility(View.GONE);
            mAddPhoneRl.setVisibility(View.GONE);
        }
    }

    public void back(View view) {
        finish();
    }
}
