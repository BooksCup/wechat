package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.fragment.ConversationFragment;
import com.bc.wechat.fragment.FindFragment;
import com.bc.wechat.fragment.FriendsFragment;
import com.bc.wechat.fragment.ProfileFragment;

public class MainActivity extends FragmentActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
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
}
