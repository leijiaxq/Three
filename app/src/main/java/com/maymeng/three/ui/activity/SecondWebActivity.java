package com.maymeng.three.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.maymeng.three.R;
import com.maymeng.three.api.RxBus;
import com.maymeng.three.base.RxBaseActivity;
import com.maymeng.three.bean.DataBean2;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;

/**
 * Create by  leijiaxq
 * Date       2017/3/8 17:16
 * Describe
 */

public class SecondWebActivity extends RxBaseActivity {
    @BindView(R.id.web_view)
    WebView mWebView;
    private String mUrl;

    private static final String TAG = "SecondWebActivity";
//    private OkHttpClient mClient;


    @Override
    public int getLayoutId() {
        return R.layout.activity_second_web;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mUrl = getIntent().getStringExtra("url");

    }

    @Override
    public void initToolBar() {

    }


    @Override
    public void loadData() {
        WebSettings webSetting = mWebView.getSettings();

        webSetting.setJavaScriptEnabled(true);
        webSetting.setSupportMultipleWindows(true);


//        webSetting.setUserAgentString(Constant.UA);


//        DocumentsContract.Document document = Jsoup.connect(mUrl).get();
//        document.getElementsByClass("header-container").remove();
//        document.getElementsByClass("footer").remove();
//        WebSettings ws = mWebView.getSettings();
//        ws.setJavaScriptEnabled(true);
//        //mWebView.loadData(document.toString(),"text/html","utf-8");
//        mWebView.loadDataWithBaseURL(mUrl,document.toString(),"text/html","utf-8","");


//        mClient = new OkHttpClient();

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

//                Map<String, String> mMap = new HashMap<>();
//                mMap.put("X-Requested-With", BaseApplication.getInstance().getPackageName());
//                view.loadUrl(url, mMap);


//                android.webkit.WebView webView = new android.webkit.WebView(SecondWebActivity.this);
//                webView.loadUrl();

                view.loadUrl(url);
                return true;
            }
/*

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                // TODO Here you must overwrite request  using your
                // HttpClient Request
                // and pass it to new WebResourceResponse
                return new  WebResourceResponse(WebResourceResponse.ContentType, response.ContentEncoding, responseStream);
            }
*/

/*
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {


                Map<String, String> requestHeaders = webResourceRequest.getRequestHeaders();
                String s = requestHeaders.get("X-Requested-With");
                if (s !=null) {
                    int i = 0;
                }


                return super.shouldInterceptRequest(webView, webResourceRequest);
            }*/

        });



        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Context context = view.getContext();
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);*/

                Intent intent = new Intent(SecondWebActivity.this, SecondWebActivity.class);
                intent.putExtra("url", data);
                context.startActivity(intent);

                return false;
            }






        });

        if (!TextUtils.isEmpty(mUrl)) {
//            Map<String, String> mMap = new HashMap<>();
//            mMap.put("X-Requested-With", BaseApplication.getInstance().getPackageName());
//            mWebView.loadUrl(mUrl, mMap);
            mWebView.loadUrl(mUrl);
        }
    }




    //清除cookie
    private void clearCookie() {
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.clearCache(true);

        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            clearCookie();
            startActivity(new Intent(SecondWebActivity.this, MainActivity.class));

            RxBus.getDefault().post(new DataBean2());
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();// 返回前一个页面
            } else {
//                clearCookie();

                RxBus.getDefault().post(new DataBean2());

                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        //        WebView scrollView = (WebView) findViewById(R.id.ch01);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    mWebView.pageUp(false);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    mWebView.pageDown(false);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

}
