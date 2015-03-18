package yangmbin.beauty.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import yangmbin.beauty.adapter.HomeListViewAdapter;
import yangmbin.beauty.application.MyApplication;
import yangmbin.beauty.javabean.BeautyUser;
import yangmbin.beauty.javabean.Collection;
import yangmbin.beauty.javabean.SharedMessage;
import yangmbin.beauty.ui.MainActivity;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.yangmbin.beauty.R;

import android.annotation.TargetApi;
import android.content.ClipData.Item;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class LikeFragment extends Fragment {
	public static PullToRefreshListView likeListView; //ϲ��ҳ���Զ���ListView
	public static HomeListViewAdapter likeListViewAdapter;
	public static LinkedList<Map<String, Object>> listItems = new LinkedList<Map<String, Object>>();

	private int clear = 0; //�ж��Ƿ����ˢ��ǰ������
	private int count = 0; //�����ж��첽���������Ƿ�������
	
	private int i; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.like_fragment, group, false);
		//��������ˢ��
		likeListView = (PullToRefreshListView) view.findViewById(R.id.like_fragment_listview);
		likeListView.setMode(Mode.BOTH);
		
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
		
		//������������ˢ�¼���
		likeListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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
            	count = 0;
                BmobQuery<Collection> query = new BmobQuery<Collection>();
                query.addWhereEqualTo("username", BmobUser.getCurrentUser(MainActivity.instance, BeautyUser.class).getUsername());
                query.order("-createdAt");
                query.findObjects(MainActivity.instance, new FindListener<Collection>()
                {
                	@Override
					public void onSuccess(final List<Collection> list) {
						for (i = 0; i < list.size(); i++)
						{
							BmobQuery<SharedMessage> innerQuery = new BmobQuery<SharedMessage>();
							innerQuery.getObject(MainActivity.instance, list.get(i).getArticleID(), new GetListener<SharedMessage>()
							{
								@Override
								public void onSuccess(SharedMessage sm) {
									final Map<String, Object> map = new HashMap<String, Object>();
									map.put("time", sm.getCreatedAt());
									map.put("sharedimg", sm.getSharedimgURL());
									map.put("id", sm.getObjectId());
									map.put("nickname", sm.getNickname());
									map.put("headimg", sm.getHeadimgURL());
									map.put("likeNum", sm.getLike().toString());
									map.put("dislikeNum", sm.getDislike().toString());
									map.put("commentNum", sm.getComment().toString());
									
																		
									//�����һ�����ϵõ��˸��£���ô�Ͱ���ǰ������Ĩ��
									if (clear == 0) {
										LikeFragment.listItems.clear();
										clear = 1;
									}
									//��ӵ����ݼ���
									LikeFragment.listItems.add(map);
									
									//������+1
									count++;
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									//������+1
									count++;
								}
							});
						}
		                //�½�һ���߳�������count�Ƿ���ɼ�����Ȼ��������ݼ���
						new Thread(new Runnable() 
						{
							@Override
							public void run() {
								while (true) {
									if (count == list.size()) {
										if (count == 0)
											listItems.clear();
										System.out.println("---------------------------" + count);
										//֪ͨ���������ݼ��Ϸ����ı䣬��ֹͣ����
										MainActivity.handler.sendEmptyMessage(2);
										break;
									}
								}
							}
						}).start();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//�����һ���ǿ����ݣ���ɾ��
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//������������ֹͣ����
						MainActivity.handler.sendEmptyMessage(3);
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
                
            	count = 0;
                BmobQuery<Collection> query = new BmobQuery<Collection>();
                query.addWhereEqualTo("username", BmobUser.getCurrentUser(MainActivity.instance, BeautyUser.class).getUsername());
                query.order("-createdAt");
                query.setSkip(listItems.size());
                query.findObjects(MainActivity.instance, new FindListener<Collection>()
                {
                	@Override
					public void onSuccess(final List<Collection> list) {
						for (i = 0; i < list.size(); i++)
						{
							BmobQuery<SharedMessage> innerQuery = new BmobQuery<SharedMessage>();
							innerQuery.getObject(MainActivity.instance, list.get(i).getArticleID(), new GetListener<SharedMessage>()
							{
								@Override
								public void onSuccess(SharedMessage sm) {
									final Map<String, Object> map = new HashMap<String, Object>();
									map.put("time", sm.getCreatedAt());
									map.put("sharedimg", sm.getSharedimgURL());
									map.put("id", sm.getObjectId());
									map.put("nickname", sm.getNickname());
									map.put("headimg", sm.getHeadimgURL());
									map.put("likeNum", sm.getLike().toString());
									map.put("dislikeNum", sm.getDislike().toString());
									map.put("commentNum", sm.getComment().toString());
									
									//��ӵ����ݼ���
									LikeFragment.listItems.add(map);
									
									//������+1
									count++;
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									//������+1
									count++;
								}
							});
						}
		                //�½�һ���߳�������count�Ƿ���ɼ�����Ȼ��������ݼ���
						new Thread(new Runnable() 
						{
							@Override
							public void run() {
								while (true) {
									if (count == list.size()) {
										//�����һ���ǿ����ݣ���ɾ��
										if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
											listItems.remove(0);
										System.out.println("---------------------------" + count);
										//֪ͨ���������ݼ��Ϸ����ı䣬��ֹͣ����
										MainActivity.handler.sendEmptyMessage(2);
										break;
									}
								}
							}
						}).start();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//�����һ���ǿ����ݣ���ɾ��
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//������������ֹͣ����
						MainActivity.handler.sendEmptyMessage(3);
					}
				});
			}
		});
			
		//���ݼ��ϳ�ʼ��
		//MyApplication.listItems = new LinkedList<Map<String, Object>>();
		
		/*
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", "2014.12.12");
		map.put("nickname", "�ǳ�");
		map.put("headimg", "http://img1.imgtn.bdimg.com/it/u=307927442,2018132219&fm=11&gp=0.jpg");
		map.put("sharedimg", "http://img1.imgtn.bdimg.com/it/u=307927442,2018132219&fm=11&gp=0.jpg");
		map.put("id", "iiiiiiiiiiiiiiiiid");
		map.put("likeNum", "11");
		map.put("dislikeNum", "22");
		MyApplication.listItems.add(map);
		*/
		
		
		//��������ʼ��
		likeListViewAdapter = new HomeListViewAdapter(MainActivity.instance, listItems, "likePage");
		//����������
		likeListView.setAdapter(likeListViewAdapter);
		
		//ÿ�ν����ҳ��ʱ���½����ƽ����Զ�����ˢ��
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -800, 0);
		if (likeListView.isBeingDraggedToTrue()) {
			likeListView.onTouchEvent(event);			
		}
		
		return view;
	}
}
