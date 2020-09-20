package com.bc.wechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.entity.QrCodeContent;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.google.zxing.client.android.CaptureActivity2;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加朋友
 *
 * @author zhou
 */
public class AddContactsActivity extends BaseActivity {

    private static final int REQUEST_CODE_SCAN = 0;
    private static final int REQUEST_CODE_CAMERA = 1;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_wx_id)
    TextView mWxIdTv;

    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        ButterKnife.bind(this);

        initStatusBar();
        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mWxIdTv.setText("我的微信号：" + mUser.getUserWxId());
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_search, R.id.rl_friend_radar, R.id.rl_scan, R.id.ll_my_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                startActivity(new Intent(this, AddFriendsBySearchActivity.class));
                break;

            case R.id.rl_friend_radar:
                startActivity(new Intent(this, AddFriendsByRadarActivity.class));
                break;

            case R.id.rl_scan:
                String[] permissions = new String[]{"android.permission.CAMERA"};
                requestPermissions(AddContactsActivity.this, permissions, REQUEST_CODE_CAMERA);
                break;

            case R.id.ll_my_info:
                startActivity(new Intent(this, MyQrCodeActivity.class));
                break;
        }
    }

    private void startScanActivity() {
        Intent intent = new Intent(AddContactsActivity.this, CaptureActivity2.class);
        intent.putExtra(CaptureActivity2.USE_DEFUALT_ISBN_ACTIVITY, true);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            // 同意权限做的处理,开启服务提交通讯录
            startScanActivity();
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(this, permissions[0]);
        }
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
                startScanActivity();
            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                ActivityCompat.requestPermissions(this, requestPermissions, requestCode);
            }
        }
    }

    public void handleRejectPermission(final Activity context, String permission) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(AddContactsActivity.this, "权限申请",
                    "在设置-应用-微信-权限中开启相机权限，以正常使用拍照、小视频、扫一扫等功能",
                    "去设置", "取消", context.getColor(R.color.navy_blue));
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


    @Override
    public int checkSelfPermission(String permission) {
        return super.checkSelfPermission(permission);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SCAN) {
                String isbn = data.getStringExtra("CaptureIsbn");
                if (!TextUtils.isEmpty(isbn)) {
                    if (isbn.contains("http")) {
                        Intent intent = new Intent(this, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.RESULT, isbn);
                        startActivity(intent);
                    } else {
                        try {
                            QrCodeContent qrCodeContent = JSON.parseObject(isbn, QrCodeContent.class);
                            if (QrCodeContent.QR_CODE_TYPE_USER.equals(qrCodeContent.getType())) {
                                startActivity(new Intent(this, UserInfoActivity.class).
                                        putExtra("userId", String.valueOf(qrCodeContent.getContentMap().get("userId"))));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
