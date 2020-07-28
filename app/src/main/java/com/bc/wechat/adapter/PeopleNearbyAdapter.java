package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.PeopleNearby;
import com.bc.wechat.entity.PositionInfo;
import com.bc.wechat.utils.DistanceUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 附近的人
 *
 * @author zhou
 */
public class PeopleNearbyAdapter extends ArrayAdapter<PeopleNearby> {
    List<PeopleNearby> mPeopleNearbyList;
    int mResource;
    private LayoutInflater mLayoutInflater;
    boolean mSexFilter = false;

    public PeopleNearbyAdapter(Context context, int resource, List<PeopleNearby> peopleNearbyList) {
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
            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            viewHolder.mNameTv = convertView.findViewById(R.id.tv_name);
            viewHolder.mSexIv = convertView.findViewById(R.id.iv_sex);
            viewHolder.mWhatsupTv = convertView.findViewById(R.id.tv_whats_up);
            viewHolder.mDistanceTv = convertView.findViewById(R.id.tv_distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PeopleNearby peopleNearby = mPeopleNearbyList.get(position);
        if (!TextUtils.isEmpty(peopleNearby.getUserAvatar())) {
            viewHolder.mAvatarSdv.setImageURI(Uri.parse(peopleNearby.getUserAvatar()));
        }
        viewHolder.mNameTv.setText(peopleNearby.getUserNickName());

        if (mSexFilter) {
            // 开启性别筛选,不显示每个item的性别
            viewHolder.mSexIv.setVisibility(View.GONE);
        } else {
            // 关闭性别筛选，显示每个item的性别
            viewHolder.mSexIv.setVisibility(View.VISIBLE);
            if (Constant.USER_SEX_MALE.equals(peopleNearby.getUserSex())) {
                viewHolder.mSexIv.setImageResource(R.mipmap.icon_sex_male);
            } else if (Constant.USER_SEX_FEMALE.equals(peopleNearby.getUserSex())) {
                viewHolder.mSexIv.setImageResource(R.mipmap.icon_sex_female);
            } else {
                viewHolder.mSexIv.setVisibility(View.GONE);
            }
        }

        if (TextUtils.isEmpty(peopleNearby.getUserSign())) {
            viewHolder.mWhatsupTv.setVisibility(View.GONE);
        } else {
            viewHolder.mWhatsupTv.setText(peopleNearby.getUserSign());
        }
        PositionInfo myPositionInfo = PreferencesUtil.getInstance().getPositionInfo();
        double distance = DistanceUtil.getDistance(myPositionInfo.getLongitude(), myPositionInfo.getLatitude(),
                Double.valueOf(peopleNearby.getLongitude()), Double.valueOf(peopleNearby.getLatitude()));
        String distancePretty = DistanceUtil.getDistancePretty(distance, peopleNearby.getRegion());
        viewHolder.mDistanceTv.setText(distancePretty);

        return convertView;
    }

    @Override
    public PeopleNearby getItem(int position) {
        return mPeopleNearbyList.get(position);
    }

    @Override
    public int getCount() {
        return mPeopleNearbyList.size();
    }

    public void setData(List<PeopleNearby> peopleNearbyList) {
        this.mPeopleNearbyList = peopleNearbyList;
    }

    public void setSexFilter(boolean sexFilter) {
        this.mSexFilter = sexFilter;
    }

    class ViewHolder {
        // 头像
        SimpleDraweeView mAvatarSdv;
        // 姓名
        TextView mNameTv;
        // 性别
        ImageView mSexIv;
        // 签名
        TextView mWhatsupTv;
        // 距离信息
        TextView mDistanceTv;
    }

}
