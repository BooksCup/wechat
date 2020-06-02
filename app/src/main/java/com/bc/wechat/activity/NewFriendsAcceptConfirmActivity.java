package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.bc.wechat.dao.FriendApplyDao;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * 申请添加朋友
 *
 * @author zhou
 */
public class NewFriendsAcceptConfirmActivity extends BaseActivity implements View.OnClickListener {
    // 好友备注
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

    private UserDao mUserDao;
    private FriendApplyDao mFriendApplyDao;
    private TextView mAcceptTv;


    private VolleyUtil mVolleyUtil;
    private String mApplyId;
    private FriendApply mFriendApply;
    private LoadingDialog mDialog;

    private String mRelaAuth;
    private String mRelaNotSeeMe;
    private String mRelaNotSeeHim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_accept_confirm);
        initStatusBar();

        mUserDao = new UserDao();
        mFriendApplyDao = new FriendApplyDao();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(this);
        initView();
    }

    private void initView() {
        mRelaAuth = Constant.RELA_AUTH_ALL;
        mRelaNotSeeMe = Constant.RELA_CAN_SEE_ME;
        mRelaNotSeeHim = Constant.RELA_CAN_SEE_HIM;

        mApplyId = getIntent().getStringExtra("applyId");
        mFriendApply = mFriendApplyDao.getFriendApplyByApplyId(mApplyId);
        mRemarkEt = findViewById(R.id.et_remark);

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

        mAcceptTv = findViewById(R.id.tv_accept);

        mAuthAllRl.setOnClickListener(this);
        mAuthOnlyChatRl.setOnClickListener(this);

        mForbidSeeMeIv.setOnClickListener(this);
        mAllowSeeMeIv.setOnClickListener(this);

        mForbidSeeHimIv.setOnClickListener(this);
        mAllowSeeHimIv.setOnClickListener(this);

        mAcceptTv.setOnClickListener(this);

        mRemarkEt.setText(mFriendApply.getFromUserNickName());
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

            case R.id.tv_accept:
                mDialog.setMessage("正在处理...");
                mDialog.show();

                String relaRemark = mRemarkEt.getText().toString();
                acceptFriendApply(mApplyId, relaRemark, mRelaAuth, mRelaNotSeeMe, mRelaNotSeeHim);
                break;
            default:
                break;
        }
    }

    /**
     * 接受好友申请
     *
     * @param applyId       申请ID
     * @param relaRemark    好友备注
     * @param relaAuth      好友朋友权限 "0":聊天、朋友圈、微信运动  "1":仅聊天
     * @param relaNotSeeMe  朋友圈和视频动态 "0":可以看我 "1":不让他看我
     * @param relaNotSeeHim 朋友圈和视频动态 "0":可以看他 "1":不看他
     */
    private void acceptFriendApply(String applyId, String relaRemark,
                                   String relaAuth, String relaNotSeeMe, String relaNotSeeHim) {
        String url = Constant.BASE_URL + "friendApplies";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("applyId", applyId);
        paramMap.put("relaRemark", relaRemark);
        paramMap.put("relaAuth", relaAuth);
        paramMap.put("relaNotSeeMe", relaNotSeeMe);
        paramMap.put("relaNotSeeHim", relaNotSeeHim);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
                mFriendApply.setStatus(Constant.FRIEND_APPLY_STATUS_ACCEPT);
                FriendApply.save(mFriendApply);

                User user = new User();
                user.setUserId(mFriendApply.getFromUserId());
                user.setUserNickName(mFriendApply.getFromUserNickName());
                user.setUserAvatar(mFriendApply.getFromUserAvatar());
                user.setUserHeader(CommonUtil.setUserHeader(mFriendApply.getFromUserNickName()));
                user.setUserSex(mFriendApply.getFromUserSex());
                user.setIsFriend(Constant.IS_FRIEND);
                mUserDao.saveUser(user);

                Toast.makeText(NewFriendsAcceptConfirmActivity.this, "已发送", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(NewFriendsAcceptConfirmActivity.this, UserInfoActivity.class);
                intent.putExtra("userId", user.getUserId());
                startActivity(intent);

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(NewFriendsAcceptConfirmActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
