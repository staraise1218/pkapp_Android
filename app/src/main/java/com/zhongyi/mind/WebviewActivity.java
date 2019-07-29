package com.zhongyi.mind;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends Activity {
    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        //设置activity
        ((MyApplication)getApplication()).setActivities(this);

        webview = findViewById(R.id.webview);
        String url =  getIntent().getStringExtra("url");
        webview.clearCache(true);
        webview.loadUrl(url);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        /***打开本地缓存提供JS调用**/
        webview.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);

        webview.addJavascriptInterface(new Object() {
            //定义要调用的方法
            //msg由js调用的时候传递
            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void back() {
                WebviewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (webview.canGoBack()) {
                                webview.goBack();
                            }else{
                                WebviewActivity.this.finish();
                            }
                        } catch (Exception e) {
                            WebviewActivity.this.finish();
                        }
                    }
                });
            }
        }, "back");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根
            }

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {

                return true;
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

            }
        });

    }
}
