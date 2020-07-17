package com.bc.wechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.NewFriendsApplyConfirmActivity;
import com.bc.wechat.activity.UserInfoStrangerActivity;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;
import java.util.Map;

/**
 * 手机通讯录
 *
 * @author zhou
 */
public class MobileContactsAdapter extends BaseAdapter {

    private Context mContext;
    private List<User> mUserList;
    private Map<String, String> mContactNameMap;
    private UserDao mUserDao;

    public MobileContactsAdapter(Context context, List<User> userList, Map<String, String> contactNameMap) {
        this.mContext = context;
        this.mUserList = userList;
        this.mContactNameMap = contactNameMap;
        mUserDao = new UserDao();
    }

    public void setData(List<User> dataList) {
        this.mUserList = dataList;
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public User getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final User user = mUserList.get(position);

        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mobile_contacts, null);

            viewHolder.mRootLl = convertView.findViewById(R.id.ll_root);
            viewHolder.mHeaderTv = convertView.findViewById(R.id.tv_header);
            viewHolder.mTempView = convertView.findViewById(R.id.view_header);
            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            viewHolder.mContactNameTv = convertView.findViewById(R.id.tv_contact_name);
            viewHolder.mWechatNameTv = convertView.findViewById(R.id.tv_wechat_name);
            viewHolder.mAddBtn = convertView.findViewById(R.id.btn_add);
            viewHolder.mAddedTv = convertView.findViewById(R.id.tv_added);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String contactName = mContactNameMap.get(user.getUserPhone());
        viewHolder.mContactNameTv.setText(contactName);

        if (!TextUtils.isEmpty(user.getUserAvatar())) {
            viewHolder.mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
        }
        viewHolder.mWechatNameTv.setText("微信:" + user.getUserNickName());

        String header = user.getUserHeader();

        if (0 == position || null != header && !header.equals(getItem(position - 1).getUserHeader())) {
            if (TextUtils.isEmpty(header)) {
                viewHolder.mHeaderTv.setVisibility(View.GONE);
                viewHolder.mTempView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mHeaderTv.setVisibility(View.VISIBLE);
                viewHolder.mHeaderTv.setText(header);
                viewHolder.mTempView.setVisibility(View.GONE);
            }
        } else {
            viewHolder.mHeaderTv.setVisibility(View.GONE);
            viewHolder.mTempView.setVisibility(View.VISIBLE);
        }

        final boolean isFriend = mUserDao.checkIsFriend(user.getUserPhone());
        if (isFriend) {
            // 是好友
            viewHolder.mAddBtn.setVisibility(View.GONE);
            viewHolder.mAddedTv.setVisibility(View.VISIBLE);
        } else {
            // 非好友
            viewHolder.mAddBtn.setVisibility(View.VISIBLE);
            viewHolder.mAddedTv.setVisibility(View.GONE);
        }

        viewHolder.mRootLl.setOnClickListener(view -> {
            if (isFriend) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("userId", user.getUserId());
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, UserInfoStrangerActivity.class);
                intent.putExtra("contactId", user.getUserId());
                intent.putExtra("from", Constant.CONTACTS_FROM_CONTACT);
                mContext.startActivity(intent);
            }
        });

        viewHolder.mAddBtn.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, NewFriendsApplyConfirmActivity.class);
            intent.putExtra("contactId", user.getUserId());
            intent.putExtra("from", Constant.CONTACTS_FROM_CONTACT);
            mContext.startActivity(intent);
        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout mRootLl;
        TextView mHeaderTv;
        View mTempView;
        SimpleDraweeView mAvatarSdv;
        TextView mContactNameTv;
        TextView mWechatNameTv;
        Button mAddBtn;
        TextView mAddedTv;
    }
}
