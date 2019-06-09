package com.bc.wechat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.PickContactAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateChatRoomActivity extends FragmentActivity {

    private PickContactAdapter contactAdapter;
    private ListView listView;
    String firstUserId;
    String firstUserNickName;
    String firstUserAvatar;

    // 可滑动的显示选中用户的View
    private LinearLayout mAvatarListLl;
    private ImageView mSearchIv;
    private Button mSaveBtn;


    private List<String> checkedUserIdList = new ArrayList<>();
    private List<Friend> checkedUserList = new ArrayList<>();
    private int totalCount = 0;

    private VolleyUtil volleyUtil;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        volleyUtil = VolleyUtil.getInstance(this);
        loadingDialog = new LoadingDialog(CreateChatRoomActivity.this);
        firstUserId = getIntent().getStringExtra("userId");
        firstUserNickName = getIntent().getStringExtra("userNickName");
        firstUserAvatar = getIntent().getStringExtra("userAvatar");
        final List<Friend> friendList = Friend.listAll(Friend.class);
        // 对list进行排序
        Collections.sort(friendList, new PinyinComparator() {
        });
        mAvatarListLl = findViewById(R.id.ll_avatar_list);
        mSearchIv = findViewById(R.id.iv_search);
        mSaveBtn = findViewById(R.id.btn_save);

        listView = findViewById(R.id.lv_friends);
        contactAdapter = new PickContactAdapter(this,
                R.layout.item_pick_contact_list, friendList, checkedUserIdList, firstUserId);
        listView.setAdapter(contactAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Friend friend = friendList.get(position);
                CheckBox mPickFriendCb = view.findViewById(R.id.cb_pick_friend);
                boolean isEnabled = mPickFriendCb.isEnabled();
                boolean isChecked = mPickFriendCb.isChecked();
                if (isEnabled) {
                    if (isChecked) {
                        mPickFriendCb.setChecked(false);
                        removeCheckedImage(friend.getUserId(), friend);
                    } else {
                        mPickFriendCb.setChecked(true);
                        addCheckedImage(friend.getUserAvatar(), friend.getUserId(), friend);
                    }
                }
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.setMessage("正在创建群聊...");
                loadingDialog.show();
                createGroup();
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void addCheckedImage(String userAvatar, final String userId, final Friend friend) {
        // 是否已包含
        if (checkedUserIdList.contains(userId)) {
            return;
        }
        totalCount++;
        checkedUserIdList.add(userId);
        checkedUserList.add(friend);
        // 包含TextView的LinearLayout
        // 参数设置
        android.widget.LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                108, 108, 1);
        View view = LayoutInflater.from(this).inflate(
                R.layout.item_chatroom_header_item, null);
        SimpleDraweeView mAvatarSdv = view.findViewById(R.id.sdv_avatar);
        mAvatarSdv.setImageURI(Uri.parse(userAvatar));
        menuLinerLayoutParames.setMargins(6, 0, 6, 15);

        // 设置id，方便后面删除
        view.setTag(userId);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCheckedImage(userId, friend);
                contactAdapter.notifyDataSetChanged();
            }
        });
        mAvatarListLl.addView(view, menuLinerLayoutParames);
        if (totalCount > 0) {
            if (mSearchIv.getVisibility() == View.VISIBLE) {
                mSearchIv.setVisibility(View.GONE);
            }

            mSaveBtn.setText("确定(" + totalCount + ")");
            mSaveBtn.setEnabled(true);
            mSaveBtn.setTextColor(0xFFFFFFFF);
        }
    }

    private void removeCheckedImage(String userId, Friend friend) {
        View view = mAvatarListLl.findViewWithTag(userId);
        mAvatarListLl.removeView(view);
        totalCount--;
        checkedUserIdList.remove(userId);
        checkedUserList.remove(friend);
        mSaveBtn.setText("确定(" + totalCount + ")");
        if (totalCount <= 0) {
            if (mSearchIv.getVisibility() == View.GONE) {
                mSearchIv.setVisibility(View.VISIBLE);
            }
            mSaveBtn.setText("确定");
            mSaveBtn.setEnabled(false);
            mSaveBtn.setTextColor(0xFFD0EFC6);
        }
    }

    public class PinyinComparator implements Comparator<Friend> {

        @Override
        public int compare(Friend o1, Friend o2) {
            String py1 = o1.getUserHeader();
            String py2 = o2.getUserHeader();
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;
            String str1 = "";
            String str2 = "";
            try {
                str1 = ((o1.getUserHeader()).toUpperCase()).substring(0, 1);
                str2 = ((o2.getUserHeader()).toUpperCase()).substring(0, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

    private void createGroup() {
        String url = Constant.BASE_URL + "groups";
        Map<String, String> paramMap = new HashMap<>();
        User user = PreferencesUtil.getInstance().getUser();
        List<String> pickedUserIdList = new ArrayList<>();
        pickedUserIdList.addAll(checkedUserIdList);
        pickedUserIdList.add(firstUserId);

        StringBuffer pickedUserIdBuffer = new StringBuffer();
        if (null != pickedUserIdList && pickedUserIdList.size() > 0) {
            for (String pickedUserId : pickedUserIdList) {
                pickedUserIdBuffer.append(pickedUserId);
                pickedUserIdBuffer.append(",");
            }
            pickedUserIdBuffer.deleteCharAt(pickedUserIdBuffer.length() - 1);
        }

        final StringBuffer pickedUserNickNameBuffer = new StringBuffer();
        if (null != checkedUserList && checkedUserList.size() > 0) {
            for (Friend checkedUser : checkedUserList) {
                pickedUserNickNameBuffer.append(checkedUser.getUserNickName());
                pickedUserNickNameBuffer.append("、");
            }

            pickedUserNickNameBuffer.append(firstUserNickName);
        }

        String userIds = pickedUserIdBuffer.toString();
        final String groupName = pickedUserNickNameBuffer.toString();
        paramMap.put("owner", user.getUserId());
        paramMap.put("groupName", groupName);
        paramMap.put("desc", "");
        paramMap.put("userIds", userIds);

        volleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Intent intent = new Intent(CreateChatRoomActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loadingDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(CreateChatRoomActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(CreateChatRoomActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
