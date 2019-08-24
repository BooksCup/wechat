package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FriendsAdapter extends ArrayAdapter<User> {

    List<User> userList;
    int resource;
    private LayoutInflater layoutInflater;

    public FriendsAdapter(Context context, int resource, List<User> userList) {
        super(context, resource, userList);
        this.resource = resource;
        this.userList = userList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = layoutInflater.inflate(resource, null);
        }

        SimpleDraweeView mAvatarSdv = convertView.findViewById(R.id.iv_avatar);
        TextView mNameTv = convertView.findViewById(R.id.tv_name);
        TextView mHeaderTv = convertView.findViewById(R.id.tv_header);
        View mTempView = convertView.findViewById(R.id.view_temp);


        User user = getItem(position);
        String header = user.getHeader();
        String avatar = user.getAvatar();
        if (0 == position || (null != header && !header.equals(getItem(position - 1).getHeader()))) {
            if ("".equals(header)) {
                mHeaderTv.setVisibility(View.GONE);
                mTempView.setVisibility(View.VISIBLE);
            } else {
                mHeaderTv.setVisibility(View.VISIBLE);
                mHeaderTv.setText(header);
                mTempView.setVisibility(View.GONE);
            }
        } else {
            mHeaderTv.setVisibility(View.GONE);
            mTempView.setVisibility(View.VISIBLE);
        }

        mNameTv.setText(user.getNickName());
        mAvatarSdv.setImageURI(Uri.parse(avatar));

        return convertView;
    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
