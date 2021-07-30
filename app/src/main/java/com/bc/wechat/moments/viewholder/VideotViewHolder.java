package com.bc.wechat.moments.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.bc.wechat.R;


public class VideotViewHolder extends ComViewHolder {

    public ImageView videos;

    public VideotViewHolder(View itemView) {
        super(itemView);
        videos = (ImageView) itemView.findViewById(R.id.dongtai_videos);
    }
}
