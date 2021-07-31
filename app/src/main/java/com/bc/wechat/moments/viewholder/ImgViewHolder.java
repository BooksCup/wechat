package com.bc.wechat.moments.viewholder;

import android.view.View;

import com.bc.wechat.R;
import com.bc.wechat.moments.widget.NineGridView;


public class ImgViewHolder extends BaseViewHolder {
    public NineGridView nineGridView;

    public ImgViewHolder(View itemView) {
        super(itemView);
        nineGridView = (NineGridView) itemView.findViewById(R.id.dongtai_layout_nine);
    }
}
