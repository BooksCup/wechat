package com.bc.wechat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.Status;

import java.util.List;

/**
 * 状态
 *
 * @author zhou
 */
public class StatusAdapter extends BaseAdapter {

    Context context;
    int resource;
    List<Status> mUserStatusList;

    public StatusAdapter(Context context, int resource, List<Status> userStatusList) {
        this.context = context;
        this.resource = resource;
        this.mUserStatusList = userStatusList;
    }

    @Override
    public int getCount() {
        if (mUserStatusList == null) {
            return 0;
        } else {
            return mUserStatusList.size();
        }
    }

    @Override
    public Status getItem(int position) {
        if (mUserStatusList == null) {
            return null;
        } else {
            return mUserStatusList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(resource, null);
            viewHolder.mIconIv = convertView.findViewById(R.id.iv_icon);
            viewHolder.mNameTv = convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Status userStatus = mUserStatusList.get(position);
        viewHolder.mNameTv.setText(userStatus.getName());
        if (!TextUtils.isEmpty(userStatus.getIcon())) {
            int iconId = context.getResources().getIdentifier(userStatus.getIcon(),
                    "mipmap", context.getPackageName());
            viewHolder.mIconIv.setImageResource(iconId);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView mIconIv;
        TextView mNameTv;
    }
}