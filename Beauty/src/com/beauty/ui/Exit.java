package com.beauty.ui;


import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

import com.beauty.javabean.BeautyUser;
import com.example.beauty.R;

public class Exit extends Activity {
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
		
		layout=(LinearLayout)findViewById(R.id.exit_layout);
		layout.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
			}
		});
		

		//BeautyUser bu  = BmobUser.getCurrentUser(this, BeautyUser.class);
		//Toast.makeText(Exit.this, bu.getUsername() + ":" + bu.getNickname(), Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	public void exitbutton_no(View v) {  
    	this.finish();    	
    }  
	public void exitbutton_yes(View v) {  
    	this.finish();
    	//清除缓存用户对象
    	BmobUser.logOut(MainActivity.instance);
    	MainActivity.instance.finish();
    }  
	
}
