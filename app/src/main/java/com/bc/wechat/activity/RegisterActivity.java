package com.bc.wechat.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.TimeoutError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.enums.ResponseMsg;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.FileUtil;
import com.bc.wechat.utils.MD5Util;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

import static com.bc.wechat.utils.ValidateUtil.validatePassword;

/**
 * 注册
 *
 * @author zhou
 */
public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";
    public static int sequence = 1;

    private VolleyUtil mVolleyUtil;

    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.iv_agreement)
    ImageView mAgreementIv;

    @BindView(R.id.tv_agreement)
    TextView mAgreementTv;

    @BindView(R.id.et_nick_name)
    EditText mNickNameEt;

    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    @BindView(R.id.et_password)
    EditText mPasswordEt;

    @BindView(R.id.btn_register)
    Button mRegisterBtn;

    LoadingDialog mDialog;
    UserDao mUserDao;

    private static final int UPDATE_AVATAR_BY_TAKE_CAMERA = 1;
    private static final int UPDATE_AVATAR_BY_ALBUM = 2;

    private String mImageName;
    private String mUserAvatar;
    /**
     * 是否同意协议
     */
    private boolean mIsAgree = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initStatusBar();

        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(RegisterActivity.this);
        mUserDao = new UserDao();
        initView();
    }

    private void initView() {
        String agreement = "<font color=" + "\"" + "#AAAAAA" + "\">" + "已阅读并同意" + "</font>" + "<u>"
                + "<font color=" + "\"" + "#576B95" + "\">" + "《软件许可及服务协议》"
                + "</font>" + "</u>";
        mAgreementTv.setText(Html.fromHtml(agreement));

        mNickNameEt.addTextChangedListener(new TextChange());
        mPhoneEt.addTextChangedListener(new TextChange());
        mPasswordEt.addTextChangedListener(new TextChange());
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.sdv_avatar, R.id.iv_agreement, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sdv_avatar:
                showPhotoDialog();
                break;
            case R.id.iv_agreement:
                if (mIsAgree) {
                    mAgreementIv.setBackgroundResource(R.mipmap.icon_choose_false);
                    mIsAgree = false;
                } else {
                    mAgreementIv.setBackgroundResource(R.mipmap.icon_choose_true);
                    mIsAgree = true;
                }
                checkSubmit();
                break;
            case R.id.btn_register:
                mDialog.setMessage(getString(R.string.registering));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                String nickName = mNickNameEt.getText().toString();
                String phone = mPhoneEt.getText().toString();
                String password = mPasswordEt.getText().toString();
                if (!validatePassword(password)) {
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, R.string.password_rules,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                register(nickName, phone, password, mUserAvatar);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_AVATAR_BY_TAKE_CAMERA:
                    final File file = new File(Environment.getExternalStorageDirectory(), mImageName);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", file.getPath());
                            if (null != imageList && imageList.size() > 0) {
                                mUserAvatar = imageList.get(0);
                            }
                        }
                    }).start();
                    mAvatarSdv.setImageURI(Uri.fromFile(file));
                    break;
                case UPDATE_AVATAR_BY_ALBUM:
                    if (data != null) {
                        Uri uri = data.getData();
                        final String filePath = FileUtil.getFilePathByUri(this, uri);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", filePath);
                                if (null != imageList && imageList.size() > 0) {
                                    mUserAvatar = imageList.get(0);
                                }
                            }
                        }).start();

                        mAvatarSdv.setImageURI(uri);
                    }
                    break;
            }
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkSubmit();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    /**
     * 显示修改头像对话框
     */
    private void showPhotoDialog() {
        final AlertDialog photoDialog = new AlertDialog.Builder(this).create();
        photoDialog.show();
        Window window = photoDialog.getWindow();
        window.setContentView(R.layout.dialog_alert_abandon);
        TextView mTakePicTv = window.findViewById(R.id.tv_content1);
        TextView mAlbumTv = window.findViewById(R.id.tv_content2);
        mTakePicTv.setText("拍照");
        mTakePicTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageName = CommonUtil.generateId() + ".png";
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                        new File(Environment.getExternalStorageDirectory(), mImageName)));
                startActivityForResult(cameraIntent, UPDATE_AVATAR_BY_TAKE_CAMERA);
                photoDialog.dismiss();
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
     * 注册
     *
     * @param nickName 昵称
     * @param phone    手机号
     * @param password 密码
     */
    private void register(String nickName, String phone, String password, String avatar) {
        String url = Constant.BASE_URL + "users";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("nickName", nickName);
        paramMap.put("phone", phone);
        paramMap.put("password", MD5Util.encode(password, "utf8"));
        if (!TextUtils.isEmpty(avatar)) {
            paramMap.put("avatar", avatar);
        }

        mVolleyUtil.httpPostRequest(url, paramMap, response -> {
            mDialog.dismiss();
            Log.d(TAG, "server response: " + response);
            final User user = JSON.parseObject(response, User.class);
            Log.d(TAG, "userId:" + user.getUserId());
            // 登录成功后设置user和isLogin至sharedpreferences中
            PreferencesUtil.getInstance().setUser(user);
            PreferencesUtil.getInstance().setLogin(true);
            // 注册jpush
            JPushInterface.setAlias(RegisterActivity.this, sequence, user.getUserId());
            List<User> friendList = user.getContactList();
            if (null != friendList && friendList.size() > 0) {
                for (User userFriend : friendList) {
                    if (null != userFriend) {
                        userFriend.setIsFriend(Constant.IS_FRIEND);
                        mUserDao.saveUser(userFriend);
                    }
                }
            }

            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
            // 登录极光im
            JMessageClient.login(user.getUserId(), user.getUserImPassword(), new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage) {
                    Log.d(TAG, "responseCode: " + responseCode + ", responseMessage: " + responseMessage);
                }
            });

        }, volleyError -> {
            mDialog.dismiss();
            if (volleyError instanceof NetworkError) {
                Toast.makeText(RegisterActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                return;
            } else if (volleyError instanceof TimeoutError) {
                Toast.makeText(RegisterActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                return;
            }

            int errorCode = volleyError.networkResponse.statusCode;
            switch (errorCode) {
                case 400:
                    Map<String, String> headers = volleyError.networkResponse.headers;
                    String responseCode = headers.get("responseCode");
                    if (ResponseMsg.USER_EXISTS.getResponseCode().equals(responseCode)) {
                        Toast.makeText(RegisterActivity.this,
                                R.string.user_exists, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                R.string.account_or_password_error, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        });
    }

    /**
     * 表单是否填充完成(昵称,手机号,密码,是否同意协议)
     */
    private void checkSubmit() {
        boolean nickNameHasText = mNickNameEt.getText().toString().length() > 0;
        boolean phoneHasText = mPhoneEt.getText().toString().length() > 0;
        boolean passwordHasText = mPasswordEt.getText().toString().length() > 0;
        if (nickNameHasText && phoneHasText && passwordHasText && mIsAgree) {
            // 注册按钮可用
            mRegisterBtn.setBackgroundColor(getColor(R.color.register_btn_bg_enable));
            mRegisterBtn.setTextColor(getColor(R.color.register_btn_text_enable));
            mRegisterBtn.setEnabled(true);
        } else {
            // 注册按钮不可用
            mRegisterBtn.setBackgroundColor(getColor(R.color.register_btn_bg_disable));
            mRegisterBtn.setTextColor(getColor(R.color.register_btn_text_disable));
            mRegisterBtn.setEnabled(false);
        }
    }
}
