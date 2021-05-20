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

    Context mContext;
    int mResource;
    List<Status> mStatusList;

    public StatusAdapter(Context context, int resource, List<Status> statusList) {
        this.mContext = context;
        this.mResource = resource;
        this.mStatusList = statusList;
    }

    @Override
    public int getCount() {
        if (mStatusList == null) {
            return 0;
        } else {
            return mStatusList.size();
        }
    }

    @Override
    public Status getItem(int position) {
        if (mStatusList == null) {
            return null;
        } else {
            return mStatusList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            viewHolder.mIconIv = convertView.findViewById(R.id.iv_icon);
            viewHolder.mNameTv = convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Status status = mStatusList.get(position);
        viewHolder.mNameTv.setText(status.getName());
        if (!TextUtils.isEmpty(status.getIcon())) {
            int iconId = mContext.getResources().getIdentifier(status.getIcon(),
                    "mipmap", mContext.getPackageName());
            viewHolder.mIconIv.setImageResource(iconId);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView mIconIv;
        TextView mNameTv;
    }

}