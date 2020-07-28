package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * 修改性别
 *
 * @author zhou
 */
public class SetGenderActivity extends BaseActivity implements View.OnClickListener {
    TextView mTitleTv;

    RelativeLayout mMaleRl;
    RelativeLayout mFemaleRl;

    TextView mSaveTv;

    ImageView mMaleIv;
    ImageView mFemaleIv;

    User mUser;
    LoadingDialog mDialog;
    VolleyUtil mVolleyUtil;
    String mSex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gender);
        initStatusBar();

        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(SetGenderActivity.this);
        mVolleyUtil = VolleyUtil.getInstance(this);
        initView();
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mMaleRl = findViewById(R.id.rl_male);
        mFemaleRl = findViewById(R.id.rl_female);

        mSaveTv = findViewById(R.id.tv_save);

        mMaleIv = findViewById(R.id.iv_male);
        mFemaleIv = findViewById(R.id.iv_female);

        mMaleRl.setOnClickListener(this);
        mFemaleRl.setOnClickListener(this);
        mSaveTv.setOnClickListener(this);

        renderSex();
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_male:
                mMaleIv.setVisibility(View.VISIBLE);
                mFemaleIv.setVisibility(View.GONE);
                mSex = Constant.USER_SEX_MALE;
                break;
            case R.id.rl_female:
                mMaleIv.setVisibility(View.GONE);
                mFemaleIv.setVisibility(View.VISIBLE);
                mSex = Constant.USER_SEX_FEMALE;
                break;
            case R.id.tv_save:
                updateUserSex(mUser.getUserId(), mSex);
                break;
        }
    }

    private void renderSex() {
        if (Constant.USER_SEX_MALE.equals(mUser.getUserSex())) {
            // 男
            mMaleIv.setVisibility(View.VISIBLE);
            mFemaleIv.setVisibility(View.GONE);
        } else if (Constant.USER_SEX_FEMALE.equals(mUser.getUserSex())) {
            // 女
            mMaleIv.setVisibility(View.GONE);
            mFemaleIv.setVisibility(View.VISIBLE);
        } else {
            // 鲲
            mMaleIv.setVisibility(View.GONE);
            mFemaleIv.setVisibility(View.GONE);
        }
    }

    /**
     * 修改用户性别
     *
     * @param userId  用户ID
     * @param userSex 用户性别
     */
    private void updateUserSex(String userId, final String userSex) {
        mDialog.setMessage(getString(R.string.saving));
        mDialog.show();
        String url = Constant.BASE_URL + "users/" + userId + "/userSex";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userSex", userSex);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mDialog.dismiss();
                mUser.setUserSex(userSex);
                PreferencesUtil.getInstance().setUser(mUser);
                renderSex();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(SetGenderActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(SetGenderActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
