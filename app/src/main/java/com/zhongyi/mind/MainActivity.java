package com.zhongyi.mind;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zhongyi.mind.adapter.DynamicAdapter;
import com.zhongyi.mind.contant.NetConstant;
import com.zhongyi.mind.data.Artical;
import com.zhongyi.mind.data.BannerBean;
import com.zhongyi.mind.data.DynamicBean;
import com.zhongyi.mind.data.HomeData;
import com.zhongyi.mind.data.TempHomeBean;
import com.zhongyi.mind.util.GlideImageLoader;
import com.zhongyi.mind.util.LogUtils;
import com.zhongyi.mind.util.NetCode;
import com.zhongyi.mind.util.NetUtils;
import com.zhongyi.mind.util.SharedPreferencesUtils;
import com.zhongyi.mind.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener,RecyclerArrayAdapter.OnItemClickListener {
    private Banner banner;
    private TextView pk_paihang;
    //private ImageView pk_paiwei;
    private TextView pk_hot_more;
    private EasyRecyclerView recyclerView;
    private WebView hide_webview;

    private ImageView home;
    private ImageView pk;
    private ImageView team;
    private ImageView mine;

    private HomeData homeData;
    private Handler handler = new Handler();
    private DynamicAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置activity
        ((MyApplication)getApplication()).setActivities(this);
        pk_paihang = findViewById(R.id.pk_paihang);
        //pk_paiwei = findViewById(R.id.pk_paiwei);
        pk_paihang.setOnClickListener(this);
        //pk_paiwei.setOnClickListener(this);
        pk_hot_more = findViewById(R.id.pk_hot_more);
        pk_hot_more.setOnClickListener(this);
        hide_webview = findViewById(R.id.hide_webview);

        home= findViewById(R.id.home);
        pk= findViewById(R.id.pk);
        team= findViewById(R.id.team);
        mine= findViewById(R.id.mine);

        home.setOnClickListener(this);
        pk.setOnClickListener(this);
        team.setOnClickListener(this);
        mine.setOnClickListener(this);


        banner = (Banner) findViewById(R.id.banner);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());

        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (homeData.getBannerList().size() > 0) {
                    BannerBean banner = homeData.getBannerList().get(position);
                    if (!TextUtils.isEmpty(banner.getAd_link())) {
                        String url = banner.getAd_link();
                        //String url =NetConstant.BASE_URL_WEB+"1";
                        Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
                        intent.putExtra("uri", url);
                        //intent.putExtra("title", "广告详情");
                        startActivity(intent);
                    }
                }
            }
        });

        //医生列表
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new DynamicAdapter(this));
        SpaceDecoration itemDecoration = new SpaceDecoration((int) Utils.convertDpToPixel(8, MainActivity.this));
        recyclerView.addItemDecoration(itemDecoration);

        adapter.setOnItemClickListener(MainActivity.this);

        //写刷新事件
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                adapter.clear();
                //刷新事件

                getData();
            }
        });

        //设置隐藏页面内容
        setHideView();
        //获取首页数据
        getData();
    }

    private void setHideView() {
        int userId = (int) SharedPreferencesUtils.getParam("userId",-1);
        hide_webview.loadUrl(NetConstant.HIDE+userId);

        WebSettings settings = hide_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        /***打开本地缓存提供JS调用**/
        hide_webview.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        hide_webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);


        hide_webview.setWebViewClient(new WebViewClient() {
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
        hide_webview.setWebChromeClient(new WebChromeClient() {

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
        switch (v.getId()){
            case R.id.pk_paihang:{
                Intent intent = new Intent(MainActivity.this,WebviewActivity.class);
                intent.putExtra("url", NetConstant.PAIHANG);
                startActivity(intent);
            }
            break;
            case R.id.pk_hot_more:{
                Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
                String url = NetConstant.KNOWLEDGE_LIST;
                intent.putExtra("url", url);
                startActivity(intent);
            }
            break;
            case R.id.home:
                break;
            case R.id.pk:{
                Intent intent = new Intent(MainActivity.this,PKActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
                break;
            case R.id.team:{
                Intent intent = new Intent(MainActivity.this,TeamActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
                break;
            case R.id.mine:{
                Intent intent = new Intent(MainActivity.this,UserActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
                break;
        }
    }

    //首页数据
    private void getData() {
        String url = NetConstant.BASE_URL + NetConstant.HOME;
        //设置回调方法
        NetUtils.setCallback(homeCallback);
        Map<String, String> keymap = new HashMap<>();
        //获取网络数据
        NetUtils.postDataWithParame(url, keymap);
    }

    //登录回调
    Callback homeCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                TempHomeBean tempBean = JSON.parseObject(response.body().string(), TempHomeBean.class);
                if (tempBean.getCode() == 200) {
                    homeData = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, tempBean.getMsg(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (homeData != null) {
                List<String> images = new ArrayList<>();
                if (homeData.getBannerList().size() > 0) {
                    for (BannerBean banner : homeData.getBannerList()) {
                        images.add(NetConstant.BASE_IMGE_URL + banner.getAd_code());
                    }
                }

                //设置图片集合
                banner.setImages(images);
                //banner设置方法全部调用完毕时最后调用
                banner.start();

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (homeData.getArticleList()!=null&&homeData.getArticleList().size()>0) {
                            adapter.clear();
                            adapter.addAll(homeData.getArticleList());
                        }
                    }
                }, 100);
            }
        }
    };

    @Override
    public void onItemClick(int position) {
        Artical dynamicBean = homeData.getArticleList().get(position);
        if (dynamicBean!=null) {
            Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
            String url = NetConstant.KNOWLEDGE+dynamicBean.getArticle_id();
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }
}
