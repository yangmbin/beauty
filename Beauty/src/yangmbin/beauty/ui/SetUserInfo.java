package yangmbin.beauty.ui;

import java.util.List;

import yangmbin.beauty.fragment.SettingFragment;
import yangmbin.beauty.javabean.BeautyUser;
import yangmbin.beauty.javabean.Comment;
import yangmbin.beauty.javabean.SharedMessage;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.yangmbin.beauty.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetUserInfo extends Activity {
	private LinearLayout layout;
	
	private String setuserinfo; //��ʾ���ݹ������ַ������ж��Ǻ����޸�
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_user_info);
		//��ȡ���ݹ���������
		setuserinfo = getIntent().getExtras().getString("setuserinfo");
		
		layout=(LinearLayout)findViewById(R.id.set_user_info_layout);
		layout.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	/*
	 * ȷ����ť
	 */
	public void buttonYes(View v) {
		final String originalNickname = BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getNickname();
		EditText input = (EditText) findViewById(R.id.set_user_info_edittext);
		final String content = input.getText().toString().trim();
		//�޸��ǳ�
		if (setuserinfo.equals("nickname") && !content.equals(""))
		{
			BeautyUser bu = new BeautyUser();
			bu.setNickname(content);
			bu.update(getApplicationContext(), BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getObjectId(), new UpdateListener()
			{
				@Override
				public void onSuccess() {
					finish();
					//�޸���ͼ����
					TextView nicknameTextView = (TextView) SettingFragment.settingFragmentView.findViewById(R.id.setting_fragment_nickname);
					nicknameTextView.setText(content);
					Toast.makeText(getApplicationContext(), "�޸ĳɹ���", Toast.LENGTH_SHORT).show();
					
					//����Comment��
					BmobQuery<Comment> query = new BmobQuery<Comment>();
					query.addWhereEqualTo("nickname", originalNickname);
					query.findObjects(getApplicationContext(), new FindListener<Comment>() {
						
						@Override
						public void onSuccess(List<Comment> list) {
							for (int i = 0; i < list.size(); i++) {
								Comment c = new Comment();
								c.setNickname(content);
								c.update(getApplicationContext(), list.get(i).getObjectId(), null);
							}
						}
						
						@Override
						public void onError(int arg0, String arg1) {
						}
					});
					
					//����SharedMessage��
					BmobQuery<SharedMessage> query2 = new BmobQuery<SharedMessage>();
					query2.addWhereEqualTo("nickname", originalNickname);
					query2.findObjects(getApplicationContext(), new FindListener<SharedMessage>() {
						
						@Override
						public void onSuccess(List<SharedMessage> list) {
							for (int i = 0; i < list.size(); i++) {
								SharedMessage sm = new SharedMessage();
								sm.setNickname(content);
								sm.update(getApplicationContext(), list.get(i).getObjectId(), null);
							}
						}
						
						@Override
						public void onError(int arg0, String arg1) {
						}
					});
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					finish();
					Toast.makeText(getApplicationContext(), "�޸�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				}
			});
		}
		//�޸�ǩ��
		else if (setuserinfo.equals("signature") && !content.equals(""))
		{
			BeautyUser bu = new BeautyUser();
			bu.setSignature(content);
			bu.update(getApplicationContext(), BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getObjectId(), new UpdateListener()
			{
				@Override
				public void onSuccess() {
					finish();
					//�޸���ͼ����
					TextView signatureTextView = (TextView) SettingFragment.settingFragmentView.findViewById(R.id.setting_fragment_signature);
					signatureTextView.setText(content);
					Toast.makeText(getApplicationContext(), "�޸ĳɹ���", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					finish();
					Toast.makeText(getApplicationContext(), "�޸�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	/*
	 * ȡ����ť
	 */
	public void buttonNo(View v) {
		finish();
	}
}
