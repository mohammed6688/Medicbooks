package com.mocomp.developer.medicbooks.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.data.constant.AppConstant;
import com.mocomp.developer.medicbooks.utility.AdsUtilities;
import com.mocomp.developer.medicbooks.utility.AppUtilities;
import com.mocomp.developer.medicbooks.utility.FilePickerUtilities;
import com.mocomp.developer.medicbooks.utility.PermissionUtilities;
import com.mocomp.developer.medicbooks.webengine.WebEngine;
import com.mocomp.developer.medicbooks.listeners.WebListener;


public class CustomUrlActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;
    private String mPageTitle, mPageUrl;

    private WebView mWebView;
    private WebEngine mWebEngine;

    private boolean mFromPush = false;
    TextView terms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        //initFunctionality();
    }

    private void initVar() {
        mActivity = CustomUrlActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            mPageTitle = intent.getStringExtra(AppConstant.BUNDLE_KEY_TITLE);
            mPageUrl = intent.getStringExtra(AppConstant.BUNDLE_KEY_URL);
            mFromPush = intent.getBooleanExtra(AppConstant.BUNDLE_FROM_PUSH, false);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_custom_url);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Cairo-Regular.ttf");
        terms=findViewById(R.id.terms);
        terms.setText("باستخدامك لهذا التطبيق فانك توافق على أن هذا التطبيق مصمم للارشادات الطبية العامة ورفع درجة الوعي الصحي .ولا يغني عن مراجعة الطبيب في العيادة والكشف الطبي .وقد تختلف الارشادات حسب مايدلي به المريض من معلومات تعبر عن حالة افتراضية  معينة دون اي  مسؤولية قانونية اوصحية تنتج من استخدامات اخرى لهذا التطبيق\n\n نقوم بجمع المعلومات لتقديم خدمات أفضل لجميع مستخدمينا. المعلومات التي نحصل عليها عنك من خدمات أخرى مثل Google Analytics و Firebase و Crashlytics و Google Play وغيرها من الخدمات التي نستخدمها لتحسين تطبيقاتنا وخدماتنا. ");
        terms.setTypeface(typeface);
        initWebEngine();

        initLoader();
        initToolbar(true);
        setToolbarTitle(mPageTitle);
        enableUpButton();
    }


    public void initWebEngine() {

        mWebView = (WebView) findViewById(R.id.web_view);

        mWebEngine = new WebEngine(mWebView, mActivity);

        /*mWebEngine.initWebView();


        mWebEngine.initListeners(new WebListener() {
            @Override
            public void onStart() {
                showLoader();
            }

            @Override
            public void onLoaded() {
                hideLoader();
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onNetworkError() {
                showEmptyView();
            }

            @Override
            public void onPageTitle(String title) {
            }
        });

         */


    }

    private void initFunctionality() {

        mWebEngine.loadPage(mPageUrl);

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtilities.isPermissionResultGranted(grantResults)) {
            if (requestCode == PermissionUtilities.REQUEST_WRITE_STORAGE_UPLOAD) {
                if (mWebEngine != null) {
                    mWebEngine.invokeImagePickerActivity();
                }
            } else if (requestCode == PermissionUtilities.REQUEST_WRITE_STORAGE_DOWNLOAD) {
                if (mWebEngine != null) {
                    mWebEngine.downloadFile();
                }
            }
        } else {
            AppUtilities.showToast(mActivity, getString(R.string.permission_not_granted));
        }

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (reqCode == WebEngine.KEY_FILE_PICKER) {
                String picturePath = FilePickerUtilities.getPickedFilePath(this, data);
                if (mWebEngine != null) {
                    mWebEngine.uploadFile(data, picturePath);
                } else {
                    AppUtilities.showToast(mContext, getString(R.string.failed));
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (mWebEngine != null) {
                mWebEngine.cancelUpload();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goToHome();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        goToHome();
    }

    private void goToHome() {
        if (mFromPush) {
            Intent intent = new Intent(CustomUrlActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }
}
