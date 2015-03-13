package com.beauty.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.beauty.adapter.CommentListViewAdapter;
import com.beauty.application.MyApplication;
import com.beauty.fragment.MainpageFragment;
import com.beauty.javabean.BeautyUser;
import com.beauty.javabean.Comment;
import com.beauty.javabean.SharedMessage;
import com.example.beauty.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class CommentActivity extends Activity {
	private PullToRefreshListView commentListView; //����listview
	private CommentListViewAdapter adapter; //������
	private LinkedList<Map<String, Object>> listItems; //���ݼ���
	
	private int clear = 0; //�ж��Ƿ����ˢ��ǰ������
	private String articleID; //���ݹ���������id
	private int itemID; //���ݹ�����int���ݣ���ʾ����˵ڼ�����Ŀ
	@SuppressLint("Recycle") @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_comment);
		
		//��ȡ����id
		articleID = getIntent().getExtras().getString("articleID");
		
		commentListView = (PullToRefreshListView) findViewById(R.id.listview_comment_listview);
		//���ó�����ˢ��
		commentListView.setMode(Mode.BOTH);
		//���ݼ���
		listItems = new LinkedList<Map<String, Object>>(); 
		
		//��listview��Ĭ�ϼ�һ�������ݲ��ܹ������Զ�ˢ�£���Ҳ��֪��Ϊʲô��
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("headimg", null);
		map.put("nickname", null);
		map.put("time", null);
		map.put("comment", null);
		listItems.add(map);
		
		
		adapter = new CommentListViewAdapter(this, listItems);
		commentListView.setAdapter(adapter);
		
		//����ˢ�µļ���
		commentListView.setOnRefreshListener(new OnRefreshListener2<ListView>()
		{
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) 
			{
				//��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat("������: " + "yyyy.MM.dd HH:mm");     
            	Date curDate = new Date(System.currentTimeMillis());
            	String label = formatter.format(curDate);   
                //���һ�εĸ���ʱ��
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                
                //��ȡ���۵��첽����
                clear = 0;
                BmobQuery<Comment> bm = new BmobQuery<Comment>();
                bm.order("-createdAt");
                bm.addWhereEqualTo("articleID", getIntent().getExtras().getString("articleID"));
                bm.findObjects(getApplicationContext(), new FindListener<Comment>() 
                {
					@Override
					public void onSuccess(List<Comment> list) {
						for (int i = 0; i < list.size(); i++)
						{
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("headimg", list.get(i).getHeadimgURL());
							map.put("nickname", list.get(i).getNickname());
							map.put("time", list.get(i).getCreatedAt());
							map.put("comment", list.get(i).getComment());
							//�����һ�����ϵõ��˸��£���ô�Ͱ���ǰ������Ĩ��
							if (clear == 0) {
								listItems.clear();
								clear = 1;
							}
							listItems.add(map);
						}
						//֪ͨ���������ݼ��Ϸ����ı䣬��ֹͣ����
						adapter.notifyDataSetChanged();
						commentListView.onRefreshComplete();
						Toast.makeText(getApplicationContext(), "ˢ�³ɹ���", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//ֹͣ����
						commentListView.onRefreshComplete();
						Toast.makeText(MainActivity.instance, "ˢ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) 
			{
				//����UI���߳���ֹͣ��������Ȼ���ɹ�
				//commentListView.onRefreshComplete();
				handler.sendEmptyMessage(0);
			}
		});
		
		//ÿ�ν����ҳ��ʱ���½����ƽ����Զ�����ˢ��
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -800, 0);
		if (commentListView.isBeingDraggedToTrue()) {
			commentListView.onTouchEvent(event);			
		}
	}
	
	/*
	 * ����������Ϣ
	 */
	public void sendComment(View v) {
		final EditText commentEditText = (EditText) findViewById(R.id.listview_comment_edittext);
		String commentStr = commentEditText.getText().toString().trim();
		if ("".equals(commentStr)) 
		{
			Toast.makeText(getApplicationContext(), "����Ϊ�գ�", Toast.LENGTH_SHORT).show();
		}
		else
		{
			final Comment comment = new Comment();
			comment.setHeadimgURL(BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getHeadimgURL());
			comment.setNickname(BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getNickname());
			comment.setComment(commentStr);
			comment.setArticleID(getIntent().getExtras().getString("articleID"));
			//��������
			startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
			comment.save(getApplicationContext(), new SaveListener() 
			{
				@Override
				public void onSuccess() {
					commentEditText.setText(null);
					//������۵�ͷ��
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("headimg", comment.getHeadimgURL());
					map.put("nickname", comment.getNickname());
					map.put("time", comment.getCreatedAt());
					map.put("comment", comment.getComment());
					listItems.addFirst(map);
					adapter.notifyDataSetChanged();
					
					LoadingActivity.instance.finish();
					Toast.makeText(getApplicationContext(), "���۳ɹ���", Toast.LENGTH_SHORT).show();
					
					//����SharedMessage���������
					BmobQuery<SharedMessage> query = new BmobQuery<SharedMessage>();
					query.getObject(getApplicationContext(), articleID, new GetListener<SharedMessage>() {
						@Override
						public void onFailure(int arg0, String arg1) {
						}

						@Override
						public void onSuccess(SharedMessage arg0) {
							final SharedMessage sm = new SharedMessage();
							sm.setComment(arg0.getComment() + 1);
							sm.update(getApplicationContext(), articleID, new UpdateListener() {
								@Override
								public void onSuccess() {
									MyApplication.listItems.get(itemID).put("commentNum", Integer.toString(sm.getComment()));
									MainpageFragment.homeListViewAdapter.notifyDataSetChanged();
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
								}
							});
						}
					});
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					LoadingActivity.instance.finish();
					Toast.makeText(getApplicationContext(), "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	/*
	 * ����UI��hander
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//�ر�ˢ�¶���
			if (msg.what == 0) {
				commentListView.onRefreshComplete();
			}
		}
	};
	
	/*
	 * ���ذ�ť
	 */
	public void backBtn(View v) {
		finish();
	}
}
