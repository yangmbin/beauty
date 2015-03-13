package yangmbin.beauty.ui;

import yangmbin.beauty.javabean.BeautyUser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.bmob.v3.listener.SaveListener;

import com.yangmbin.beauty.R;

public class LoginActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}
	
	/*
	 * 登录到主页面
	 */
	public void loginToMainPage(View v) {
		EditText usernameEditText = (EditText) findViewById(R.id.login_edittext_top);
		EditText passwordEditText = (EditText) findViewById(R.id.login_edittext_bottom);
		//获取输入用于注册的用户名和密码
		String username = usernameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
		//判断是否为空
		if (username.equals("") || password.equals("")) {
			Toast.makeText(LoginActivity.this, "输入为空！", Toast.LENGTH_SHORT).show();
		}
		else {
			//关闭软键盘
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
			//开启进度条
			final ProgressBar bar = (ProgressBar) findViewById(R.id.login_progressbar);
			bar.setVisibility(0);
			
			//构造登录对象
			BeautyUser bu = new BeautyUser();
			bu.setUsername(username);
			bu.setPassword(password);
			bu.login(LoginActivity.this, new SaveListener() {
				
				@Override
				public void onSuccess() {
					bar.setVisibility(8);
					Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					LoginActivity.this.finish();
				}
				
				@Override
				public void onFailure(int code, String msg) {
					bar.setVisibility(8);
					Toast.makeText(LoginActivity.this, "登录失败：" + msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	/*
	 * 注册账户，跳转到注册页面
	 */
	public void registerTextviewBtn(View v) {
		startActivity(new Intent(this, RegisterActivity.class));
	}
}
