package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.utils.VolleyUtil;

public class LoginActivity extends FragmentActivity {
    private VolleyUtil volleyUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        volleyUtil = VolleyUtil.getInstance(this);
        login("13770519290", "1");
    }


    private void login(String phone, String password) {
        String url = Constant.BASE_URL + "users/login?phone=" + phone + "&password=" + password;
        volleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int errorCode = volleyError.networkResponse.statusCode;
                Toast.makeText(LoginActivity.this, errorCode + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
