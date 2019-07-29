package com.zhongyi.mind;

import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.mind.contant.NetConstant;
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

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText et_phone;
    private EditText et_password;
    private TextView tv_login_register;
    private TextView tv_login_forget;
    private Button loginBtn;

    private User user;
    private Handler handler = new Handler();
    private String phone;
    private String pwd;
    private ImageView yanjing;

    private boolean isShow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isLogin = (boolean) SharedPreferencesUtils.getParam("isLogin",false);
        if (isLogin){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }else{
            setContentView(R.layout.activity_login);

            et_phone = findViewById(R.id.et_phone);
            et_password = findViewById(R.id.et_password);
            loginBtn = findViewById(R.id.btn_login);
            loginBtn.setOnClickListener(this);

            tv_login_register = findViewById(R.id.tv_login_register);
            tv_login_register.setOnClickListener(this);
            tv_login_forget = findViewById(R.id.tv_login_forget);
            tv_login_forget.setOnClickListener(this);
            yanjing = findViewById(R.id.yanjing);
            yanjing.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:{
                phone = et_phone.getText().toString().trim();
                pwd = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||phone.length()!=11){
                    Toast.makeText(LoginActivity.this,"请输入正确的手机号", Toast.LENGTH_LONG).show();
                }else {
                    if (TextUtils.isEmpty(pwd)){
                        Toast.makeText(LoginActivity.this,"请输入正确的密码", Toast.LENGTH_LONG).show();
                    }else{
                        login(phone,pwd);
                    }
                }
            }
            break;
            case R.id.tv_login_register:{
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
                break;
            case R.id.tv_login_forget:{
                Intent intent = new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
                break;
            case R.id.yanjing:{
                if (!isShow) {
                    //如果选中，显示密码
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShow = true;
                } else {
                    //否则隐藏密码
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShow = false;
                }
            }
                break;
        }
    }

    private void login(String phone, String pwd) {
        String url = NetConstant.BASE_URL + NetConstant.LOGIN;
        //设置回调方法
        NetUtils.setCallback(loginCallback);
        Map<String, String> keymap = new HashMap<>();
        keymap.put("mobile",phone);
        keymap.put("password",pwd);
        //获取网络数据
        NetUtils.postDataWithParame(url, keymap);
    }

    //登录回调
    Callback loginCallback = new Callback() {
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
                    Toast.makeText(LoginActivity.this, tempBean.getMsg(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
           //登录成功
            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
            SharedPreferencesUtils.setParam("userId",user.getUser_id());
            SharedPreferencesUtils.setParam("isLogin",true);
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    };

}
