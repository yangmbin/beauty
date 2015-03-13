package com.beauty.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bmob.v3.BmobUser;

import com.beauty.javabean.BeautyUser;
import com.beauty.ui.MainActivity;
import com.beauty.ui.MyPublishment;
import com.beauty.ui.SetUserInfo;
import com.beauty.ui.TakeAndChoosePhoto;
import com.example.beauty.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class SettingFragment extends Fragment {
	//使用DisplayImageOptions.Builder()创建DisplayImageOptions
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(null) // 设置图片下载期间显示的图片
        .showImageForEmptyUri(null) // 设置图片Uri为空或是错误的时候显示的图片
        .showImageOnFail(null) // 设置图片加载或解码过程中发生错误显示的图片
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
        //.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
        .build(); // 构建完成
    
    public static View settingFragmentView;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		settingFragmentView = inflater.inflate(R.layout.setting_fragment, group, false);
		
		//显示用户头像
		ImageView headImageView = (ImageView) settingFragmentView.findViewById(R.id.setting_fragment_headimg);
		ImageLoader.getInstance().displayImage(
				BmobUser.getCurrentUser(MainActivity.instance, BeautyUser.class).getHeadimgURL(), headImageView, options);
		//修改头像
		RelativeLayout setHeadimgRelativeLayout = (RelativeLayout) settingFragmentView.findViewById(R.id.setting_fragment_set_headimg_layout);
		setHeadimgRelativeLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				//表示次跳转用来设置头像，而不是分享图片
				Intent intent = new Intent(MainActivity.instance, TakeAndChoosePhoto.class);
				intent.putExtra("setHeadimg", "yes");
				startActivity(intent);
			}
		});
		
		//显示昵称
		TextView nicknameTextView = (TextView) settingFragmentView.findViewById(R.id.setting_fragment_nickname);
		nicknameTextView.setText(BmobUser.getCurrentUser(MainActivity.instance, BeautyUser.class).getNickname());
		//修改昵称
		RelativeLayout setNicknameRelativeLayout = (RelativeLayout) settingFragmentView.findViewById(R.id.setting_fragment_set_nickname_layout);
		setNicknameRelativeLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.instance, SetUserInfo.class);
				intent.putExtra("setuserinfo", "nickname");
				startActivity(intent);
			}
		});
		
		//显示签名
		TextView signatureTextView = (TextView) settingFragmentView.findViewById(R.id.setting_fragment_signature);
		signatureTextView.setText(BmobUser.getCurrentUser(MainActivity.instance, BeautyUser.class).getSignature());
		//修改签名
		RelativeLayout setSignatureRelativeLayout = (RelativeLayout) settingFragmentView.findViewById(R.id.setting_fragment_set_signature_layout);
		setSignatureRelativeLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.instance, SetUserInfo.class);
				intent.putExtra("setuserinfo", "signature");
				startActivity(intent);
			}
		});
		
		//跳转到我发布的信息页面
		RelativeLayout mypublishmentLayout = (RelativeLayout) settingFragmentView.findViewById(R.id.setting_fragment_set_published_beauty_layout);
		mypublishmentLayout.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.instance, MyPublishment.class));
			}
		});
		
		return settingFragmentView;
	}
}
