package com.zhongyi.mind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditNameActivity extends Activity implements View.OnClickListener {
    private ImageView title_lift;
    private TextView title;

    private Button submit;
    private EditText editText;
    private String name = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_edit);
        title_lift = findViewById(R.id.title_back);
        title_lift.setOnClickListener(this);
        title = findViewById(R.id.title_content);
        title.setText("个人信息");

        editText = findViewById(R.id.et_name);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
            {
                EditNameActivity.this.finish();
            }
                break;
            case R.id.submit:{
                name = editText.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("name",name);
                EditNameActivity.this.setResult(RESULT_OK,intent);
                EditNameActivity.this.finish();
            }
                break;
        }
    }
}
