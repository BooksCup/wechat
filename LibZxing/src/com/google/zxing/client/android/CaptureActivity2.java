package com.google.zxing.client.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ISBNActivity;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.encode.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.util.ProjectorUtil;

import java.util.Hashtable;
import java.util.Vector;

public class CaptureActivity2 extends CaptureActivity implements OnEditorActionListener {

    public static final String USE_DEFUALT_ISBN_ACTIVITY = "use_defualt_isbn_activity";
    public static final String MANUAL_INPUT_TITLE = "manual_input_title";
    public static final String EXTRA_SHOW_INPUT_VIEW = "show_input_view";
    public static final int REQUEST_OPEN_GALLERY = 0xFF15;
    public static final int OPEN_ISBN_ACTIVITY = 0xFF16;

    private static final int REQUST_WIDTH = 1280;
    private static final int REQUST_HEIGHT = 1080;
    //	private Button btnInput;
//	private Button btnFinish;
//	private RelativeLayout rlFlash;
    private TextView tvInput, tvCancel, tvFlash;
    private boolean hasFlashLight = false;
    private Camera camera = null;
    private boolean torchEnabled = false;

    private LinearLayout viewManualInputISBN;
    private FrameLayout viewCaptureISBN;
    private LinearLayout titleBar;
    private EditText etIsbn;
    private TextView tvClose;
    private ImageView ivClose;
    private Button btnSearch;
    private InputMethodManager imm;
    // private Animation slideInBottomAnim;
    private Animation slideOutBottomAnim;
    private Animation animIn;
    private Animation animOut;
    private SurfaceView preview_view;
    private TextView tvAlbum;
    private ProgressDialog mProgress;
    private Handler mHandler;
    private int inputUnable, lightUnable, albumUnable, cancleUnable;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        preview_view = findViewById(R.id.preview_view);
        tvInput = findViewById(R.id.tvInput);
        tvCancel = findViewById(R.id.tvCancel);
        tvFlash = findViewById(R.id.tvFlash);
        tvAlbum = findViewById(R.id.tvAlbum);
        viewCaptureISBN = findViewById(R.id.captureIsbn);
        titleBar = findViewById(R.id.llTitleBar);
        viewManualInputISBN = findViewById(R.id.manualInputIsbn);
        etIsbn = findViewById(R.id.etISBN);
        tvClose = findViewById(R.id.tvClose);
        btnSearch = findViewById(R.id.btnSearch);
        ivClose = findViewById(R.id.ivClose);
        Intent intent = getIntent();
        boolean showInputView = intent.getBooleanExtra(EXTRA_SHOW_INPUT_VIEW, true);
        if (!showInputView) {
            tvInput.setVisibility(View.GONE);
        }
        //manualInputTitle = intent.getStringExtra(MANUAL_INPUT_TITLE);
        inputUnable = intent.getIntExtra("inputUnable", 0);
        lightUnable = intent.getIntExtra("lightUnable", 0);
        albumUnable = intent.getIntExtra("albumUnable", 0);
        cancleUnable = intent.getIntExtra("cancleUnable", 0);
        if (inputUnable != 0) {
            tvInput.setVisibility(View.GONE);
        }
        if (lightUnable != 0) {
            tvFlash.setVisibility(View.GONE);
        }
        if (albumUnable != 0) {
            tvAlbum.setVisibility(View.GONE);
        }
        if (cancleUnable != 0) {
            tvCancel.setVisibility(View.GONE);
        }

        etIsbn.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etIsbn.setOnEditorActionListener(this);

        animIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        animOut = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        viewManualInputISBN.setVisibility(View.GONE);

        PackageManager pm = this.getPackageManager();
        hasFlashLight = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        Log.v("wsg", "hasFlashLight ?? " + hasFlashLight);

