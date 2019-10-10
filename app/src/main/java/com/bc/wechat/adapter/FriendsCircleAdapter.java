package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.utils.TimeUtil;
import com.bc.wechat.utils.TimestampUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FriendsCircleAdapter extends BaseAdapter {
    private List<FriendsCircle> dataList;
    private Context mContext;

    public FriendsCircleAdapter(List<FriendsCircle> dataList, Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendsCircle friendsCircle = dataList.get(position);
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_friends_circle, null);
            viewHolder = new ViewHolder();
            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            viewHolder.mNickNameTv = convertView.findViewById(R.id.tv_nick_name);
            viewHolder.mContentTv = convertView.findViewById(R.id.tv_content);
            viewHolder.mMoreTv = convertView.findViewById(R.id.tv_more);
            viewHolder.mCreateTimeTv = convertView.findViewById(R.id.tv_create_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mAvatarSdv.setImageURI(Uri.parse(friendsCircle.getUserAvatar()));
        viewHolder.mNickNameTv.setText(friendsCircle.getUserNickName());
        viewHolder.mContentTv.setText(friendsCircle.getCircleContent());
        viewHolder.mCreateTimeTv.setText(TimestampUtil.getTimePoint(friendsCircle.getTimestamp()));

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView mAvatarSdv;
        TextView mNickNameTv;
        TextView mContentTv;
        TextView mMoreTv;
        TextView mCreateTimeTv;
    }
}
