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
	
	private String setuserinfo; //表示传递过来的字符串，判断是何种修改
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_user_info);
		//获取传递过来的数据
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
	 * 确定按钮
	 */
	public void buttonYes(View v) {
		final String originalNickname = BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getNickname();
		EditText input = (EditText) findViewById(R.id.set_user_info_edittext);
		final String content = input.getText().toString().trim();
		//修改昵称
		if (setuserinfo.equals("nickname") && !content.equals(""))
		{
			BeautyUser bu = new BeautyUser();
			bu.setNickname(content);
			bu.update(getApplicationContext(), BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getObjectId(), new UpdateListener()
			{
				@Override
				public void onSuccess() {
					finish();
					//修改视图内容
					TextView nicknameTextView = (TextView) SettingFragment.settingFragmentView.findViewById(R.id.setting_fragment_nickname);
					nicknameTextView.setText(content);
					Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
					
					//更新Comment表
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
					
					//更新SharedMessage表
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
					Toast.makeText(getApplicationContext(), "修改失败！", Toast.LENGTH_SHORT).show();
				}
			});
		}
		//修改签名
		else if (setuserinfo.equals("signature") && !content.equals(""))
		{
			BeautyUser bu = new BeautyUser();
			bu.setSignature(content);
			bu.update(getApplicationContext(), BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getObjectId(), new UpdateListener()
			{
				@Override
				public void onSuccess() {
					finish();
					//修改视图内容
					TextView signatureTextView = (TextView) SettingFragment.settingFragmentView.findViewById(R.id.setting_fragment_signature);
					signatureTextView.setText(content);
					Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					finish();
					Toast.makeText(getApplicationContext(), "修改失败！", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	/*
	 * 取消按钮
	 */
	public void buttonNo(View v) {
		finish();
	}
}
