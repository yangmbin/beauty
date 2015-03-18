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
	public static PullToRefreshListView likeListView; //喜欢页面自定义ListView
	public static HomeListViewAdapter likeListViewAdapter;
	public static LinkedList<Map<String, Object>> listItems = new LinkedList<Map<String, Object>>();

	private int clear = 0; //判断是否清空刷新前的数据
	private int count = 0; //用来判定异步加载数据是否加载完成
	
	private int i; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.like_fragment, group, false);
		//设置两端刷新
		likeListView = (PullToRefreshListView) view.findViewById(R.id.like_fragment_listview);
		likeListView.setMode(Mode.BOTH);
		
		//添加一条空数据
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
		
		//上拉和下拉的刷新监听
		likeListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			//设置下拉刷新监听
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView)
			{
				//获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat("最后更新: " + "yyyy.MM.dd HH:mm");     
            	Date curDate = new Date(System.currentTimeMillis());
            	String label = formatter.format(curDate);   
            	
                //最近一次的更新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                
                //头部刷新
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
									
																		
									//如果有一条以上得到了更新，那么就把先前的数据抹掉
									if (clear == 0) {
										LikeFragment.listItems.clear();
										clear = 1;
									}
									//添加到数据集合
									LikeFragment.listItems.add(map);
									
									//计数器+1
									count++;
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									//计数器+1
									count++;
								}
							});
						}
		                //新建一个线程来监听count是否完成计数，然后更新数据集合
						new Thread(new Runnable() 
						{
							@Override
							public void run() {
								while (true) {
									if (count == list.size()) {
										if (count == 0)
											listItems.clear();
										System.out.println("---------------------------" + count);
										//通知适配器数据集合发生改变，并停止动画
										MainActivity.handler.sendEmptyMessage(2);
										break;
									}
								}
							}
						}).start();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//如果第一条是空数据，则删除
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//更新适配器，停止动画
						MainActivity.handler.sendEmptyMessage(3);
					}
				});
			}

			//设置上拉刷新监听
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) 
			{
				//获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat("最后更新: " + "yyyy.MM.dd HH:mm");     
            	Date curDate = new Date(System.currentTimeMillis());
            	String label = formatter.format(curDate);   
            	
                //最近一次的更新时间
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
									
									//添加到数据集合
									LikeFragment.listItems.add(map);
									
									//计数器+1
									count++;
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									//计数器+1
									count++;
								}
							});
						}
		                //新建一个线程来监听count是否完成计数，然后更新数据集合
						new Thread(new Runnable() 
						{
							@Override
							public void run() {
								while (true) {
									if (count == list.size()) {
										//如果第一条是空数据，则删除
										if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
											listItems.remove(0);
										System.out.println("---------------------------" + count);
										//通知适配器数据集合发生改变，并停止动画
										MainActivity.handler.sendEmptyMessage(2);
										break;
									}
								}
							}
						}).start();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//如果第一条是空数据，则删除
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//更新适配器，停止动画
						MainActivity.handler.sendEmptyMessage(3);
					}
				});
			}
		});
			
		//数据集合初始化
		//MyApplication.listItems = new LinkedList<Map<String, Object>>();
		
		/*
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", "2014.12.12");
		map.put("nickname", "昵称");
		map.put("headimg", "http://img1.imgtn.bdimg.com/it/u=307927442,2018132219&fm=11&gp=0.jpg");
		map.put("sharedimg", "http://img1.imgtn.bdimg.com/it/u=307927442,2018132219&fm=11&gp=0.jpg");
		map.put("id", "iiiiiiiiiiiiiiiiid");
		map.put("likeNum", "11");
		map.put("dislikeNum", "22");
		MyApplication.listItems.add(map);
		*/
		
		
		//适配器初始化
		likeListViewAdapter = new HomeListViewAdapter(MainActivity.instance, listItems, "likePage");
		//设置适配器
		likeListView.setAdapter(likeListViewAdapter);
		
		//每次进入此页面时，新建手势进行自动下拉刷新
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -800, 0);
		if (likeListView.isBeingDraggedToTrue()) {
			likeListView.onTouchEvent(event);			
		}
		
		return view;
	}
}
