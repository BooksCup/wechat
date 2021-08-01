package com.bc.wechat.viewholder.moments;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.moments.widget.CommentsView;
import com.bc.wechat.moments.widget.PraiseListView;
import com.bc.wechat.widget.ExpandTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 基础viewHolder
 *
 * @author zhou
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    // 发布人头像
    public SimpleDraweeView mAvatarSdv;
    // 发布人昵称
    public TextView mNickNameTv;
    // 文字内容
    public ExpandTextView mContentEtv;
    // 发布时间
    public TextView mTimeTv;
    // 删除
    public TextView mDeleteTv;
    public ImageView dianzanPinglun;//点赞评论按钮
    public LinearLayout linearLayoutAll;//点赞评论的背景
    public PraiseListView dianzanList;//点赞列表
    public CommentsView pinglunList;//评论列表
    public View dongtaiDriver;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mAvatarSdv = itemView.findViewById(R.id.sdv_avatar);
        mNickNameTv = itemView.findViewById(R.id.tv_nick_name);
        mContentEtv = itemView.findViewById(R.id.etv_content);
        mTimeTv = itemView.findViewById(R.id.tv_time);
        mDeleteTv = itemView.findViewById(R.id.tv_delete);
        dianzanPinglun = (ImageView) itemView.findViewById(R.id.dongtai_tv_plugs);
        dianzanList = (PraiseListView) itemView.findViewById(R.id.dongtai_rv_like);
        pinglunList = (CommentsView) itemView.findViewById(R.id.dongtai_rv_comment);
        linearLayoutAll = (LinearLayout) itemView.findViewById(R.id.dongtai_rv_all);
        dongtaiDriver = (View) itemView.findViewById(R.id.dongtai_driver);
    }

}