package com.bc.wechat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.activity.MainActivity;
import com.bc.wechat.activity.MomentsActivity;
import com.bc.wechat.activity.PeopleNearbyActivity;
import com.bc.wechat.activity.SearchActivity;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.activity.WebViewActivity;
import com.bc.wechat.entity.QrCodeContent;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.google.zxing.client.android.CaptureActivity2;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 发现
 *
 * @author zhou
 */
public class DiscoverFragment extends Fragment implements View.OnClickListener {
    // 朋友圈
    private RelativeLayout mMomentsRl;

    // 扫一扫
    private RelativeLayout mScanRl;

    // 搜一搜
    private RelativeLayout mSearchRl;

    // 附近的人
    private RelativeLayout mPeopleNearbyRl;

    // 开启"附近的人"标记
    private ImageView mOpenPeopleNearbyIv;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mOpenPeopleNearbyIv = getView().findViewById(R.id.iv_open_people_nearby);
        if (PreferencesUtil.getInstance().isOpenPeopleNearby()) {
            mOpenPeopleNearbyIv.setVisibility(View.VISIBLE);
        } else {
            mOpenPeopleNearbyIv.setVisibility(View.GONE);
        }

        mMomentsRl = getView().findViewById(R.id.rl_moments);
        mScanRl = getView().findViewById(R.id.rl_scan);
        mSearchRl = getView().findViewById(R.id.rl_search);
        mPeopleNearbyRl = getView().findViewById(R.id.rl_people_nearby);

        mMomentsRl.setOnClickListener(this);
        mScanRl.setOnClickListener(this);
        mSearchRl.setOnClickListener(this);
        mPeopleNearbyRl.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferencesUtil.getInstance().isOpenPeopleNearby()) {
            mOpenPeopleNearbyIv.setVisibility(View.VISIBLE);
        } else {
            mOpenPeopleNearbyIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MainActivity.REQUEST_CODE_SCAN) {
                String isbn = data.getStringExtra("CaptureIsbn");
                if (!TextUtils.isEmpty(isbn)) {
                    if (isbn.contains("http")) {
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra(WebViewActivity.RESULT, isbn);
                        startActivity(intent);
                    } else {
                        try {
                            QrCodeContent qrCodeContent = JSON.parseObject(isbn, QrCodeContent.class);
                            if (QrCodeContent.QR_CODE_TYPE_USER.equals(qrCodeContent.getType())) {
                                startActivity(new Intent(getActivity(), UserInfoActivity.class).
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

    /**
     * 动态权限
     */
    public void requestPerms(Activity activity, String[] permissions, int requestCode) {
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
                    case MainActivity.REQUEST_CODE_CAMERA:
                        startScanActivity();
                        break;
                    case MainActivity.REQUEST_CODE_LOCATION:
                        startPeopleNearbyActivity();
                        break;
                }

            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                requestPermissions(requestPermissions, requestCode);
            }
        }
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
            switch (requestCode) {
                case MainActivity.REQUEST_CODE_CAMERA:
                    startScanActivity();
                    break;
                case MainActivity.REQUEST_CODE_LOCATION:
                    startPeopleNearbyActivity();
                    break;
            }
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(getActivity(), permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            String content = "";
            // 非初次进入App且已授权
            switch (requestCode) {
                case MainActivity.REQUEST_CODE_CAMERA:
                    content = getString(R.string.request_permission_camera);
                    break;
                case MainActivity.REQUEST_CODE_LOCATION:
                    content = getString(R.string.request_permission_location);
                    break;
            }
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(getActivity(), "权限申请",
                    content,
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

    /**
     * 进入扫一扫页面
     */
    private void startScanActivity() {
        Intent intent = new Intent(getActivity(), CaptureActivity2.class);
        intent.putExtra(CaptureActivity2.USE_DEFUALT_ISBN_ACTIVITY, true);
        startActivityForResult(intent, MainActivity.REQUEST_CODE_SCAN);
    }

    /**
     * 进入附近的人列表页
     */
    private void startPeopleNearbyActivity() {
        final ConfirmDialog mConfirmDialog = new ConfirmDialog(getActivity(), getString(R.string.tips),
                getString(R.string.open_people_nearby_tips),
                getString(R.string.ok), getString(R.string.cancel), getActivity().getColor(R.color.navy_blue));
        mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
            @Override
            public void onOkClick() {
                mConfirmDialog.dismiss();
                // 开启足迹
                PreferencesUtil.getInstance().setOpenPeopleNearby(true);

                startActivity(new Intent(getActivity(), PeopleNearbyActivity.class));
            }

            @Override
            public void onCancelClick() {
                mConfirmDialog.dismiss();
            }
        });
        // 点击空白处消失
        mConfirmDialog.setCancelable(true);
        mConfirmDialog.show();
    }

    @Override
    public void onClick(View view) {
        String[] permissions;
        switch (view.getId()) {
            case R.id.rl_moments:
                startActivity(new Intent(getActivity(), MomentsActivity.class));
                break;
            case R.id.rl_scan:
                permissions = new String[]{"android.permission.CAMERA"};
                requestPerms(getActivity(), permissions, MainActivity.REQUEST_CODE_CAMERA);
                break;
            case R.id.rl_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.rl_people_nearby:
                // 动态申请定位权限
                permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION"};
                requestPerms(getActivity(), permissions, MainActivity.REQUEST_CODE_LOCATION);
                break;
        }
    }
}
