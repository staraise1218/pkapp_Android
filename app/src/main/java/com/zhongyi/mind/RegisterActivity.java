package com.zhongyi.mind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhongyi.mind.contant.NetConstant;
import com.zhongyi.mind.data.User;
import com.zhongyi.mind.data.VerifCode;
import com.zhongyi.mind.data.temp.CodeTemp;
import com.zhongyi.mind.data.temp.UserTemp;
import com.zhongyi.mind.util.LogUtils;
import com.zhongyi.mind.util.NetCode;
import com.zhongyi.mind.util.NetUtils;

import java.io.IOException;
import java.nio.charset.CoderMalfunctionError;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends Activity implements View.OnClickListener {
    private EditText phone_et;
    private EditText validate_et;
    private EditText password_et;
    private EditText password_sure_et;
    private TextView tv_get_validate_code;
    private Button btn_register;
    private TextView login_tv;
    private ImageView show_bg;
    private ImageView show_2_bg;

    private VerifCode code;
    private Handler handler = new Handler();
    private User user;
    private boolean isShow = false;
    private boolean isShow_2 = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        phone_et = findViewById(R.id.phone_et);
        validate_et = findViewById(R.id.validate_et);
        password_et = findViewById(R.id.password_et);
        password_sure_et = findViewById(R.id.password_sure_et);
        tv_get_validate_code = findViewById(R.id.tv_get_validate_code);
        tv_get_validate_code.setOnClickListener(this);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        login_tv = findViewById(R.id.login_tv);
        login_tv.setOnClickListener(this);

        show_bg = findViewById(R.id.show_bg);
        show_2_bg = findViewById(R.id.show_2_bg);
        show_bg.setOnClickListener(this);
        show_2_bg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_get_validate_code:{
                String phone = phone_et.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||phone.length()!=11){
                    Toast.makeText(RegisterActivity.this,"请输入正确的手机号", Toast.LENGTH_LONG).show();
                }else{
                    getVerifCode(phone);
                }
            }
                break;
            case R.id.btn_register:{
                String phone = phone_et.getText().toString().trim();
                String verifCode = validate_et.getText().toString().trim();
                String password = password_et.getText().toString().trim();
                String password_sure = password_sure_et.getText().toString().trim();
                if (TextUtils.isEmpty(phone)||phone.length()!=11){
                    Toast.makeText(RegisterActivity.this,"请输入正确的手机号", Toast.LENGTH_LONG).show();
                }else{
                    if (TextUtils.isEmpty(verifCode)){
                        Toast.makeText(RegisterActivity.this,"请输入验证码", Toast.LENGTH_LONG).show();
                    }else{
                        if (TextUtils.isEmpty(password)||password.length()<6){
                            Toast.makeText(RegisterActivity.this,"请输入大于6位的密码", Toast.LENGTH_LONG).show();
                        }else{
                            if (TextUtils.isEmpty(password_sure)||!password_sure.equals(password)) {
                                Toast.makeText(RegisterActivity.this, "请保证两次输入的密码一致", Toast.LENGTH_LONG).show();
                            }else {
                                register(phone,verifCode,password);
                            }
                        }
                    }
                }
            }
            break;
            case R.id.login_tv:{
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
                break;
            case R.id.show_bg:{
                if (!isShow) {
                    //如果选中，显示密码
                    password_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShow = true;
                } else {
                    //否则隐藏密码
                    password_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShow = false;
                }
            }
            break;
            case R.id.show_2_bg:{
                if (!isShow_2) {
                    //如果选中，显示密码
                    password_sure_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShow_2 = true;
                } else {
                    //否则隐藏密码
                    password_sure_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShow_2 = false;
                }
            }
            break;
        }
    }

    private void register(String phone, String verifCode, String password) {
        String url = NetConstant.BASE_URL + NetConstant.REGISTER;
        //设置回调方法
        NetUtils.setCallback(registerCallback);
        Map<String, String> keymap = new HashMap<>();
        keymap.put("mobile",phone);
        keymap.put("password",password);
        keymap.put("code",verifCode);
        //获取网络数据
        NetUtils.postDataWithParame(url, keymap);
    }

    private void getVerifCode(String phone) {
        String url = NetConstant.BASE_URL + NetConstant.SEND_MOBILE_CODE;
        //设置回调方法
        NetUtils.setCallback(codeCallback);
        Map<String, String> keymap = new HashMap<>();
        keymap.put("mobile",phone);
        keymap.put("sence","1");
        //获取网络数据
        NetUtils.postDataWithParame(url, keymap);
    }

    //验证码回调
    Callback codeCallback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.e("访问出错");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (NetCode.CODE_SUCCESS == response.code()) {
                CodeTemp tempBean = JSON.parseObject(response.body().string(), CodeTemp.class);
                if (tempBean.getCode() == 200) {
                    code = tempBean.getData();
                    //更新UI界面
                    handler.post(runnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, tempBean.getMsg(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(RegisterActivity.this,code.getCode(),Toast.LENGTH_LONG).show();
        }
    };

    //注册回调
    Callback registerCallback = new Callback() {

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
                    handler.post(registerRunnableUi);
                } else if (tempBean.getCode() == 400) {
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, tempBean.getMsg(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }
    };

    // 构建Runnable对象，在runnable中更新界面
    Runnable registerRunnableUi = new Runnable() {
        @Override
        public void run() {
            //注册成功
            Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            RegisterActivity.this.finish();
        }
    };
}
