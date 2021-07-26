package com.bc.wechat.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 联系人权限
 *
 * @author zhou
 */
public class ContactPrivacyActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    // 聊天、朋友圈、微信运动等
    @BindView(R.id.iv_chats_moments_werun_etc)
    ImageView mChatsMomentsWerunEtcIv;

    // 仅聊天
    @BindView(R.id.iv_chats_only)
    ImageView mChatsOnlyIv;

    // 权限(不看他，不让他看我)
    @BindView(R.id.ll_privacy)
    LinearLayout mPrivacyLl;

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

    UserDao mUserDao;
    String mContactUserId;
    User mContact;

    VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;
    User mUser;

    String mPrivacy;
    String mHideMyPosts;
    String mHideHisPosts;

    @Override
    public int getContentView() {
        return R.layout.activity_contact_privacy;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        mUserDao = new UserDao();
        mContactUserId = getIntent().getStringExtra("contactId");
        mContact = mUserDao.getUserById(mContactUserId);

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(ContactPrivacyActivity.this);

        mPrivacy = mContact.getUserContactPrivacy();
        mHideMyPosts = mContact.getUserContactHideMyPosts();
        mHideHisPosts = mContact.getUserContactHideHisPosts();

        if (Constant.PRIVACY_CHATS_MOMENTS_WERUN_ETC.equals(mContact.getUserContactPrivacy())) {
            // 所有权限
            mChatsMomentsWerunEtcIv.setVisibility(View.VISIBLE);
            mChatsOnlyIv.setVisibility(View.GONE);

            mPrivacyLl.setVisibility(View.VISIBLE);
            if (Constant.HIDE_MY_POSTS.equals(mContact.getUserContactHideMyPosts())) {
                // 不让他看我
                mHideMyPostsIv.setVisibility(View.VISIBLE);
                mShowMyPostsIv.setVisibility(View.GONE);
            } else {
                // 让他看我
                mHideMyPostsIv.setVisibility(View.GONE);
                mShowMyPostsIv.setVisibility(View.VISIBLE);
            }

            if (Constant.HIDE_HIS_POSTS.equals(mContact.getUserContactHideHisPosts())) {
                // 不看他
                mHideHisPostsIv.setVisibility(View.VISIBLE);
                mShowHisPostsIv.setVisibility(View.GONE);
            } else {
                // 看他
                mHideHisPostsIv.setVisibility(View.GONE);
                mShowHisPostsIv.setVisibility(View.VISIBLE);
            }
        } else {
            // 仅聊天
            mChatsMomentsWerunEtcIv.setVisibility(View.GONE);
            mChatsOnlyIv.setVisibility(View.VISIBLE);

            mPrivacyLl.setVisibility(View.GONE);
        }
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_chats_moments_werun_etc, R.id.rl_chats_only,
            R.id.iv_hide_my_posts, R.id.iv_show_my_posts, R.id.iv_hide_his_posts, R.id.iv_show_his_posts})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_chats_moments_werun_etc:
                mPrivacy = Constant.PRIVACY_CHATS_MOMENTS_WERUN_ETC;

                mChatsMomentsWerunEtcIv.setVisibility(View.VISIBLE);
                mChatsOnlyIv.setVisibility(View.GONE);

                mPrivacyLl.setVisibility(View.VISIBLE);

                updateContactPrivacy(mUser.getUserId(), mContactUserId,
                        mPrivacy, mHideMyPosts, mHideHisPosts);
                break;
            case R.id.rl_chats_only:
                mPrivacy = Constant.PRIVACY_CHATS_ONLY;

                mChatsMomentsWerunEtcIv.setVisibility(View.GONE);
                mChatsOnlyIv.setVisibility(View.VISIBLE);

                mPrivacyLl.setVisibility(View.GONE);

                updateContactPrivacy(mUser.getUserId(), mContactUserId,
                        mPrivacy, mHideMyPosts, mHideHisPosts);
                break;

            case R.id.iv_hide_my_posts:
                mHideMyPosts = Constant.SHOW_MY_POSTS;

                mShowMyPostsIv.setVisibility(View.VISIBLE);
                mHideMyPostsIv.setVisibility(View.GONE);

                updateContactPrivacy(mUser.getUserId(), mContactUserId,
                        mPrivacy, mHideMyPosts, mHideHisPosts);
                break;
            case R.id.iv_show_my_posts:
                mHideMyPosts = Constant.HIDE_MY_POSTS;

                mShowMyPostsIv.setVisibility(View.GONE);
                mHideMyPostsIv.setVisibility(View.VISIBLE);

                updateContactPrivacy(mUser.getUserId(), mContactUserId,
                        mPrivacy, mHideMyPosts, mHideHisPosts);
                break;

            case R.id.iv_hide_his_posts:
                mHideHisPosts = Constant.SHOW_HIS_POSTS;

                mShowHisPostsIv.setVisibility(View.VISIBLE);
                mHideHisPostsIv.setVisibility(View.GONE);

                updateContactPrivacy(mUser.getUserId(), mContactUserId,
                        mPrivacy, mHideMyPosts, mHideHisPosts);
                break;
            case R.id.iv_show_his_posts:
                mHideHisPosts = Constant.HIDE_HIS_POSTS;

                mShowHisPostsIv.setVisibility(View.GONE);
                mHideHisPostsIv.setVisibility(View.VISIBLE);

                updateContactPrivacy(mUser.getUserId(), mContactUserId,
                        mPrivacy, mHideMyPosts, mHideHisPosts);
                break;
        }
    }

    /**
     * 设置朋友权限
     *
     * @param userId        用户ID
     * @param contactUserId 联系人用户ID
     * @param privacy       朋友权限
     * @param hideMyPosts   不让他看我
     * @param hideHisPosts  不看他
     */
    private void updateContactPrivacy(String userId, final String contactUserId,
                                      final String privacy, final String hideMyPosts, final String hideHisPosts) {
        String url = Constant.BASE_URL + "users/" + userId + "/contacts/" + contactUserId + "/privacy";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("privacy", privacy);
        paramMap.put("hideMyPosts", hideMyPosts);
        paramMap.put("hideHisPosts", hideHisPosts);

        mVolleyUtil.httpPutRequest(url, paramMap, s -> {
            mContact.setUserContactPrivacy(privacy);
            mContact.setUserContactHideMyPosts(hideMyPosts);
            mContact.setUserContactHideHisPosts(hideHisPosts);
            mUserDao.saveUser(mContact);
        }, volleyError -> {
        });
    }

}