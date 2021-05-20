package com.bc.wechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.StatusGroup;

import java.util.List;

/**
 * 状态组
 *
 * @author zhou
 */
public class StatusGroupAdapter extends BaseAdapter {

    Context mContext;
    int mResource;
    List<StatusGroup> mStatusGroupList;
    StatusAdapter mAdapter;

    public StatusGroupAdapter(Context context, int resource,
                              List<StatusGroup> statusGroupList) {
        this.mContext = context;
        this.mResource = resource;
        this.mStatusGroupList = statusGroupList;
    }

    public void setData(List<StatusGroup> dataList) {
        this.mStatusGroupList = dataList;
    }

    @Override
    public int getCount() {
        if (mStatusGroupList == null) {
            return 0;
        } else {
            return mStatusGroupList.size();
        }
    }

    @Override
    public StatusGroup getItem(int position) {
        if (mStatusGroupList == null) {
            return null;
        } else {
            return mStatusGroupList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            holder.mStatusGv = convertView.findViewById(R.id.gv_status);
            holder.mGroupNameTv = convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        StatusGroup statusGroup = mStatusGroupList.get(position);
        holder.mGroupNameTv.setText(statusGroup.getName());
        mAdapter = new StatusAdapter(mContext, R.layout.item_status, statusGroup.getStatusList());
        holder.mStatusGv.setHorizontalSpacing(40);
        holder.mStatusGv.setVerticalSpacing(40);
        holder.mStatusGv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return convertView;
    }

    class ViewHolder {
        TextView mGroupNameTv;
        GridView mStatusGv;
    }

}