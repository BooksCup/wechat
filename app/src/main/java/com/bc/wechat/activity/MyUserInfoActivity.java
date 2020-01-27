package com.bc.wechat.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.FileUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人信息
 *
 * @author zhou
 */
public class MyUserInfoActivity extends FragmentActivity implements View.OnClickListener {

    private static final int WRITE_PERMISSION = 0x01;
    // 头像
    private RelativeLayout mAvatarRl;
    // 昵称
    private RelativeLayout mNickNameRl;
    // 微信号
    private RelativeLayout mWxIdRl;
    // 二维码
    private RelativeLayout mQrCodeRl;
    // 更多
    private RelativeLayout mMoreRl;


    private TextView mNickNameTv;
    private TextView mWxIdTv;
    private SimpleDraweeView mAvatarSdv;

    private VolleyUtil mVolleyUtil;

    private static final int UPDATE_AVATAR_BY_ALBUM = 2;
    private static final int UPDATE_USER_NICK_NAME = 3;
    private static final int UPDATE_USER_WX_ID = 4;

    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        mVolleyUtil = VolleyUtil.getInstance(this);
        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        requestWritePermission();
        initView();
    }

    private void initView() {
        mAvatarRl = findViewById(R.id.rl_avatar);

        mNickNameRl = findViewById(R.id.rl_nick_name);
        mNickNameTv = findViewById(R.id.tv_nick_name);

        mWxIdRl = findViewById(R.id.rl_wx_id);
        mWxIdTv = findViewById(R.id.tv_wx_id);

        mQrCodeRl = findViewById(R.id.rl_qr_code);

        mMoreRl = findViewById(R.id.rl_more);

        mAvatarSdv = findViewById(R.id.sdv_avatar);

        mNickNameTv.setText(mUser.getUserNickName());
        mWxIdTv.setText(mUser.getUserWxId());
        String userAvatar = mUser.getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            mAvatarSdv.setImageURI(Uri.parse(userAvatar));
        }

        mAvatarRl.setOnClickListener(this);
        mNickNameRl.setOnClickListener(this);
        mWxIdRl.setOnClickListener(this);

        mQrCodeRl.setOnClickListener(this);
        mMoreRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_avatar:
                showPhotoDialog();
                break;
            case R.id.rl_nick_name:
                // 昵称
                startActivityForResult(new Intent(this, UpdateNickNameActivity.class), UPDATE_USER_NICK_NAME);
                break;
            case R.id.rl_wx_id:
                startActivityForResult(new Intent(this, UpdateWxIdActivity.class), UPDATE_USER_WX_ID);
                break;
            case R.id.rl_qr_code:
                startActivity(new Intent(this, QrCodeActivity.class));
                break;
            case R.id.rl_more:
                startActivity(new Intent(this, MyMoreUserInfoActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            final User user = PreferencesUtil.getInstance().getUser();
            switch (requestCode) {
                case UPDATE_AVATAR_BY_ALBUM:
                    if (data != null) {
                        Uri uri = data.getData();
                        final String filePath = FileUtil.getFilePathByUri(this, uri);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<String> imageList = FileUtil.uploadFile(Constant.FILE_UPLOAD_URL, filePath);
                                if (null != imageList && imageList.size() > 0) {
                                    String newAvatar = Constant.FILE_BASE_URL + imageList.get(0);
                                    updateUserAvatar(user.getUserId(), newAvatar);
                                }
                            }
                        }).start();

                        mAvatarSdv.setImageURI(uri);
                    }
                    break;
                case UPDATE_USER_NICK_NAME:
                    // 昵称
                    mNickNameTv.setText(user.getUserNickName());
                    break;
                case UPDATE_USER_WX_ID:
                    // 微信号
                    mWxIdTv.setText(user.getUserWxId());
                    break;
            }
        }
    }

    /**
     * 显示修改头像对话框
     */
    private void showPhotoDialog() {
        final AlertDialog photoDialog = new AlertDialog.Builder(this).create();
        photoDialog.show();
        Window window = photoDialog.getWindow();
        window.setContentView(R.layout.dialog_alert);
        TextView mTakePicTv = window.findViewById(R.id.tv_content1);
        TextView mAlbumTv = window.findViewById(R.id.tv_content2);
        mTakePicTv.setText("拍照");
        mTakePicTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mAlbumTv.setText("相册");
        mAlbumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, UPDATE_AVATAR_BY_ALBUM);
                photoDialog.dismiss();
            }
        });

    }

    /**
     * 修改用户头像
     *
     * @param userId     用户ID
     * @param userAvatar 用户头像
     */
    private void updateUserAvatar(String userId, final String userAvatar) {
        String url = Constant.BASE_URL + "users/" + userId + "/userAvatar";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userAvatar", userAvatar);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mUser.setUserAvatar(userAvatar);
                PreferencesUtil.getInstance().setUser(mUser);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(MyUserInfoActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(MyUserInfoActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    // 运行时添加权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestWritePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
        }
    }
}
