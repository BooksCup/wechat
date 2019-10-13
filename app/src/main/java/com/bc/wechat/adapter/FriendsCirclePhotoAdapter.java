package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bc.wechat.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FriendsCirclePhotoAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> photoList;

    public FriendsCirclePhotoAdapter(List<String> photoList, Context context) {
        this.photoList = photoList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String photo = photoList.get(position);
        convertView = LayoutInflater.from(mContext).inflate(
                R.layout.item_friends_circle_photo, null);
        SimpleDraweeView mPhotoSdv = convertView.findViewById(R.id.sdv_moment_photo);
        mPhotoSdv.setImageURI(Uri.parse(photo));

        return convertView;
    }
}
