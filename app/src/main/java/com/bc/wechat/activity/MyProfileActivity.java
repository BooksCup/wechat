package com.bc.wechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.engine.GlideEngine;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.FileUtil;
import com.bc.wechat.utils.OssUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.LoadingDialog;
import com.blankj.utilcode.util.CollectionUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人信息
 *
 * @author zhou
 */
public class MyProfileActivity extends BaseActivity {

    // 头像
    @BindView(R.id.rl_avatar)
    RelativeLayout mAvatarRl;

    // 昵称
    @BindView(R.id.rl_nick_name)
    RelativeLayout mNickNameRl;

    // 微信号
    @BindView(R.id.rl_wx_id)
    RelativeLayout mWxIdRl;

    // 二维码
    @BindView(R.id.rl_qr_code)
    RelativeLayout mQrCodeRl;

    // 更多
    @BindView(R.id.rl_more)
    RelativeLayout mMoreRl;

    // 我的地址
    @BindView(R.id.rl_address)
    RelativeLayout mAddressRl;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_nick_name)
    TextView mNickNameTv;

    @BindView(R.id.tv_wx_id)
    TextView mWxIdTv;

    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.iv_wx_id)
    ImageView mWxIdIv;

    VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;

    private static final int UPDATE_AVATAR_BY_TAKE_CAMERA = 1;
    private static final int UPDATE_AVATAR_BY_ALBUM = 2;
    private static final int UPDATE_USER_NICK_NAME = 3;
    private static final int UPDATE_USER_WX_ID = 4;
    private static final int UPDATE_USER_AVATAR = 5;

    User mUser;
    String mImageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        initStatusBar();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(MyProfileActivity.this);

        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();
        initCamera();
    }

    private void initView() {
        setTitleStrokeWidth(mTitleTv);
        mNickNameTv.setText(mUser.getUserNickName());

        String userAvatar = mUser.getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            String resizeAvatarUrl = OssUtil.resize(userAvatar);
            mAvatarSdv.setImageURI(resizeAvatarUrl);
        }

        renderWxId(mUser);
    }

    @OnClick({R.id.rl_avatar, R.id.sdv_avatar, R.id.rl_nick_name, R.id.rl_wx_id,
            R.id.rl_qr_code, R.id.rl_more, R.id.rl_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_avatar:
                showPhotoDialog();
                break;
            case R.id.sdv_avatar:
                Intent intent = new Intent(this, BigImageActivity.class);
                intent.putExtra("imgUrl", mUser.getUserAvatar());
                startActivity(intent);
                break;
            case R.id.rl_nick_name:
                // 昵称
                startActivityForResult(new Intent(this, EditNameActivity.class), UPDATE_USER_NICK_NAME);
                break;
            case R.id.rl_wx_id:
                startActivityForResult(new Intent(this, EditWeChatIdActivity.class), UPDATE_USER_WX_ID);
                break;
            case R.id.rl_qr_code:
                startActivity(new Intent(this, MyQrCodeActivity.class));
                break;
            case R.id.rl_more:
                startActivity(new Intent(this, MyMoreProfileActivity.class));
                break;
            case R.id.rl_address:
                startActivity(new Intent(this, MyAddressActivity.class));
                break;
        }
    }

    public void back(View view) {
        finish();
    }

    /**
     * 渲染微信ID
     */
    private void renderWxId(User user) {
        mWxIdTv.setText(user.getUserWxId());
        // 微信号只能修改一次
        if (Constant.USER_WX_ID_MODIFY_FLAG_TRUE.equals(user.getUserWxIdModifyFlag())) {
            mWxIdIv.setVisibility(View.GONE);
            mWxIdRl.setClickable(false);
        } else {
            mWxIdRl.setClickable(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final User user = PreferencesUtil.getInstance().getUser();
            switch (requestCode) {
                case UPDATE_USER_NICK_NAME:
                    // 昵称
                    mNickNameTv.setText(user.getUserNickName());
                    break;
                case UPDATE_USER_WX_ID:
                    renderWxId(user);
                    break;
                case UPDATE_USER_AVATAR:
                    mDialog.setMessage("正在上传头像");
                    mDialog.show();
                    mDialog.setCanceledOnTouchOutside(false);
                    // 返回对象集合: 包含图片的宽、高、大小、用户是否选中原图选项等信息
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    if (!CollectionUtils.isEmpty(resultPhotos)) {
                        new Thread(() -> {
                            List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", resultPhotos.get(0).path);
                            if (null != imageList && imageList.size() > 0) {
                                updateUserAvatar(user.getUserId(), imageList.get(0));
                            }
                        }).start();
                    }
                    break;
            }
        }
    }

    /**
     * 显示修改头像对话框
     */
    private void showPhotoDialog() {
        EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.bc.wechat.fileprovider")
                .setCount(1)//参数说明：最大可选数，默认1
                .start(UPDATE_USER_AVATAR);
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
                mAvatarSdv.setImageURI(OssUtil.resize(userAvatar));
                mDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(MyProfileActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(MyProfileActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    /**
     * android 7.0系统解决拍照的问题
     */
    private void initCamera() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    /**
     * 动态权限
     */
    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android 6.0开始的动态权限，这里进行版本判断
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                // 非初次进入App且已授权
                switch (requestCode) {
                    case UPDATE_AVATAR_BY_TAKE_CAMERA:
                        showCamera();
                        break;
                    case UPDATE_AVATAR_BY_ALBUM:
                        showAlbum();
                        break;
                }
            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
            }
        }
    }

    /**
     * requestPermissions的回调
     * 一个或多个权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        // 判断是否拒绝  拒绝后要怎么处理 以及取消再次提示的处理
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {
            switch (requestCode) {
                case UPDATE_AVATAR_BY_TAKE_CAMERA:
                    showCamera();
                    break;
                case UPDATE_AVATAR_BY_ALBUM:
                    showAlbum();
                    break;
            }
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(MyProfileActivity.this, permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            String content = "";
            // 非初次进入App且已授权
            switch (requestCode) {
                case UPDATE_AVATAR_BY_TAKE_CAMERA:
                    content = getString(R.string.request_permission_camera);
                    break;
                case UPDATE_AVATAR_BY_ALBUM:
                    content = getString(R.string.request_permission_storage);
                    break;
            }

            final ConfirmDialog mConfirmDialog = new ConfirmDialog(MyProfileActivity.this, "权限申请",
                    content,
                    "去设置", "取消", getColor(R.color.navy_blue));
            mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    mConfirmDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelClick() {
                    mConfirmDialog.dismiss();
                }
            });
            // 点击空白处消失
            mConfirmDialog.setCancelable(false);
            mConfirmDialog.show();
        }
    }

    /**
     * 跳转到相机
     */
    private void showCamera() {
        mImageName = CommonUtil.generateId() + ".png";
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                new File(Environment.getExternalStorageDirectory(), mImageName)));
        startActivityForResult(cameraIntent, UPDATE_AVATAR_BY_TAKE_CAMERA);
    }

    /**
     * 跳转到相册
     */
    private void showAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, UPDATE_AVATAR_BY_ALBUM);
    }

}