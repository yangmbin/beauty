package com.beauty.fragment;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.beauty.adapter.HomeListViewAdapter;
import com.beauty.application.MyApplication;
import com.beauty.javabean.BeautyUser;
import com.beauty.javabean.LikeOrDislike;
import com.beauty.javabean.SharedMessage;
import com.beauty.ui.MainActivity;
import com.example.beauty.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import android.annotation.TargetApi;
import android.content.IntentSender.SendIntentException;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class MainpageFragment extends Fragment {
	public static PullToRefreshListView homeListView; //主页面自定义ListView
	public static HomeListViewAdapter homeListViewAdapter;

	private int clear = 0; //判断是否清空刷新前的数据
	
	private int i; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mainpage_fragment, group, false);
		//设置两端刷新
		homeListView = (PullToRefreshListView) view.findViewById(R.id.mainpage_fragment_listview);
		homeListView.setMode(Mode.BOTH);
		
		//上拉和下拉的刷新监听
		homeListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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
                query.findObjects(MainActivity.instance, new FindListener<SharedMessage>() 
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
								MyApplication.listItems.clear();
								clear = 1;
							}
							//添加到数据集合
							MyApplication.listItems.add(map);
						}
						//通知适配器数据集合发生改变，并停止动画
						MainActivity.handler.sendEmptyMessage(0);
						MainActivity.handler.sendEmptyMessage(1);
						Toast.makeText(MainActivity.instance, "刷新成功！", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//停止动画
						MainActivity.handler.sendEmptyMessage(1);
						Toast.makeText(MainActivity.instance, "刷新失败！", Toast.LENGTH_SHORT).show();
					}
				});
			}

			//设置上拉刷新监听
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				//需在UI主线程中停止动画，不然不成功
				MainActivity.instance.handler.sendEmptyMessage(1);
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
		homeListViewAdapter = new HomeListViewAdapter(MainActivity.instance, MyApplication.listItems);
		//设置适配器
		homeListView.setAdapter(homeListViewAdapter);
		
		return view;
	}
}
