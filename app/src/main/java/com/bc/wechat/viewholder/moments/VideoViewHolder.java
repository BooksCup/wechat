package com.bc.wechat.viewholder.moments;

import android.view.View;
import android.widget.ImageView;

import com.bc.wechat.R;

/**
 * 视频ViewHolder
 *
 * @author zhou
 */
public class VideoViewHolder extends BaseViewHolder {

    public ImageView mVideoThumbnailIv;

    public VideoViewHolder(View itemView) {
        super(itemView);
        mVideoThumbnailIv = (ImageView) itemView.findViewById(R.id.iv_video_thumbnail);
    }

}