package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * 申请添加朋友
 *
 * @author zhou
 */
public class NewFriendsApplyConfirmActivity extends BaseActivity implements View.OnClickListener {

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

    private TextView mSendTv;

    private User mUser;
    private UserDao mUserDao;
    private User mFriend;
    private String mFriendId;

    private VolleyUtil mVolleyUtil;
    private LoadingDialog mDialog;

    private String mRelaAuth;
    private String mRelaNotSeeMe;
    private String mRelaNotSeeHim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_apply_confirm);
        initStatusBar();

        mUser = PreferencesUtil.getInstance().getUser();
        mUserDao = new UserDao();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(this);
        initView();
    }

    private void initView() {
        mRelaAuth = Constant.RELA_AUTH_ALL;
        mRelaNotSeeMe = Constant.RELA_CAN_SEE_ME;
        mRelaNotSeeHim = Constant.RELA_CAN_SEE_HIM;

        mFriendId = getIntent().getStringExtra("friendId");

        mFriend = mUserDao.getUserById(mFriendId);

        mSendTv = findViewById(R.id.tv_send);

        mApplyRemarkEt = findViewById(R.id.et_apply_remark);
        mApplyRemarkEt.setText("我是" + mUser.getUserNickName());

        mRemarkEt = findViewById(R.id.et_remark);
        if (TextUtils.isEmpty(mFriend.getUserContactAlias())) {
            mRemarkEt.setText(mFriend.getUserNickName());
        } else {
            mRemarkEt.setText(mFriend.getUserContactAlias());
        }

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

        mSendTv.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_auth_all:
                mRelaAuth = Constant.RELA_AUTH_ALL;

                mAuthAllIv.setVisibility(View.VISIBLE);
                mAuthOnlyChatIv.setVisibility(View.GONE);

                mRoleTempRl.setVisibility(View.VISIBLE);
                mRoleRl.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_auth_only_chat:
                mRelaAuth = Constant.RELA_AUTH_ONLY_CHAT;

                mAuthAllIv.setVisibility(View.GONE);
                mAuthOnlyChatIv.setVisibility(View.VISIBLE);

                mRoleTempRl.setVisibility(View.GONE);
                mRoleRl.setVisibility(View.GONE);
                break;

            case R.id.iv_switch_forbid_see_me:
                mRelaNotSeeMe = Constant.RELA_CAN_SEE_ME;

                mAllowSeeMeIv.setVisibility(View.VISIBLE);
                mForbidSeeMeIv.setVisibility(View.GONE);
                break;
            case R.id.iv_switch_allow_see_me:
                mRelaNotSeeMe = Constant.RELA_NOT_SEE_ME;

                mAllowSeeMeIv.setVisibility(View.GONE);
                mForbidSeeMeIv.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_switch_forbid_see_him:
                mRelaNotSeeHim = Constant.RELA_CAN_SEE_HIM;

                mAllowSeeHimIv.setVisibility(View.VISIBLE);
                mForbidSeeHimIv.setVisibility(View.GONE);
                break;
            case R.id.iv_switch_allow_see_him:
                mRelaNotSeeHim = Constant.RELA_NOT_SEE_HIM;

                mAllowSeeHimIv.setVisibility(View.GONE);
                mForbidSeeHimIv.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_send:
                mDialog.setMessage("正在发送...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                String applyRemark = mApplyRemarkEt.getText().toString();
                String relaRemark = mRemarkEt.getText().toString();
                addFriendApply(applyRemark, mUser.getUserId(), mFriendId, relaRemark, mRelaAuth, mRelaNotSeeMe, mRelaNotSeeHim);
                break;
            default:
                break;
        }
    }

    private void addFriendApply(String applyRemark, String fromUserId, String toUserId, String relaRemark,
                                String relaAuth, String relaNotSeeMe, String relaNotSeeHim) {
        String url = Constant.BASE_URL + "friendApplies";

        if (relaRemark.equals(mFriend.getUserNickName())) {
            relaRemark = "";
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("applyRemark", applyRemark);
        paramMap.put("fromUserId", fromUserId);
        paramMap.put("toUserId", toUserId);
        paramMap.put("relaRemark", relaRemark);
        paramMap.put("relaAuth", relaAuth);
        paramMap.put("relaNotSeeMe", relaNotSeeMe);
        paramMap.put("relaNotSeeHim", relaNotSeeHim);

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
                Toast.makeText(NewFriendsApplyConfirmActivity.this, "已发送", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(NewFriendsApplyConfirmActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
