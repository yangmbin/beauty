package com.beauty.ui;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.beauty.javabean.BeautyUser;
import com.example.beauty.R;

public class RegisterActivity extends Activity {
	private String headimgURL; //表示返回的头像URL地址
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
	}
	
	/*
	 * 返回按钮
	 */
	public void backBtn(View v) {
		this.finish();
	}
	
	/*
	 * 注册按钮操作
	 */
	public void registerBtn(View v) {
		EditText usernameEditText = (EditText) findViewById(R.id.register_edittext_top);
		EditText nicknameEditText = (EditText) findViewById(R.id.register_edittext_mid);
		EditText passwordEditText = (EditText) findViewById(R.id.register_edittext_bottom);
		//获取输入用于注册的用户名、密码和昵称
		final String username = usernameEditText.getText().toString().trim();
		final String nickname = nicknameEditText.getText().toString().trim();
		final String password = passwordEditText.getText().toString().trim();
		//判断是否为空
		if (username.equals("") || nickname.equals("") || password.equals("")) {
			Toast.makeText(RegisterActivity.this, "输入为空！", Toast.LENGTH_SHORT).show();
		}
		else {
			//关闭软键盘
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(RegisterActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
			//开启进度条
			final ProgressBar bar = (ProgressBar) findViewById(R.id.register_progressbar);
			bar.setVisibility(0);
			
			//先上传头像
			File localFile = new File(Environment.getExternalStorageDirectory(), username + ".jpg");
			try {
				Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.headimg);
			    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
			    bp.compress(Bitmap.CompressFormat.PNG, 100, localFileOutputStream);
			    localFileOutputStream.flush();
			    localFileOutputStream.close();
			}
			catch (Exception e) {
				
			}
			
			final BmobFile bmobFile = new BmobFile(localFile);
			bmobFile.uploadblock(this, new UploadFileListener() {
				
				@Override
				public void onSuccess() {
					headimgURL = bmobFile.getFileUrl(RegisterActivity.this);
					
					//构造注册对象
					BeautyUser bu = new BeautyUser();
					bu.setUsername(username);
					bu.setNickname(nickname);
					bu.setPassword(password);
					bu.setHeadimgURL(headimgURL);
					bu.setSignature("还未设置此项~");
					bu.signUp(RegisterActivity.this, new SaveListener() {
						@Override
						public void onSuccess() {
							bar.setVisibility(8);
							Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
							RegisterActivity.this.finish();
						}
						
						@Override
						public void onFailure(int code, String msg) {
							bar.setVisibility(8);
							Toast.makeText(RegisterActivity.this, "注册失败：" + msg, Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
				}
			});
		}
	}
}
