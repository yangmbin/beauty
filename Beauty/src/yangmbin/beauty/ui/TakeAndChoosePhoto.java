package yangmbin.beauty.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import yangmbin.beauty.application.MyApplication;
import yangmbin.beauty.fragment.MainpageFragment;
import yangmbin.beauty.fragment.SettingFragment;
import yangmbin.beauty.javabean.BeautyUser;
import yangmbin.beauty.javabean.Comment;
import yangmbin.beauty.javabean.SharedMessage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.yangmbin.beauty.R;

public class TakeAndChoosePhoto extends Activity {
	private LinearLayout layout;
	//剪裁后图片的保存位置
	File croppedFile = new File(Environment.getExternalStorageDirectory(), "photoImg1.jpg");
	//表示用来设置头像还是分享图片，在case中分别处理
    String setHeadimg = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.take_and_choose_photo);
		
		layout=(LinearLayout)findViewById(R.id.take_and_choose_photo_layout);
		layout.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
			}
		});
		
		setHeadimg = getIntent().getExtras().getString("setHeadimg");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	/*
	 * 从相册选择图片
	 */
	public void choosePhoto(View v) {
		//相册
        Intent localIntent = new Intent("android.intent.action.PICK", null);
        localIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        TakeAndChoosePhoto.this.startActivityForResult(localIntent, 1);
	}
	/*
	 * 拍照获取图片
	 */
	public void takePhoto(View v) {
		//通过拍照获取照片
        Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        localIntent.putExtra("output", Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photoImg.jpg")));
        TakeAndChoosePhoto.this.startActivityForResult(localIntent, 2);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * 获取到照片后的回调函数
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	//从相册直接获取
    	case 1:
    		if(data != null) {
    			/*
    			//从Uri获取相册中的照片
    			Bitmap localBitmap = getBitmapFromUri(data.getData());
    			//在内存卡中新建一个被选择照片的拷贝，避免原图片被剪裁修改掉
    	        File localFile = new File(Environment.getExternalStorageDirectory(), "photoImg1.jpg");
    	        try
    	        {
	    	        FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
	    	        localBitmap.compress(Bitmap.CompressFormat.PNG, 100, localFileOutputStream);
	    	        localFileOutputStream.flush();
	    	        localFileOutputStream.close();
    	        	
	    	        startPhotoZoom(Uri.fromFile(localFile));
    	        }
    	        catch (Exception localException)
    	        {
    	            localException.printStackTrace();
    	        }
    	        */
    			startPhotoZoom(data.getData());
    		}
    		break;
    	// 如果是调用相机拍照时  
    	case 2:
    		File temp = new File(Environment.getExternalStorageDirectory() + "/photoImg.jpg");
    		startPhotoZoom(Uri.fromFile(temp));
    		break;
    	// 取得裁剪后的图片 
    	case 3:
    		//非空判断大家一定要验证，如果不验证的话，  
            //在剪裁之后如果发现不满意，要重新裁剪，丢弃  
            //当前功能时，会报NullException
    		startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
    		TakeAndChoosePhoto.this.finish();
    		if(data != null) {
    			/*
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("username", "姓名");
    			map.put("nickname", "昵称");
    			map.put("headimg", BitmapFactory.decodeResource(getResources(), R.drawable.headimg));
    			map.put("sharedimg", getBitmapFromUri(Uri.fromFile(croppedFile)));
    			MainpageFragment.listItems.addFirst(map);
    			MainpageFragment.homeListViewAdapter.notifyDataSetChanged();
    			*/
    			
    			//先上传图片，返回URL后再把分享的这条记录写入后台
    			final BmobFile bmobFile = new BmobFile(croppedFile);
    			bmobFile.uploadblock(TakeAndChoosePhoto.this, new UploadFileListener() {
					@Override
					public void onSuccess() {
		    			//构造分享内容的对象
						final SharedMessage sm = new SharedMessage();
						sm.setUsername(BmobUser.getCurrentUser(TakeAndChoosePhoto.this, BeautyUser.class).getUsername());
						sm.setSharedimgURL(bmobFile.getFileUrl(TakeAndChoosePhoto.this));
						sm.setNickname(BmobUser.getCurrentUser(TakeAndChoosePhoto.this, BeautyUser.class).getNickname());
						sm.setHeadimgURL(BmobUser.getCurrentUser(TakeAndChoosePhoto.this, BeautyUser.class).getHeadimgURL());
						sm.setLike(0);
						sm.setDislike(0);
						sm.setComment(0);
						sm.save(TakeAndChoosePhoto.this, new SaveListener() {
							@Override
							public void onSuccess() {
								//更新数据集合，把新分享的内容加在主页头部
								Map<String, Object> map = new HashMap<String, Object>();
				    			map.put("username", sm.getUsername());
				    			map.put("nickname", sm.getNickname());
				    			map.put("headimg", sm.getHeadimgURL());
				    			map.put("sharedimg", sm.getSharedimgURL());
				    			map.put("id", sm.getObjectId());
				    			map.put("likeNum", sm.getLike().toString());
				    			map.put("dislikeNum", sm.getDislike().toString());
				    			map.put("commentNum", sm.getComment().toString());
				    			MyApplication.listItems.addFirst(map);
				    			MainpageFragment.homeListViewAdapter.notifyDataSetChanged();
				    			
				    			LoadingActivity.instance.finish();
				    			Toast.makeText(MainActivity.instance, "发布成功！", Toast.LENGTH_SHORT).show();
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								LoadingActivity.instance.finish();
								Toast.makeText(MainActivity.instance, "发布失败！", Toast.LENGTH_SHORT).show();
							}
						});
					}
					@Override
					public void onFailure(int arg0, String arg1) {
						LoadingActivity.instance.finish();
					}
				});
    		}
    		break;
    	//表示修改用户头像
    	case 4:
    		startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
    		TakeAndChoosePhoto.this.finish();
    		if (data != null)
    		{
    			//上传头像，获取URL
    			final BmobFile headimg = new BmobFile(croppedFile);
    			headimg.uploadblock(TakeAndChoosePhoto.this, new UploadFileListener()
    			{
					@SuppressLint("NewApi") @Override
					public void onSuccess() {
						//修改数据库中的BeautyUser表中的头像字段
						BeautyUser user = new BeautyUser();
						user.setHeadimgURL(headimg.getFileUrl(getApplicationContext()));
						user.update(getApplicationContext(), BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getObjectId(), new UpdateListener() 
						{
							@Override
							public void onSuccess() {
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
							}
						});
						//修改数据库中的SharedMessage表中的头像字段
						BmobQuery<SharedMessage> query = new BmobQuery<SharedMessage>();
						query.addWhereEqualTo("username", BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getUsername());
						query.findObjects(getApplicationContext(), new FindListener<SharedMessage>() {
							
							@Override
							public void onSuccess(List<SharedMessage> list) {
								for (int i = 0; i < list.size(); i++) {
									SharedMessage sm = new SharedMessage();
									sm.setHeadimgURL(headimg.getFileUrl(getApplicationContext()));
									sm.update(getApplicationContext(), list.get(i).getObjectId(), null);
								}
							}
							
							@Override
							public void onError(int arg0, String arg1) {
							}
						});
						//修改数据库中的Comment表中的头像字段
						BmobQuery<Comment> query2 = new BmobQuery<Comment>();
						query2.addWhereEqualTo("nickname", BmobUser.getCurrentUser(getApplicationContext(), BeautyUser.class).getNickname());
						query2.findObjects(getApplicationContext(), new FindListener<Comment>() {
							
							@Override
							public void onSuccess(List<Comment> list) {
								for (int i = 0; i < list.size(); i++) {
									Comment c = new Comment();
									c.setHeadimgURL(headimg.getFileUrl(getApplicationContext()));
									c.update(getApplicationContext(), list.get(i).getObjectId(), null);
								}
							}
							
							@Override
							public void onError(int arg0, String arg1) {
							}
						});
						
						
						//把ImageView设置为修改后的头像
						ImageView headImageView = (ImageView) SettingFragment.settingFragmentView.findViewById(R.id.setting_fragment_headimg);
						headImageView.setImageBitmap(getBitmapFromUri(Uri.fromFile(croppedFile)));
			
						LoadingActivity.instance.finish();
						Toast.makeText(MainActivity.instance, "上传成功！", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						LoadingActivity.instance.finish();
						Toast.makeText(MainActivity.instance, "上传失败！", Toast.LENGTH_SHORT).show();
					}
				});
    		}
    		break;
		default:
			break;
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    } 
    
    /*
     * 从Uri中获取Bitmap类型图片
     */
    private Bitmap getBitmapFromUri(Uri paramUri)
    {
	    try
	    {
            Bitmap localBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), paramUri);
            return localBitmap;
	    }
	    catch (Exception localException)
	    {
	        localException.printStackTrace();
	    }
	    return null;
    }
    
    /*
     * 剪裁照片的操作
     */
    public void startPhotoZoom(Uri paramUri)
    {
    	//获取屏幕宽度
    	DisplayMetrics dm = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);
    	int width = dm.widthPixels;
    	
	    Intent localIntent = new Intent("com.android.camera.action.CROP");
	    localIntent.setDataAndType(paramUri, "image/*");
	    localIntent.putExtra("crop", "true");
	    localIntent.putExtra("aspectX", 1);
	    localIntent.putExtra("aspectY", 1);
	    //localIntent.putExtra("outputX", width);
	    //localIntent.putExtra("outputY", width);
	    localIntent.putExtra("scale", true);
	    localIntent.putExtra("return-data", false); 
	    localIntent.putExtra("output", Uri.fromFile(croppedFile)); //输出路径
	    
	    //分享图片还是设置头像
	    if (setHeadimg.equals("no"))
	    	startActivityForResult(localIntent, 3);
	    else if(setHeadimg.equals("yes"))
	    	startActivityForResult(localIntent, 4);
    }
}
