package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 更多信息
 *
 * @author zhou
 */
public class MyMoreProfileActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.rl_sex)
    RelativeLayout mSexRl;

    @BindView(R.id.rl_region)
    RelativeLayout mRegionRl;

    @BindView(R.id.rl_sign)
    RelativeLayout mSignRl;

    @BindView(R.id.tv_sex)
    TextView mSexTv;

    @BindView(R.id.tv_region)
    TextView mRegionTv;

    @BindView(R.id.tv_sign)
    TextView mSignTv;

    User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_more_profile);
        ButterKnife.bind(this);

        initStatusBar();

        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        setTitleStrokeWidth(mTitleTv);

        String userSex = mUser.getUserSex();

        if (Constant.USER_SEX_MALE.equals(userSex)) {
            mSexTv.setText(getString(R.string.sex_male));
        } else if (Constant.USER_SEX_FEMALE.equals(userSex)) {
            mSexTv.setText(getString(R.string.sex_female));
        }
        mRegionTv.setText(mUser.getUserRegion());
        mSignTv.setText(mUser.getUserSign());
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_sex, R.id.rl_region, R.id.rl_sign})
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
                startActivity(new Intent(this, EditSignActivity.class));
                break;
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
        mRegionTv.setText(user.getUserRegion());

        // 个性签名
        mSignTv.setText(user.getUserSign());
    }

}