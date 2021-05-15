package com.bc.wechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.ModifyAddressActivity;
import com.bc.wechat.entity.Address;

import java.util.List;

/**
 * 我的地址
 *
 * @author zhou
 */
public class MyAddressAdapter extends BaseAdapter {

    Context mContext;
    List<Address> mAddressList;

    public MyAddressAdapter(Context context, List<Address> addressList) {
        this.mContext = context;
        this.mAddressList = addressList;
    }

    public void setData(List<Address> dataList) {
        this.mAddressList = dataList;
    }

    @Override
    public int getCount() {
        return mAddressList.size();
    }

    @Override
    public Address getItem(int position) {
        return mAddressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Address address = mAddressList.get(position);
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_address, null);

            viewHolder.mRootLl = convertView.findViewById(R.id.ll_root);
            viewHolder.mUserInfoTv = convertView.findViewById(R.id.tv_user_info);
            viewHolder.mAddressInfoTv = convertView.findViewById(R.id.tv_address_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        StringBuffer userInfoBuffer = new StringBuffer();
        userInfoBuffer.append(address.getAddressName()).append("，").append(address.getAddressPhone());
        StringBuffer addressInfoBuffer = new StringBuffer();
        addressInfoBuffer.append(address.getAddressProvince()).append(" ")
                .append(address.getAddressCity()).append(" ")
                .append(address.getAddressDistrict()).append(" ")
                .append(address.getAddressDetail());
        viewHolder.mUserInfoTv.setText(userInfoBuffer.toString());
        viewHolder.mAddressInfoTv.setText(addressInfoBuffer.toString());
        viewHolder.mRootLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ModifyAddressActivity.class);
                intent.putExtra("address", address);
                mContext.startActivity(intent);
            }
        });
        viewHolder.mRootLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout mRootLl;
        TextView mUserInfoTv;
        TextView mAddressInfoTv;
    }
}
