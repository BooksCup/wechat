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

    Context context;
    int resource;
    List<StatusGroup> mUserStatusGroupList;
    StatusAdapter mAdapter;

    public StatusGroupAdapter(Context context, int resource,
                              List<StatusGroup> userStatusGroupList) {
        this.context = context;
        this.resource = resource;
        this.mUserStatusGroupList = userStatusGroupList;
    }

    public void setData(List<StatusGroup> dataList) {
        this.mUserStatusGroupList = dataList;
    }

    @Override
    public int getCount() {
        if (mUserStatusGroupList == null) {
            return 0;
        } else {
            return mUserStatusGroupList.size();
        }
    }

    @Override
    public StatusGroup getItem(int position) {
        if (mUserStatusGroupList == null) {
            return null;
        } else {
            return mUserStatusGroupList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(resource, null);
            holder.mUserStatusGv = convertView.findViewById(R.id.gv_user_status);
            holder.mGroupNameTv = convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        StatusGroup userStatusGroup = mUserStatusGroupList.get(position);
        holder.mGroupNameTv.setText(userStatusGroup.getName());
        mAdapter = new StatusAdapter(context, R.layout.item_user_status, userStatusGroup.getStatusList());
        holder.mUserStatusGv.setHorizontalSpacing(40);
        holder.mUserStatusGv.setVerticalSpacing(40);
        holder.mUserStatusGv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return convertView;
    }

    class ViewHolder {
        TextView mGroupNameTv;
        GridView mUserStatusGv;
    }

}