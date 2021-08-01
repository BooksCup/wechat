package com.bc.wechat.viewholder.moments;

import android.view.View;
import android.widget.ImageView;

import com.bc.wechat.R;


public class VideotViewHolder extends BaseViewHolder {

    public ImageView videos;

    public VideotViewHolder(View itemView) {
        super(itemView);
        videos = (ImageView) itemView.findViewById(R.id.dongtai_videos);
    }

}