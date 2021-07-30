package com.bc.wechat.moments.listener;

import android.view.View;

import com.bc.wechat.moments.bean.ExplorePostPinglunBean;


public interface Explore_dongtai1_listener {
    //点击一个内容，回复评论
    void onPinlunEdit(View view, int friendid, String userid, String userName);

    //点击一个昵称和头像，，返回到个人资料里去，如果是自己就查看资料，
    void onClickUser(String userid);

    //点击，弹出弹框，包含点赞，评论
    void onClickEdit(View view, int position);

    //删除我发的朋友圈
    void deletePengyouquan(int id);

    //删除我的评论
    void deleteMypinglun(int ids, ExplorePostPinglunBean id);

    //图片被点击了，需要隐藏输入栏
    void imageOnclick();

    //视频呗点击了,缩略图网址与视频地址网址
    void videoOnclick(String img, String httpUrl);
}
