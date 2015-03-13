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
	private PullToRefreshListView commentListView; //评论listview
	private CommentListViewAdapter adapter; //适配器
	private LinkedList<Map<String, Object>> listItems; //数据集合
	
	private int clear = 0; //判断是否清空刷新前的数据
	private String articleID; //传递过来的文章id
	private int itemID; //传递过来的int数据，表示点击了第几个条目
	@SuppressLint("Recycle") @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_comment);
		
		//获取文章id
		articleID = getIntent().getExtras().getString("articleID");
		
		commentListView = (PullToRefreshListView) findViewById(R.id.listview_comment_listview);
		//设置成两端刷新
		commentListView.setMode(Mode.BOTH);
		//数据集合
		listItems = new LinkedList<Map<String, Object>>(); 
		
		//在listview中默认加一条空数据才能够进入自动刷新，我也不知道为什么？
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("headimg", null);
		map.put("nickname", null);
		map.put("time", null);
		map.put("comment", null);
		listItems.add(map);
		
		
		adapter = new CommentListViewAdapter(this, listItems);
		commentListView.setAdapter(adapter);
		
		//设置刷新的监听
		commentListView.setOnRefreshListener(new OnRefreshListener2<ListView>()
		{
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
                
                //获取评论的异步操作
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
							//如果有一条以上得到了更新，那么就把先前的数据抹掉
							if (clear == 0) {
								listItems.clear();
								clear = 1;
							}
							listItems.add(map);
						}
						//通知适配器数据集合发生改变，并停止动画
						adapter.notifyDataSetChanged();
						commentListView.onRefreshComplete();
						Toast.makeText(getApplicationContext(), "刷新成功！", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						//停止动画
						commentListView.onRefreshComplete();
						Toast.makeText(MainActivity.instance, "刷新失败！", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) 
			{
				//需在UI主线程中停止动画，不然不成功
				//commentListView.onRefreshComplete();
				handler.sendEmptyMessage(0);
			}
		});
		
		//每次进入此页面时，新建手势进行自动下拉刷新
		MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), 
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, -800, 0);
		if (commentListView.isBeingDraggedToTrue()) {
			commentListView.onTouchEvent(event);			
		}
	}
	
	/*
	 * 发送评论消息
	 */
	public void sendComment(View v) {
		final EditText commentEditText = (EditText) findViewById(R.id.listview_comment_edittext);
		String commentStr = commentEditText.getText().toString().trim();
		if ("".equals(commentStr)) 
		{
			Toast.makeText(getApplicationContext(), "不能为空！", Toast.LENGTH_SHORT).show();
		}
		else
		{
			final Comment comment = new Comment();
			comment.setHeadimgURL(BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getHeadimgURL());
			comment.setNickname(BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getNickname());
			comment.setComment(commentStr);
			comment.setArticleID(getIntent().getExtras().getString("articleID"));
			//发送评论
			startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
			comment.save(getApplicationContext(), new SaveListener() 
			{
				@Override
				public void onSuccess() {
					commentEditText.setText(null);
					//添加评论到头部
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("headimg", comment.getHeadimgURL());
					map.put("nickname", comment.getNickname());
					map.put("time", comment.getCreatedAt());
					map.put("comment", comment.getComment());
					listItems.addFirst(map);
					adapter.notifyDataSetChanged();
					
					LoadingActivity.instance.finish();
					Toast.makeText(getApplicationContext(), "评论成功！", Toast.LENGTH_SHORT).show();
					
					//更新SharedMessage表的评论数
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
					Toast.makeText(getApplicationContext(), "评论失败！", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	/*
	 * 更新UI的hander
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//关闭刷新动画
			if (msg.what == 0) {
				commentListView.onRefreshComplete();
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
