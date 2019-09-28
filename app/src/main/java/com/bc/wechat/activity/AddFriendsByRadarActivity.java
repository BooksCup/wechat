package com.bc.wechat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.facebook.drawee.view.SimpleDraweeView;

public class AddFriendsByRadarActivity extends FragmentActivity {
    private SimpleDraweeView mAvatarSdv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_by_radar);

        mAvatarSdv = findViewById(R.id.sdv_avatar);
        User user = PreferencesUtil.getInstance().getUser();
        mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
    }

    public void back(View view) {
        finish();
    }
}
