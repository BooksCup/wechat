package com.google.zxing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.R;


/**
 * Created by sdj on 2017/11/16.
 */

public class ISBNActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private EditText etISBN;
    private InputMethodManager imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_input_isbn);
        initViews();
    }

    private void initViews() {
        etISBN = findViewById(R.id.etISBN);
        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputIsbnViewOut(true);
            }
        });
        findViewById(R.id.tvClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etISBN.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etISBN.setOnEditorActionListener(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
                || (event != null && event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            inputIsbnViewOut(true);
        }
        return true;
    }

    private void inputIsbnViewOut(final boolean isSearch) {
        final String isbn = etISBN.getText().toString();
        if (isbn.equals("") && isSearch) {
            Toast.makeText(this, "请输入ISBN号", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("isbn", isbn);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        if (null != imm) {
            imm.hideSoftInputFromWindow(etISBN.getWindowToken(), 0);
        }
    }
}
