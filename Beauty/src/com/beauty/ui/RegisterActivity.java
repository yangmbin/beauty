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
	private String headimgURL; //��ʾ���ص�ͷ��URL��ַ
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
	}
	
	/*
	 * ���ذ�ť
	 */
	public void backBtn(View v) {
		this.finish();
	}
	
	/*
	 * ע�ᰴť����
	 */
	public void registerBtn(View v) {
		EditText usernameEditText = (EditText) findViewById(R.id.register_edittext_top);
		EditText nicknameEditText = (EditText) findViewById(R.id.register_edittext_mid);
		EditText passwordEditText = (EditText) findViewById(R.id.register_edittext_bottom);
		//��ȡ��������ע����û�����������ǳ�
		final String username = usernameEditText.getText().toString().trim();
		final String nickname = nicknameEditText.getText().toString().trim();
		final String password = passwordEditText.getText().toString().trim();
		//�ж��Ƿ�Ϊ��
		if (username.equals("") || nickname.equals("") || password.equals("")) {
			Toast.makeText(RegisterActivity.this, "����Ϊ�գ�", Toast.LENGTH_SHORT).show();
		}
		else {
			//�ر������
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(RegisterActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
			//����������
			final ProgressBar bar = (ProgressBar) findViewById(R.id.register_progressbar);
			bar.setVisibility(0);
			
			//���ϴ�ͷ��
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
					
					//����ע�����
					BeautyUser bu = new BeautyUser();
					bu.setUsername(username);
					bu.setNickname(nickname);
					bu.setPassword(password);
					bu.setHeadimgURL(headimgURL);
					bu.setSignature("��δ���ô���~");
					bu.signUp(RegisterActivity.this, new SaveListener() {
						@Override
						public void onSuccess() {
							bar.setVisibility(8);
							Toast.makeText(RegisterActivity.this, "ע��ɹ���", Toast.LENGTH_SHORT).show();
							RegisterActivity.this.finish();
						}
						
						@Override
						public void onFailure(int code, String msg) {
							bar.setVisibility(8);
							Toast.makeText(RegisterActivity.this, "ע��ʧ�ܣ�" + msg, Toast.LENGTH_SHORT).show();
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
