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
	//ʹ��DisplayImageOptions.Builder()����DisplayImageOptions
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(null) // ����ͼƬ�����ڼ���ʾ��ͼƬ
        .showImageForEmptyUri(null) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
        .showImageOnFail(null) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ
        .cacheInMemory(true) // �������ص�ͼƬ�Ƿ񻺴����ڴ���
        .cacheOnDisk(true) // �������ص�ͼƬ�Ƿ񻺴���SD����
        //.displayer(new RoundedBitmapDisplayer(20)) // ���ó�Բ��ͼƬ
        .build(); // �������
    
    public static View settingFragmentView;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		settingFragmentView = inflater.inflate(R.layout.setting_fragment, group, false);
		
		//��ʾ�û�ͷ��
		ImageView headImageView = (ImageView) settingFragmentView.findViewById(R.id.setting_fragment_headimg);
		ImageLoader.getInstance().displayImage(
				BmobUser.getCurrentUser(MainActivity.instance, BeautyUser.class).getHeadimgURL(), headImageView, options);
		//�޸�ͷ��
		RelativeLayout setHeadimgRelativeLayout = (RelativeLayout) settingFragmentView.findViewById(R.id.setting_fragment_set_headimg_layout);
		setHeadimgRelativeLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				//��ʾ����ת��������ͷ�񣬶����Ƿ���ͼƬ
				Intent intent = new Intent(MainActivity.instance, TakeAndChoosePhoto.class);
				intent.putExtra("setHeadimg", "yes");
				startActivity(intent);
			}
		});
		
		//��ʾ�ǳ�
		TextView nicknameTextView = (TextView) settingFragmentView.findViewById(R.id.setting_fragment_nickname);
		nicknameTextView.setText(BmobUser.getCurrentUser(MainActivity.instance, BeautyUser.class).getNickname());
		//�޸��ǳ�
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
		
		//��ʾǩ��
		TextView signatureTextView = (TextView) settingFragmentView.findViewById(R.id.setting_fragment_signature);
		signatureTextView.setText(BmobUser.getCurrentUser(MainActivity.instance, BeautyUser.class).getSignature());
		//�޸�ǩ��
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
		
		//��ת���ҷ�������Ϣҳ��
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
