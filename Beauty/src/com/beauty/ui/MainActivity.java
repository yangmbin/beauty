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
        
        //����SlideMenu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // ���ô�����Ļ��ģʽ  
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // ���û����˵���ͼ�Ŀ��  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // ���ý��뽥��Ч����ֵ  
        menu.setFadeDegree(0.35f);
        //����activity
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT); 
        //Ϊ�໬�˵����ò���  
        menu.setMenu(R.layout.left_menu);
        
        
        //����Ĭ�ϵ�Fragment
        setDefaultFragment();
    }
    
    /*
     * Ĭ�ϵ�Fragment
     */
    private void setDefaultFragment() {
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	fragment1 = new MainpageFragment();
    	transaction.replace(R.id.main_activity_framelayout, fragment1);
    	transaction.commit();
    }
	
	/*
	 * ��ҳ��߰�ť���򿪲����
	 */
	public void mainPageLeftToOpenSlideMenu(View v) {
		menu.showMenu();
	}
	
	/*
	 * ��ҳ�ұ߰�ť������������ѡ��Ի���
	 */
	public void getPhoto(View v) {
		//��ʾ����ת��������ͼƬ������������ͷ��
		Intent intent = new Intent(MainActivity.this, TakeAndChoosePhoto.class);
		intent.putExtra("setHeadimg", "no");
		startActivity(intent);
	}
	
	/*
	 * �Ӳ������-��ҳ
	 */
	public void slideMenuToHome(View v) {
		if (fragment1 == null)
    		fragment1 = new MainpageFragment();
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.main_activity_framelayout, fragment1);
    	transaction.commit();
    	
    	//�رղ����
    	menu.toggle();
    	
    	//�������Ͻ�ͼƬ�ɼ�
    	ImageButton camera = (ImageButton) findViewById(R.id.main_activity_camera_btn);
    	camera.setVisibility(View.VISIBLE);
	}
	
	/*
	 * �Ӳ������-��ϲ��
	 */
	public void slideMenuToLike(View v) {
		if (fragment2 == null)
    		fragment2 = new LikeFragment();
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.main_activity_framelayout, fragment2);
    	transaction.commit();
    	
    	//�رղ����
    	menu.toggle();
    	
    	//�������Ͻ�ͼƬ���ɼ�
    	ImageButton camera = (ImageButton) findViewById(R.id.main_activity_camera_btn);
    	camera.setVisibility(View.GONE);
	}
	
	/*
	 * �Ӳ������-����
	 */
	public void slideMenuToSetting(View v) {
		if (fragment3 == null)
    		fragment3 = new SettingFragment();
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.main_activity_framelayout, fragment3);
    	transaction.commit();
    	
		//�رղ����
    	menu.toggle();
    	
    	//�������Ͻ�ͼƬ���ɼ�
    	ImageButton camera = (ImageButton) findViewById(R.id.main_activity_camera_btn);
    	camera.setVisibility(View.GONE);
	}
	
	/*
	 * �Ӳ������-����
	 */
	public void slideMenuToAbout(View v) {
		if (fragment4 == null)
    		fragment4 = new AboutFragment();
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.main_activity_framelayout, fragment4);
    	transaction.commit();
    	
		//�رղ����
    	menu.toggle();
    	
    	//�������Ͻ�ͼƬ���ɼ�
    	ImageButton camera = (ImageButton) findViewById(R.id.main_activity_camera_btn);
    	camera.setVisibility(View.GONE);
	}
	
	/*
	 * ������ؼ��������Ƿ��˳��Ի���
	 */
	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		if (keyCode == KeyEvent.KEYCODE_BACK) {
  			startActivity(new Intent(MainActivity.this, Exit.class));
  		}
  		return super.onKeyDown(keyCode, event);
  	}
	
	/*
	 * ���һ���࣬ʵ�� Comparator �ӿڣ�����listItems����
	 */
	public static class myComparator implements Comparator<Map<String, Object>>
	{

		@Override
		public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
			 return ((String) arg1.get("time")).compareTo((String) arg0.get("time"));
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
				MainpageFragment.homeListViewAdapter.notifyDataSetChanged();
			}
			//��ҳ�رն���
			else if(msg.what == 1) {
				MainpageFragment.homeListView.onRefreshComplete();
			}
			
			//ϲ��ҳ���������򡢸���
			else if (msg.what == 2) {
				Collections.sort(LikeFragment.listItems, new myComparator());
				LikeFragment.likeListViewAdapter.notifyDataSetChanged();
				Toast.makeText(MainActivity.instance, "ˢ�³ɹ���", Toast.LENGTH_SHORT).show();
			}
			//ϲ��ҳ��رն���
			else if (msg.what == 3) {
				LikeFragment.likeListView.onRefreshComplete();
			}
		}
	};
}
