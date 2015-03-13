package com.beauty.ui;

import cn.bmob.v3.Bmob;

import com.example.beauty.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class AppStart extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化Bmob SDK，第二个参数为application ID
        Bmob.initialize(this, "45f3a0f60e96b6eacb4558d016d1d86e");
        
        setContentView(R.layout.app_start);
        
        //1.5s后跳转
        new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(AppStart.this, LoginActivity.class));
				AppStart.this.finish();
			}
		}, 1500);
    } 
}
