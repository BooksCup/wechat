package com.bc.wechat.listener;

import android.view.View;

import com.bc.wechat.moments.bean.ExplorePostPinglunBean;

/**
 * 朋友圈监听器
 *
 * @author zhou
 */
public interface MomentsListener {
    //点击一个内容，回复评论
    void onPinlunEdit(View view, int friendid, String userid, String userName);

    /**
     * 进入用户详情页
     *
     * @param userId 用户ID
     */
    void toUserInfo(String userId);

    /**
     * 点击，弹出弹框，包含点赞，评论
     *
     * @param view     view
     * @param position 位置
     */
    void onClickLikeAndComment(View view, int position);

    //删除我发的朋友圈
    void deletePengyouquan(int id);

    //删除我的评论
    void deleteMypinglun(int ids, ExplorePostPinglunBean id);

    //图片被点击了，需要隐藏输入栏
    void imageOnclick();

    //视频呗点击了,缩略图网址与视频地址网址
    void videoOnclick(String img, String httpUrl);

}