        tvInput.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (getIntent().getBooleanExtra(USE_DEFUALT_ISBN_ACTIVITY, false)) {
                    inputIsbnViewIn();
                } else {
                    Intent data = new Intent();
                    data.putExtra("inputIsbn", true);
                    setResult(RESULT_OK, data);
                    finish();
                }

            }
        });
        tvFlash.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onFlash();
            }
        });
        tvCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputIsbnViewOut(true);
            }
        });
        tvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputIsbnViewOut(false);
                imm.hideSoftInputFromWindow(etIsbn.getWindowToken(), 0);
            }
        });
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(toGalleryIntent, REQUEST_OPEN_GALLERY);
            }
        });

        resetInputDialogTitle();
        mHandler = new Handler();

    }

    private void resetInputDialogTitle() {
        String inputTitle = getIntent().getStringExtra(MANUAL_INPUT_TITLE);
        if (inputTitle != null) {
//            TextView tvInputTitle = findViewById(R.id.tvTitle);
//            tvInputTitle.setText(inputTitle);
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
//        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom_alpha);
    }

    @Override
    protected void setLayout() {
        setContentView(R.layout.capture2);
    }

    private void inputIsbnViewIn() {
        animOut.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivityForResult(new Intent(CaptureActivity2.this, ISBNActivity.class), OPEN_ISBN_ACTIVITY);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                viewCaptureISBN.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewCaptureISBN.startAnimation(animOut);

    }

    private void inputIsbnViewOut(final boolean isSearch) {
        if (viewManualInputISBN.getVisibility() != View.VISIBLE)
            return;
        final String isbn = etIsbn.getText().toString();
        if (isbn.equals("") && isSearch) {
            Toast.makeText(this, "请输入ISBN号", Toast.LENGTH_SHORT).show();
            return;
        }
        animOut.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewManualInputISBN.setVisibility(View.GONE);
                if (isSearch) {
                    isbnSearch(isbn);
                } else {
                    finish();
                }
            }
        });
        viewManualInputISBN.startAnimation(animOut);
        imm.hideSoftInputFromWindow(etIsbn.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewfinderView().setOnFlashLightStateChangeListener(new ViewfinderView.onFlashLightStateChangeListener() {
            @Override
            public void openFlashLight(boolean open) {
                turnOnFlashLight(open);
                getViewfinderView().reOnDraw();
            }
        });
    }

    private void isbnSearch(String isbn) {
        Intent intent = new Intent();
        intent.putExtra("CaptureIsbn", isbn);
        intent.putExtra("type", "input");
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void onFlash() {
        // 带闪光灯
        if (hasFlashLight) {
            if (torchEnabled) {
                if (camera == null) {
                    camera = CameraManager.getCamera();
                }
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                tvFlash.setText(R.string.flash);
                torchEnabled = !torchEnabled;
            } else {
                try {
                    if (camera == null) {
                        camera = CameraManager.getCamera();
                    }
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    tvFlash.setText(R.string.flash);
                    torchEnabled = !torchEnabled;
                } catch (Exception e) {
                    Toast.makeText(CaptureActivity2.this, "您的设备不支持闪光灯", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(CaptureActivity2.this, "您的设备不支持闪光灯", Toast.LENGTH_SHORT).show();
        }
    }

    protected void turnOnFlashLight(boolean open) {
        // 带闪光灯
        if (hasFlashLight) {
            if (!open) {
                if (camera == null) {
                    camera = CameraManager.getCamera();
                }
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                // tvFlash.setText(R.string.flash);
            } else {
                try {
                    if (camera == null) {
                        camera = CameraManager.getCamera();
                    }
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    // tvFlash.setText(R.string.flash);
                } catch (Exception e) {
                    Toast.makeText(CaptureActivity2.this, "您的设备不支持闪光灯", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(CaptureActivity2.this, "您的设备不支持闪光灯", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (viewManualInputISBN.getVisibility() == View.VISIBLE) {
            inputIsbnViewOut(false);
            imm.hideSoftInputFromWindow(etIsbn.getWindowToken(), 0);
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
                || (event != null && event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            inputIsbnViewOut(true);
        }
        return true;
    }

    @Override
    protected void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.barcode_canner_title2));
        builder.setMessage(String.format(getString(R.string.msg_camera_framework_bug2), getString(R.string.this_app_name)));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        AlertDialog dialog = builder.show();
        ProjectorUtil.getInstance().addDialog(dialog);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_GALLERY) {
            if (data == null) return;
            Uri selectedImageUri = data.getData();
            scanImgTask(selectedImageUri);
        } else if (requestCode == OPEN_ISBN_ACTIVITY) {
            if (data == null) return;
            viewCaptureISBN.setVisibility(View.VISIBLE);
            String isbn = data.getStringExtra("isbn");
            if (!TextUtils.isEmpty(isbn)) {
                isbnSearch(isbn);
            }
            if (null != imm) {
                imm.hideSoftInputFromWindow(etIsbn.getWindowToken(), 0);
            }
        }
    }

    private void scanImgTask(final Uri selectedImageUri) {
        mProgress = new ProgressDialog(this, R.style.theme_customer_progress_dialog);
        mProgress.setMessage("正在扫描...");
        mProgress.setCancelable(false);
        mProgress.show();
        ProjectorUtil.getInstance().addDialog(mProgress);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = CaptureActivity2.this.getContentResolver().query(selectedImageUri, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                final Result result = scanningImage(picturePath);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgress != null && mProgress.isShowing())
                            mProgress.dismiss();
                        if (result == null) {
                            final AlertDialog dialog = new AlertDialog.Builder(CaptureActivity2.this)
                                    .setTitle(R.string.tip)
                                    .setMessage(R.string.scan_empty)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                            dialog.show();
                            ProjectorUtil.getInstance().addDialog(dialog);
                        } else {
                            CaptureActivity2.this.switchActivity(result.getText());
                        }
                    }
                }, 1000);
            }
        }).start();

    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        Bitmap scanBitmap = getScanBitmap(path);
        if (scanBitmap == null) return null;
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getScanBitmap(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap scanBitmap = BitmapFactory.decodeFile(photoPath, options);
        final int height = options.outHeight;
        final int width = options.outWidth;
        int sampleSize = 1;
        if (height > REQUST_HEIGHT || width > REQUST_WIDTH) {
            int heightRatio = Math.round((float) height
                    / (float) REQUST_HEIGHT);
            int widthRatio = Math.round((float) width / (float) REQUST_WIDTH);
            sampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        scanBitmap = BitmapFactory.decodeFile(photoPath, options);
        return scanBitmap;
    }
}
