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
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.FriendApplyDao;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通过朋友验证
 *
 * @author zhou
 */
public class NewFriendsAcceptConfirmActivity extends BaseActivity {

    // 好友备注
    @BindView(R.id.et_remark)
    EditText mRemarkEt;

    // 所有权限
    @BindView(R.id.rl_chats_moments_werun_etc)
    RelativeLayout mChatsMomentsWerunEtcRl;

    @BindView(R.id.iv_chats_moments_werun_etc)
    ImageView mChatsMomentsWerunEtcIv;

    // 仅聊天
    @BindView(R.id.rl_chats_only)
    RelativeLayout mChatsOnlyRl;

    @BindView(R.id.iv_chats_only)
    ImageView mChatsOnlyIv;

    // 权限(不看他，不让他看我)
    @BindView(R.id.rl_privacy_header)
    RelativeLayout mPrivacyHeaderRl;

    @BindView(R.id.rl_privacy)
    RelativeLayout mPrivacyRl;

    // 不让他看我
    @BindView(R.id.iv_hide_my_posts)
    ImageView mHideMyPostsIv;

    // 可以看我
    @BindView(R.id.iv_show_my_posts)
    ImageView mShowMyPostsIv;

    // 不看他
    @BindView(R.id.iv_hide_his_posts)
    ImageView mHideHisPostsIv;

    // 看他
    @BindView(R.id.iv_show_his_posts)
    ImageView mShowHisPostsIv;

    @BindView(R.id.tv_accept)
    TextView mAcceptTv;

    private UserDao mUserDao;
    private FriendApplyDao mFriendApplyDao;

    private VolleyUtil mVolleyUtil;
    private String mApplyId;
    private FriendApply mFriendApply;
    private LoadingDialog mDialog;

    private String mRelaPrivacy;
    private String mRelaHideMyPosts;
    private String mRelaHideHisPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_accept_confirm);

        ButterKnife.bind(this);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(NewFriendsAcceptConfirmActivity.this, R.color.status_bar_color_white);

        mUserDao = new UserDao();
        mFriendApplyDao = new FriendApplyDao();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(this);
        initView();
    }

    private void initView() {
        mRelaPrivacy = Constant.PRIVACY_CHATS_MOMENTS_WERUN_ETC;
        mRelaHideMyPosts = Constant.SHOW_MY_POSTS;
        mRelaHideHisPosts = Constant.SHOW_HIS_POSTS;

        mApplyId = getIntent().getStringExtra("applyId");
        mFriendApply = mFriendApplyDao.getFriendApplyByApplyId(mApplyId);
        mRemarkEt = findViewById(R.id.et_remark);

        mRemarkEt.setText(mFriendApply.getFromUserNickName());
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_chats_moments_werun_etc, R.id.rl_chats_only,
            R.id.iv_hide_my_posts, R.id.iv_show_my_posts, R.id.iv_hide_his_posts, R.id.iv_show_his_posts, R.id.tv_accept})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_chats_moments_werun_etc:
                mRelaPrivacy = Constant.PRIVACY_CHATS_MOMENTS_WERUN_ETC;

                mChatsMomentsWerunEtcIv.setVisibility(View.VISIBLE);
                mChatsOnlyIv.setVisibility(View.GONE);

                mPrivacyHeaderRl.setVisibility(View.VISIBLE);
                mPrivacyRl.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_chats_only:
                mRelaPrivacy = Constant.PRIVACY_CHATS_ONLY;

                mChatsMomentsWerunEtcIv.setVisibility(View.GONE);
                mChatsOnlyIv.setVisibility(View.VISIBLE);

                mPrivacyHeaderRl.setVisibility(View.GONE);
                mPrivacyRl.setVisibility(View.GONE);
                break;

            case R.id.iv_hide_my_posts:
                mRelaHideMyPosts = Constant.SHOW_MY_POSTS;

                mShowMyPostsIv.setVisibility(View.VISIBLE);
                mHideMyPostsIv.setVisibility(View.GONE);
                break;
            case R.id.iv_show_my_posts:
                mRelaHideMyPosts = Constant.HIDE_MY_POSTS;

                mShowMyPostsIv.setVisibility(View.GONE);
                mHideMyPostsIv.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_hide_his_posts:
                mRelaHideHisPosts = Constant.SHOW_HIS_POSTS;

                mShowHisPostsIv.setVisibility(View.VISIBLE);
                mHideHisPostsIv.setVisibility(View.GONE);
                break;
            case R.id.iv_show_his_posts:
                mRelaHideHisPosts = Constant.HIDE_HIS_POSTS;

                mShowHisPostsIv.setVisibility(View.GONE);
                mHideHisPostsIv.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_accept:
                mDialog.setMessage("正在处理...");
                mDialog.show();

                String relaRemark = mRemarkEt.getText().toString();
                acceptFriendApply(mApplyId, relaRemark, mRelaPrivacy, mRelaHideMyPosts, mRelaHideHisPosts);
                break;
            default:
                break;
        }
    }

    /**
     * 接受好友申请
     *
     * @param applyId          申请ID
     * @param relaRemark       好友备注
     * @param relaPrivacy      好友朋友权限 "0":聊天、朋友圈、微信运动  "1":仅聊天
     * @param relaHideMyPosts  朋友圈和视频动态 "0":可以看我 "1":不让他看我
     * @param relaHideHisPosts 朋友圈和视频动态 "0":可以看他 "1":不看他
     */
    private void acceptFriendApply(String applyId, String relaRemark,
                                   String relaPrivacy, String relaHideMyPosts, String relaHideHisPosts) {
        String url = Constant.BASE_URL + "friendApplies";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("applyId", applyId);
        paramMap.put("relaRemark", relaRemark);
        paramMap.put("relaPrivacy", relaPrivacy);
        paramMap.put("relaHideMyPosts", relaHideMyPosts);
        paramMap.put("relaHideHisPosts", relaHideHisPosts);

        mVolleyUtil.httpPutRequest(url, paramMap, response -> {
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
        }, volleyError -> {
            mDialog.dismiss();
            if (volleyError instanceof NetworkError) {
                Toast.makeText(NewFriendsAcceptConfirmActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }
}
