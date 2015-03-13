package yangmbin.beauty.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import yangmbin.beauty.adapter.HomeListViewAdapter;
import yangmbin.beauty.application.MyApplication;
import yangmbin.beauty.javabean.BeautyUser;
import yangmbin.beauty.javabean.SharedMessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yangmbin.beauty.R;

public class MyPublishment extends Activity {
	public static PullToRefreshListView mypublishmentListView; //��ҳ���Զ���ListView
	public static HomeListViewAdapter mypublishmentListViewAdapter;
	public static LinkedList<Map<String, Object>> listItems = new LinkedList<Map<String,Object>>();

	private int clear = 0; //�ж��Ƿ����ˢ��ǰ������
	
	private int i; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_publishment);
		
		//��������ˢ��
		mypublishmentListView = (PullToRefreshListView) findViewById(R.id.my_publishment_listview);
		mypublishmentListView.setMode(Mode.BOTH);
		
		//���һ��������
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", null);
		map.put("sharedimg", null);
		map.put("id", null);
		map.put("nickname", null);
		map.put("headimg", null);
		map.put("likeNum", null);
		map.put("dislikeNum", null);
		map.put("commentNum", null);
		if (listItems.isEmpty())
			listItems.add(map);
		
		//��������ʼ��
		mypublishmentListViewAdapter = new HomeListViewAdapter(MyPublishment.this, listItems);
		//����������
		mypublishmentListView.setAdapter(mypublishmentListViewAdapter);
		
		//������������ˢ�¼���
		mypublishmentListView.setOnRefreshListener(new OnRefreshListener2<ListView>() 
		{
			//��������ˢ�¼���
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
                
                //ͷ��ˢ��
            	clear = 0;
                //ˢ�²�������ȡ�������Ϣ����
                BmobQuery<SharedMessage> query = new BmobQuery<SharedMessage>();
                query.order("-createdAt");
                query.addWhereEqualTo("username", BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getUsername());
                query.findObjects(getApplicationContext(), new FindListener<SharedMessage>() 
                {
					@Override
					public void onSuccess(final List<SharedMessage> list) 
					{
						for (i = 0; i < list.size(); i++) 
						{
							final Map<String, Object> map = new HashMap<String, Object>();
							map.put("time", list.get(i).getCreatedAt());
							map.put("sharedimg", list.get(i).getSharedimgURL());
							map.put("id", list.get(i).getObjectId());
							map.put("nickname", list.get(i).getNickname());
							map.put("headimg", list.get(i).getHeadimgURL());
							map.put("likeNum", list.get(i).getLike().toString());
							map.put("dislikeNum", list.get(i).getDislike().toString());
							map.put("commentNum", list.get(i).getComment().toString());
							
							//�����һ�����ϵõ��˸��£���ô�Ͱ���ǰ������Ĩ��
							if (clear == 0) {
								listItems.clear();
								clear = 1;
							}
							//��ӵ����ݼ���
							listItems.add(map);
						}
						if (list.size() == 0)
							listItems.clear();
						//֪ͨ���������ݼ��Ϸ����ı䣬��ֹͣ����
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						Toast.makeText(getApplicationContext(), "ˢ�³ɹ���", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//�����һ���ǿ����ݣ���ɾ��
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//������������ֹͣ����
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						Toast.makeText(getApplicationContext(), "ˢ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
					}
				});
			}

			//��������ˢ�¼���
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView)
			{
				//��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat("������: " + "yyyy.MM.dd HH:mm");     
            	Date curDate = new Date(System.currentTimeMillis());
            	String label = formatter.format(curDate);   
            	
                //���һ�εĸ���ʱ��
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                
           
                //ˢ�²�������ȡ�������Ϣ����
                BmobQuery<SharedMessage> query = new BmobQuery<SharedMessage>();
                query.order("-createdAt");
                query.addWhereEqualTo("username", BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getUsername());
                query.setSkip(listItems.size());
                query.findObjects(getApplicationContext(), new FindListener<SharedMessage>() 
                {
					@Override
					public void onSuccess(final List<SharedMessage> list) 
					{
						for (i = 0; i < list.size(); i++) 
						{
							final Map<String, Object> map = new HashMap<String, Object>();
							map.put("time", list.get(i).getCreatedAt());
							map.put("sharedimg", list.get(i).getSharedimgURL());
							map.put("id", list.get(i).getObjectId());
							map.put("nickname", list.get(i).getNickname());
							map.put("headimg", list.get(i).getHeadimgURL());
							map.put("likeNum", list.get(i).getLike().toString());
							map.put("dislikeNum", list.get(i).getDislike().toString());
							map.put("commentNum", list.get(i).getComment().toString());
							//��ӵ����ݼ���
							listItems.add(map);
						}
						//�����һ���ǿ����ݣ���ɾ��
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//֪ͨ���������ݼ��Ϸ����ı䣬��ֹͣ����
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						Toast.makeText(getApplicationContext(), "ˢ�³ɹ���", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//�����һ���ǿ����ݣ���ɾ��
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//������������ֹͣ����
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						Toast.makeText(getApplicationContext(), "ˢ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		//ÿ�ν����ҳ��ʱ���½����ƽ����Զ�����ˢ��
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -800, 0);
		if (mypublishmentListView.isBeingDraggedToTrue()) {
			mypublishmentListView.onTouchEvent(event);			
		}
	}
	
	/*
	 * ���ڸ���UI��handler
	 */
	public static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//��ҳ���ݸ���
			if (msg.what == 0) {
				mypublishmentListViewAdapter.notifyDataSetChanged();
			}
			//��ҳ�رն���
			else if(msg.what == 1) {
				mypublishmentListView.onRefreshComplete();
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
