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
	public static PullToRefreshListView mypublishmentListView; //主页面自定义ListView
	public static HomeListViewAdapter mypublishmentListViewAdapter;
	public static LinkedList<Map<String, Object>> listItems = new LinkedList<Map<String,Object>>();

	private int clear = 0; //判断是否清空刷新前的数据
	
	private int i; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_publishment);
		
		//设置两端刷新
		mypublishmentListView = (PullToRefreshListView) findViewById(R.id.my_publishment_listview);
		mypublishmentListView.setMode(Mode.BOTH);
		
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
		
		//适配器初始化
		mypublishmentListViewAdapter = new HomeListViewAdapter(MyPublishment.this, listItems);
		//设置适配器
		mypublishmentListView.setAdapter(mypublishmentListViewAdapter);
		
		//上拉和下拉的刷新监听
		mypublishmentListView.setOnRefreshListener(new OnRefreshListener2<ListView>() 
		{
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
                //刷新操作，获取分享的信息集合
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
							
							//如果有一条以上得到了更新，那么就把先前的数据抹掉
							if (clear == 0) {
								listItems.clear();
								clear = 1;
							}
							//添加到数据集合
							listItems.add(map);
						}
						if (list.size() == 0)
							listItems.clear();
						//通知适配器数据集合发生改变，并停止动画
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						Toast.makeText(getApplicationContext(), "刷新成功！", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//如果第一条是空数据，则删除
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//更新适配器，停止动画
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						Toast.makeText(getApplicationContext(), "刷新失败！", Toast.LENGTH_SHORT).show();
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
                
           
                //刷新操作，获取分享的信息集合
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
							//添加到数据集合
							listItems.add(map);
						}
						//如果第一条是空数据，则删除
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//通知适配器数据集合发生改变，并停止动画
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						Toast.makeText(getApplicationContext(), "刷新成功！", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//如果第一条是空数据，则删除
						if (!listItems.isEmpty() && listItems.get(0).get("id") == null)
							listItems.remove(0);
						//更新适配器，停止动画
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
						Toast.makeText(getApplicationContext(), "刷新失败！", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		//每次进入此页面时，新建手势进行自动下拉刷新
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -800, 0);
		if (mypublishmentListView.isBeingDraggedToTrue()) {
			mypublishmentListView.onTouchEvent(event);			
		}
	}
	
	/*
	 * 用于更改UI的handler
	 */
	public static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//主页数据更新
			if (msg.what == 0) {
				mypublishmentListViewAdapter.notifyDataSetChanged();
			}
			//主页关闭动画
			else if(msg.what == 1) {
				mypublishmentListView.onRefreshComplete();
			}
		}
	};
	
	/*
	 * 返回按钮
	 */
	public void backBtn(View v) {
		finish();
	}
}
