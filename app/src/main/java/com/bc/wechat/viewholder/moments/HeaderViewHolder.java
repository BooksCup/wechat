package com.bc.wechat.viewholder.moments;

import android.view.View;
import android.widget.TextView;

import com.bc.wechat.R;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 头ViewHolder
 *
 * @author zhou
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {

    // 头像
    public SimpleDraweeView mAvatarSdv;
    // 昵称
    public TextView mNickNameTv;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        mAvatarSdv = itemView.findViewById(R.id.sdv_avatar);
        mNickNameTv = itemView.findViewById(R.id.tv_nick_name);
    }

}