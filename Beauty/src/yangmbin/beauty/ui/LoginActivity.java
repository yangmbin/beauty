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
	 * ��¼����ҳ��
	 */
	public void loginToMainPage(View v) {
		EditText usernameEditText = (EditText) findViewById(R.id.login_edittext_top);
		EditText passwordEditText = (EditText) findViewById(R.id.login_edittext_bottom);
		//��ȡ��������ע����û���������
		String username = usernameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
		//�ж��Ƿ�Ϊ��
		if (username.equals("") || password.equals("")) {
			Toast.makeText(LoginActivity.this, "����Ϊ�գ�", Toast.LENGTH_SHORT).show();
		}
		else {
			//�ر������
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
			//����������
			final ProgressBar bar = (ProgressBar) findViewById(R.id.login_progressbar);
			bar.setVisibility(0);
			
			//�����¼����
			BeautyUser bu = new BeautyUser();
			bu.setUsername(username);
			bu.setPassword(password);
			bu.login(LoginActivity.this, new SaveListener() {
				
				@Override
				public void onSuccess() {
					bar.setVisibility(8);
					Toast.makeText(LoginActivity.this, "��¼�ɹ���", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					LoginActivity.this.finish();
				}
				
				@Override
				public void onFailure(int code, String msg) {
					bar.setVisibility(8);
					Toast.makeText(LoginActivity.this, "��¼ʧ�ܣ�" + msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	/*
	 * ע���˻�����ת��ע��ҳ��
	 */
	public void registerTextviewBtn(View v) {
		startActivity(new Intent(this, RegisterActivity.class));
	}
}
