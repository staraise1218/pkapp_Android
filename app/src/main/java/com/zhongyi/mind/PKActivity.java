package com.zhongyi.mind;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.mind.contant.NetConstant;
import com.zhongyi.mind.control.SelectPicPopupWindow;
import com.zhongyi.mind.data.temp.UploadFileTemp;
import com.zhongyi.mind.util.CommonUtils;
import com.zhongyi.mind.util.LogUtils;
import com.zhongyi.mind.util.NetCode;
import com.zhongyi.mind.util.NetUtils;
import com.zhongyi.mind.util.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PKActivity extends Activity implements View.OnClickListener {
    private WebView webview;
    private ImageView home;
    private ImageView pk;
    private ImageView team;
    private ImageView mine;
    private LinearLayout tab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pk);
        //设置activity
        ((MyApplication)getApplication()).setActivities(this);

        webview = findViewById(R.id.webview);
        home = findViewById(R.id.home);
        pk = findViewById(R.id.pk);
        team = findViewById(R.id.team);
        mine = findViewById(R.id.mine);
        tab = findViewById(R.id.tab);

        home.setOnClickListener(this);
        pk.setOnClickListener(this);
        team.setOnClickListener(this);
        mine.setOnClickListener(this);


        String url = NetConstant.PK;
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
            public void hide() {
                tab.setVisibility(View.GONE);
            }

            @SuppressLint("JavascriptInterface")
            @JavascriptInterface
            public void show() {
                tab.setVisibility(View.VISIBLE);
            }
        }, "pk");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if(Build.VERSION.SDK_INT<26) {
//                    view.loadUrl(url);
//                }

                return false;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home: {
                Intent intent = new Intent(PKActivity.this, MainActivity.class);
                startActivity(intent);
                PKActivity.this.finish();
            }
            break;
            case R.id.pk:
                break;
            case R.id.team: {
                Intent intent = new Intent(PKActivity.this, TeamActivity.class);
                startActivity(intent);
                PKActivity.this.finish();
            }
            break;
            case R.id.mine: {
                Intent intent = new Intent(PKActivity.this, UserActivity.class);
                startActivity(intent);
                PKActivity.this.finish();
            }
            break;
        }
    }

    //使用Webview的时候，返回键没有重写的时候会直接关闭程序，这时候其实我们要其执行的知识回退到上一步的操作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
