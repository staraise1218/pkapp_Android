package com.zhongyi.mind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongyi.mind.util.SharedPreferencesUtils;

public class SettingActivity extends Activity {
    private TextView submit;
    private ImageView title_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        submit = findViewById(R.id.submit);
        title_back = findViewById(R.id.title_back);
        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置activity
                ((MyApplication)getApplication()).destory();
                SharedPreferencesUtils.setParam("isLogin",false);
                Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                SettingActivity.this.finish();
            }
        });
    }
}
