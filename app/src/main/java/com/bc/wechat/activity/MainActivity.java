package com.bc.wechat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.fragment.ConversationFragment;
import com.bc.wechat.fragment.FindFragment;
import com.bc.wechat.fragment.FriendsFragment;
import com.bc.wechat.fragment.ProfileFragment;
import com.bc.wechat.utils.ExampleUtil;
import com.bc.wechat.utils.PreferencesUtil;

public class MainActivity extends FragmentActivity {

    public static boolean isForeground = false;

    private Fragment[] fragments;
    private ConversationFragment conversationFragment;
    private FriendsFragment friendsFragment;
    private FindFragment findFragment;
    private ProfileFragment profileFragment;

    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    // 当前fragment的index
    private int currentTabIndex;

    private TextView mUnreadNewFriendsNumTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        PreferencesUtil.getInstance().init(this);
        registerMessageReceiver();
        refreshNewFriendsUnreadNum();
    }

    private void initView() {
        conversationFragment = new ConversationFragment();
        friendsFragment = new FriendsFragment();
        findFragment = new FindFragment();
        profileFragment = new ProfileFragment();

        fragments = new Fragment[]{conversationFragment, friendsFragment, findFragment, profileFragment};

        imagebuttons = new ImageView[4];
        imagebuttons[0] = findViewById(R.id.ib_weixin);
        imagebuttons[1] = findViewById(R.id.ib_contact_list);
        imagebuttons[2] = findViewById(R.id.ib_find);
        imagebuttons[3] = findViewById(R.id.ib_profile);

        imagebuttons[0].setSelected(true);
        textviews = new TextView[4];
        textviews[0] = findViewById(R.id.tv_weixin);
        textviews[1] = findViewById(R.id.tv_contact_list);
        textviews[2] = findViewById(R.id.tv_find);
        textviews[3] = findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF45C01A);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, conversationFragment)
                .add(R.id.fragment_container, friendsFragment)
                .add(R.id.fragment_container, findFragment)
                .add(R.id.fragment_container, profileFragment)
                .hide(friendsFragment).hide(findFragment).hide(profileFragment)
                .show(conversationFragment).commit();

        mUnreadNewFriendsNumTv = findViewById(R.id.unread_address_number);
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_weixin:
                index = 0;
                break;
            case R.id.re_contact_list:
                index = 1;
                break;
            case R.id.re_find:
                index = 2;
                break;
            case R.id.re_profile:
                index = 3;
                break;
        }

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        refreshNewFriendsUnreadNum();
        friendsFragment.refreshNewFriendsUnreadNum();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION_ADD_FRIENDS = "com.bc.wechat.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION_ADD_FRIENDS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION_ADD_FRIENDS.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    refreshNewFriendsUnreadNum();
                    friendsFragment.refreshNewFriendsUnreadNum();
                }
            } catch (Exception e) {
            }
        }
    }

    private void refreshNewFriendsUnreadNum() {
        int newFriendsUnreadNum = PreferencesUtil.getInstance().getNewFriendsUnreadNumber();
        if (newFriendsUnreadNum > 0) {
            mUnreadNewFriendsNumTv.setVisibility(View.VISIBLE);
            mUnreadNewFriendsNumTv.setText(String.valueOf(newFriendsUnreadNum));
        } else {
            mUnreadNewFriendsNumTv.setVisibility(View.GONE);
        }
    }
}
