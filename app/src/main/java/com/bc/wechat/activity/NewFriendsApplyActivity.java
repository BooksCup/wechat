package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

/**
 * 申请添加朋友
 *
 * @author zhou
 */
public class NewFriendsApplyActivity extends BaseActivity implements View.OnClickListener {

    // 申请信息
    private EditText mApplyRemarkEt;
    // 备注
    private EditText mRemarkEt;

    // 所有权限
    private RelativeLayout mAuthAllRl;
    private ImageView mAuthAllIv;

    // 仅聊天
    private RelativeLayout mAuthOnlyChatRl;
    private ImageView mAuthOnlyChatIv;

    // 权限(不看他，不让他看我)
    private RelativeLayout mRoleTempRl;
    private RelativeLayout mRoleRl;

    // 不让他看我
    private ImageView mForbidSeeMeIv;
    // 可以看我
    private ImageView mAllowSeeMeIv;

    // 不看他
    private ImageView mForbidSeeHimIv;
    // 看他
    private ImageView mAllowSeeHimIv;


    private User mUser;
    private String mFriendId;
    private String mFriendNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_apply);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        mFriendId = getIntent().getStringExtra("friendId");
        mFriendNickName = getIntent().getStringExtra("friendNickName");

        mApplyRemarkEt = findViewById(R.id.et_apply_remark);
        mApplyRemarkEt.setText("我是" + mUser.getUserNickName());

        mRemarkEt = findViewById(R.id.et_remark);
        mRemarkEt.setText(mFriendNickName);

        mAuthAllRl = findViewById(R.id.rl_auth_all);
        mAuthAllIv = findViewById(R.id.iv_auth_all);

        mAuthOnlyChatRl = findViewById(R.id.rl_auth_only_chat);
        mAuthOnlyChatIv = findViewById(R.id.iv_auth_only_chat);

        mRoleTempRl = findViewById(R.id.rl_role_temp);
        mRoleRl = findViewById(R.id.rl_role);

        mForbidSeeMeIv = findViewById(R.id.iv_switch_forbid_see_me);
        mAllowSeeMeIv = findViewById(R.id.iv_switch_allow_see_me);

        mForbidSeeHimIv = findViewById(R.id.iv_switch_forbid_see_him);
        mAllowSeeHimIv = findViewById(R.id.iv_switch_allow_see_him);

        mAuthAllRl.setOnClickListener(this);
        mAuthOnlyChatRl.setOnClickListener(this);

        mForbidSeeMeIv.setOnClickListener(this);
        mAllowSeeMeIv.setOnClickListener(this);

        mForbidSeeHimIv.setOnClickListener(this);
        mAllowSeeHimIv.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_auth_all:
                mAuthAllIv.setVisibility(View.VISIBLE);
                mAuthOnlyChatIv.setVisibility(View.GONE);

                mRoleTempRl.setVisibility(View.VISIBLE);
                mRoleRl.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_auth_only_chat:
                mAuthAllIv.setVisibility(View.GONE);
                mAuthOnlyChatIv.setVisibility(View.VISIBLE);

                mRoleTempRl.setVisibility(View.GONE);
                mRoleRl.setVisibility(View.GONE);
                break;

            case R.id.iv_switch_forbid_see_me:
                mAllowSeeMeIv.setVisibility(View.VISIBLE);
                mForbidSeeMeIv.setVisibility(View.GONE);
                break;
            case R.id.iv_switch_allow_see_me:
                mAllowSeeMeIv.setVisibility(View.GONE);
                mForbidSeeMeIv.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_switch_forbid_see_him:
                mAllowSeeHimIv.setVisibility(View.VISIBLE);
                mForbidSeeHimIv.setVisibility(View.GONE);
                break;
            case R.id.iv_switch_allow_see_him:
                mAllowSeeHimIv.setVisibility(View.GONE);
                mForbidSeeHimIv.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
