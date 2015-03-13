package com.beauty.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.beauty.adapter.HomeListViewAdapter;
import com.beauty.application.MyApplication;
import com.beauty.fragment.AboutFragment;
import com.beauty.fragment.MainpageFragment;
import com.beauty.fragment.LikeFragment;
import com.beauty.fragment.SettingFragment;
import com.example.beauty.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity {
	private SlidingMenu menu;
	private MainpageFragment fragment1;
	private LikeFragment fragment2;
	private SettingFragment fragment3;
	private AboutFragment fragment4;
	
	public static MainActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        instance = this;
        
        //构建SlideMenu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式  
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置滑动菜单视图的宽度  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值  
        menu.setFadeDegree(0.35f);
        //关联activity
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT); 
        //为侧滑菜单设置布局  
        menu.setMenu(R.layout.left_menu);
        
        
        //设置默认的Fragment
        setDefaultFragment();
    }
    
    /*
     * 默认的Fragment
     */
    private void setDefaultFragment() {
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	fragment1 = new MainpageFragment();
    	transaction.replace(R.id.main_activity_framelayout, fragment1);
    	transaction.commit();
    }
	
	/*
	 * 主页左边按钮，打开侧边栏
	 */
	public void mainPageLeftToOpenSlideMenu(View v) {
		menu.showMenu();
	}
	
	/*
	 * 主页右边按钮，打开相册或拍照选择对话框
	 */
	public void getPhoto(View v) {
		//表示次跳转用来分享图片，而不是设置头像
		Intent intent = new Intent(MainActivity.this, TakeAndChoosePhoto.class);
		intent.putExtra("setHeadimg", "no");
		startActivity(intent);
	}
	
	/*
	 * 从侧边栏打开-主页
	 */
	public void slideMenuToHome(View v) {
		if (fragment1 == null)
    		fragment1 = new MainpageFragment();
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.main_activity_framelayout, fragment1);
    	transaction.commit();
    	
    	//关闭侧边栏
    	menu.toggle();
    	
    	//设置右上角图片可见
    	ImageButton camera = (ImageButton) findViewById(R.id.main_activity_camera_btn);
    	camera.setVisibility(View.VISIBLE);
	}
	
	/*
	 * 从侧边栏打开-我喜欢
	 */
	public void slideMenuToLike(View v) {
		if (fragment2 == null)
    		fragment2 = new LikeFragment();
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.main_activity_framelayout, fragment2);
    	transaction.commit();
    	
    	//关闭侧边栏
    	menu.toggle();
    	
    	//设置右上角图片不可见
    	ImageButton camera = (ImageButton) findViewById(R.id.main_activity_camera_btn);
    	camera.setVisibility(View.GONE);
	}
	
	/*
	 * 从侧边栏打开-设置
	 */
	public void slideMenuToSetting(View v) {
		if (fragment3 == null)
    		fragment3 = new SettingFragment();
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.main_activity_framelayout, fragment3);
    	transaction.commit();
    	
		//关闭侧边栏
    	menu.toggle();
    	
    	//设置右上角图片不可见
    	ImageButton camera = (ImageButton) findViewById(R.id.main_activity_camera_btn);
    	camera.setVisibility(View.GONE);
	}
	
	/*
	 * 从侧边栏打开-关于
	 */
	public void slideMenuToAbout(View v) {
		if (fragment4 == null)
    		fragment4 = new AboutFragment();
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.main_activity_framelayout, fragment4);
    	transaction.commit();
    	
		//关闭侧边栏
    	menu.toggle();
    	
    	//设置右上角图片不可见
    	ImageButton camera = (ImageButton) findViewById(R.id.main_activity_camera_btn);
    	camera.setVisibility(View.GONE);
	}
	
	/*
	 * 点击返回键，出现是否退出对话框
	 */
	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		if (keyCode == KeyEvent.KEYCODE_BACK) {
  			startActivity(new Intent(MainActivity.this, Exit.class));
  		}
  		return super.onKeyDown(keyCode, event);
  	}
	
	/*
	 * 添加一个类，实现 Comparator 接口，用于listItems排序
	 */
	public static class myComparator implements Comparator<Map<String, Object>>
	{

		@Override
		public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
			 return ((String) arg1.get("time")).compareTo((String) arg0.get("time"));
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
				MainpageFragment.homeListViewAdapter.notifyDataSetChanged();
			}
			//主页关闭动画
			else if(msg.what == 1) {
				MainpageFragment.homeListView.onRefreshComplete();
			}
			
			//喜欢页面数据排序、更新
			else if (msg.what == 2) {
				Collections.sort(LikeFragment.listItems, new myComparator());
				LikeFragment.likeListViewAdapter.notifyDataSetChanged();
				Toast.makeText(MainActivity.instance, "刷新成功！", Toast.LENGTH_SHORT).show();
			}
			//喜欢页面关闭动画
			else if (msg.what == 3) {
				LikeFragment.likeListView.onRefreshComplete();
			}
		}
	};
}
