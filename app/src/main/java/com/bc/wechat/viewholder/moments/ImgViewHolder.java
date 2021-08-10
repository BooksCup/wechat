package com.bc.wechat.viewholder.moments;

import android.view.View;

import com.bc.wechat.R;
import com.bc.wechat.moments.widget.NineGridView;

/**
 * 九宫格图片
 *
 * @author zhou
 */
public class ImgViewHolder extends BaseViewHolder {

    public NineGridView mPhotosGv;

    public ImgViewHolder(View itemView) {
        super(itemView);
        mPhotosGv = (NineGridView) itemView.findViewById(R.id.gv_photos);
    }

}