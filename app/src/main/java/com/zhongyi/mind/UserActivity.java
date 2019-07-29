package com.zhongyi.mind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhongyi.mind.contant.NetConstant;
import com.zhongyi.mind.control.GlideCircleTransform;
import com.zhongyi.mind.data.User;
import com.zhongyi.mind.data.temp.UserTemp;
import com.zhongyi.mind.util.LogUtils;
import com.zhongyi.mind.util.NetCode;
import com.zhongyi.mind.util.NetUtils;
import com.zhongyi.mind.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserActivity extends Activity implements View.OnClickListener {
    private ImageView home;
    private ImageView pk;
    private ImageView team;
    private ImageView mine;
    private RelativeLayout qiandao_rv;
    private RelativeLayout zhishiku_rv;
    private RelativeLayout xinyuan_rv;
    private RelativeLayout shoucang_rv;
    private RelativeLayout banben_rv;
    private ImageView touxiang_img;
    private TextView golden_coins;
    private TextView name_tv;
    private TextView school_tv;

    private User user;
    private Handler handler = new Handler();
    private int user_id ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //设置activity
        ((MyApplication)getApplication()).setActivities(this);

        home= findViewById(R.id.home);
        pk= findViewById(R.id.pk);
        team= findViewById(R.id.team);
        mine= findViewById(R.id.mine);
        touxiang_img = findViewById(R.id.touxiang_img);
        qiandao_rv= findViewById(R.id.qiandao_rv);
        zhishiku_rv= findViewById(R.id.zhishiku_rv);
        xinyuan_rv= findViewById(R.id.xinyuan_rv);
        shoucang_rv= findViewById(R.id.shoucang_rv);
        banben_rv = findViewById(R.id.banben_rv);
        golden_coins = findViewById(R.id.golden_coins);
        name_tv = findViewById(R.id.name_tv);
        school_tv = findViewById(R.id.school_tv);

        home.setOnClickListener(this);
        pk.setOnClickListener(this);
        team.setOnClickListener(this);
        mine.setOnClickListener(this);
        qiandao_rv.setOnClickListener(this);
        zhishiku_rv.setOnClickListener(this);
        xinyuan_rv.setOnClickListener(this);
        shoucang_rv.setOnClickListener(this);
        banben_rv.setOnClickListener(this);
        touxiang_img.setOnClickListener(this);

        user_id = (int) SharedPreferencesUtils.getParam("userId",user_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取数据
        getData();
    }

    //首页数据
    private void getData() {
        String url = NetConstant.BASE_URL + NetConstant.GET_USER_INFO;
        //设置回调方法
        NetUtils.setCallback(userCallback);
        Map<String, String> keymap = new HashMap<>();
        keymap.put("user_id",user_id+"");
        //获取网络数据
        NetUtils.postDataWithParame(url, keymap);
    }

    //登录回调
    Callback userCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                UserTemp tempBean = JSON.parseObject(response.body().string(), UserTemp.class);
                if (tempBean.getCode() == 200) {
                    user = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(UserActivity.this, tempBean.getMsg(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (user != null) {
                golden_coins.setText(user.getGoldcoin());
                name_tv.setText(user.getNickname());
                school_tv.setText(user.getSchool());
                if (!TextUtils.isEmpty(user.getHead_pic())){
                    Glide.with(UserActivity.this).load(NetConstant.BASE_IMGE_URL+user.getHead_pic()).transform(new GlideCircleTransform(UserActivity.this)).into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource,
                                                    GlideAnimation<? super GlideDrawable> glideAnimation) {
                            touxiang_img.setImageDrawable(resource); //显示图片
                        }
                    });
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:{
                Intent intent = new Intent(UserActivity.this,MainActivity.class);
                startActivity(intent);
                UserActivity.this.finish();
            }
            break;
            case R.id.pk:{
                Intent intent = new Intent(UserActivity.this,PKActivity.class);
                startActivity(intent);
                UserActivity.this.finish();
            }
                break;
            case R.id.team:{
                Intent intent = new Intent(UserActivity.this,TeamActivity.class);
                startActivity(intent);
                UserActivity.this.finish();
            }
            break;
            case R.id.mine:
                break;
            case R.id.qiandao_rv:{
                Intent intent = new Intent(UserActivity.this, WebviewActivity.class);
                String url = NetConstant.SIGN;
                intent.putExtra("url", url);
                startActivity(intent);
            }
                break;
            case R.id.zhishiku_rv:{
                Intent intent = new Intent(UserActivity.this, WebviewActivity.class);
                String url = NetConstant.ZHI_SHI_DIAN;
                intent.putExtra("url", url);
                startActivity(intent);
            }
                break;
            case R.id.xinyuan_rv:{
                Intent intent = new Intent(UserActivity.this, WebviewActivity.class);
                String url = NetConstant.XIN_YUAN;
                intent.putExtra("url", url);
                startActivity(intent);
            }
                break;
            case R.id.shoucang_rv:{
                Intent intent = new Intent(UserActivity.this, WebviewActivity.class);
                String url = NetConstant.MY_COLLECT;
                intent.putExtra("url", url);
                startActivity(intent);
            }
                break;
            case R.id.banben_rv:{
                Intent intent = new Intent(UserActivity.this, SettingActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.touxiang_img:{
                Intent intent = new Intent(UserActivity.this,UserInfoActivity.class);
                startActivity(intent);
            }
                break;
        }
    }
}
