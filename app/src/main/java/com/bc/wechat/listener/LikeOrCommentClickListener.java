package com.bc.wechat.listener;

/**
 * 朋友圈点赞评论监听器
 *
 * @author zhou
 */
public interface LikeOrCommentClickListener {

    /**
     * 点赞
     *
     * @param position 位置
     */
    void onLikeClick(int position);

    /**
     * 评论
     *
     * @param position
     */
    void onCommentClick(int position);

    void onClickFrendCircleTopBg();

    void onDeleteItem(String id, int position);

}