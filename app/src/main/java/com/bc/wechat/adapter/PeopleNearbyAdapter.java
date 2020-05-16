package com.bc.wechat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bc.wechat.entity.User;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 附近的人
 *
 * @author zhou
 */
public class PeopleNearbyAdapter extends ArrayAdapter<User> {
    List<User> mPeopleNearbyList;
    int mResource;
    private LayoutInflater mLayoutInflater;

    public PeopleNearbyAdapter(Context context, int resource, List<User> peopleNearbyList) {
        super(context, resource, peopleNearbyList);
        this.mResource = resource;
        this.mPeopleNearbyList = peopleNearbyList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(mResource, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public User getItem(int position) {
        return mPeopleNearbyList.get(position);
    }

    @Override
    public int getCount() {
        return mPeopleNearbyList.size();
    }

    public void setData(List<User> peopleNearbyList) {
        this.mPeopleNearbyList = peopleNearbyList;
    }

    class ViewHolder {
        // 头像
        SimpleDraweeView mAvatarSdv;
        // 姓名
        TextView mNameTv;
        // 签名
        TextView mWhatsupTv;
        // 距离信息
        TextView mDistanceTv;
    }

}
