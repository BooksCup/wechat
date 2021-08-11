package com.bc.wechat.viewholder.moments;

import android.view.View;

import com.bc.wechat.R;
import com.bc.wechat.widget.NineGridView;

/**
 * 图片ViewHolder
 *
 * @author zhou
 */
public class ImageViewHolder extends BaseViewHolder {

    public NineGridView mPhotosGv;

    public ImageViewHolder(View itemView) {
        super(itemView);
        mPhotosGv = (NineGridView) itemView.findViewById(R.id.gv_photos);
    }

}