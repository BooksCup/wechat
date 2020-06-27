package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

import androidx.annotation.Nullable;


/**
 * 更多信息
 *
 * @author zhou
 */
public class MyMoreProfileActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTitleTv;

    private RelativeLayout mSexRl;
    private RelativeLayout mRegionRl;
    private RelativeLayout mSignRl;

    private TextView mSexTv;
    private TextView mSignTv;

    private User mUser;

    private static final int UPDATE_USER_SIGN = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_more_profile);
        initStatusBar();

        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mSexRl = findViewById(R.id.rl_sex);
        mSexTv = findViewById(R.id.tv_sex);

        mRegionRl = findViewById(R.id.rl_region);

        mSignRl = findViewById(R.id.rl_sign);
        mSignTv = findViewById(R.id.tv_sign);

        String userSex = mUser.getUserSex();

        if (Constant.USER_SEX_MALE.equals(userSex)) {
            mSexTv.setText(getString(R.string.sex_male));
        } else if (Constant.USER_SEX_FEMALE.equals(userSex)) {
            mSexTv.setText(getString(R.string.sex_female));
        }
        mSignTv.setText(mUser.getUserSign());

        mSexRl.setOnClickListener(this);
        mRegionRl.setOnClickListener(this);
        mSignRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_sex:
                startActivity(new Intent(this, SetGenderActivity.class));
                break;
            case R.id.rl_region:
                startActivity(new Intent(this, PickRegionActivity.class));
                break;
            case R.id.rl_sign:
                // 签名
                startActivityForResult(new Intent(this, EditSignActivity.class), UPDATE_USER_SIGN);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final User user = PreferencesUtil.getInstance().getUser();
            switch (requestCode) {
                case UPDATE_USER_SIGN:
                    // 个性签名
                    mSignTv.setText(user.getUserSign());
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = PreferencesUtil.getInstance().getUser();
        if (Constant.USER_SEX_MALE.equals(user.getUserSex())) {
            mSexTv.setText(getString(R.string.sex_male));
        } else if (Constant.USER_SEX_FEMALE.equals(user.getUserSex())) {
            mSexTv.setText(getString(R.string.sex_female));
        } else {
            mSexTv.setText("");
        }
    }
}
