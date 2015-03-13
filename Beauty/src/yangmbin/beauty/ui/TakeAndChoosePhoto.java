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
	//���ú�ͼƬ�ı���λ��
	File croppedFile = new File(Environment.getExternalStorageDirectory(), "photoImg1.jpg");
	//��ʾ��������ͷ���Ƿ���ͼƬ����case�зֱ���
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
	 * �����ѡ��ͼƬ
	 */
	public void choosePhoto(View v) {
		//���
        Intent localIntent = new Intent("android.intent.action.PICK", null);
        localIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        TakeAndChoosePhoto.this.startActivityForResult(localIntent, 1);
	}
	/*
	 * ���ջ�ȡͼƬ
	 */
	public void takePhoto(View v) {
		//ͨ�����ջ�ȡ��Ƭ
        Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        localIntent.putExtra("output", Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photoImg.jpg")));
        TakeAndChoosePhoto.this.startActivityForResult(localIntent, 2);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * ��ȡ����Ƭ��Ļص�����
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	//�����ֱ�ӻ�ȡ
    	case 1:
    		if(data != null) {
    			/*
    			//��Uri��ȡ����е���Ƭ
    			Bitmap localBitmap = getBitmapFromUri(data.getData());
    			//���ڴ濨���½�һ����ѡ����Ƭ�Ŀ���������ԭͼƬ�������޸ĵ�
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
    	// ����ǵ����������ʱ  
    	case 2:
    		File temp = new File(Environment.getExternalStorageDirectory() + "/photoImg.jpg");
    		startPhotoZoom(Uri.fromFile(temp));
    		break;
    	// ȡ�òü����ͼƬ 
    	case 3:
    		//�ǿ��жϴ��һ��Ҫ��֤���������֤�Ļ���  
            //�ڼ���֮��������ֲ����⣬Ҫ���²ü�������  
            //��ǰ����ʱ���ᱨNullException
    		startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
    		TakeAndChoosePhoto.this.finish();
    		if(data != null) {
    			/*
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("username", "����");
    			map.put("nickname", "�ǳ�");
    			map.put("headimg", BitmapFactory.decodeResource(getResources(), R.drawable.headimg));
    			map.put("sharedimg", getBitmapFromUri(Uri.fromFile(croppedFile)));
    			MainpageFragment.listItems.addFirst(map);
    			MainpageFragment.homeListViewAdapter.notifyDataSetChanged();
    			*/
    			
    			//���ϴ�ͼƬ������URL���ٰѷ����������¼д���̨
    			final BmobFile bmobFile = new BmobFile(croppedFile);
    			bmobFile.uploadblock(TakeAndChoosePhoto.this, new UploadFileListener() {
					@Override
					public void onSuccess() {
		    			//����������ݵĶ���
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
								//�������ݼ��ϣ����·�������ݼ�����ҳͷ��
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
				    			Toast.makeText(MainActivity.instance, "�����ɹ���", Toast.LENGTH_SHORT).show();
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								LoadingActivity.instance.finish();
								Toast.makeText(MainActivity.instance, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
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
    	//��ʾ�޸��û�ͷ��
    	case 4:
    		startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
    		TakeAndChoosePhoto.this.finish();
    		if (data != null)
    		{
    			//�ϴ�ͷ�񣬻�ȡURL
    			final BmobFile headimg = new BmobFile(croppedFile);
    			headimg.uploadblock(TakeAndChoosePhoto.this, new UploadFileListener()
    			{
					@SuppressLint("NewApi") @Override
					public void onSuccess() {
						//�޸����ݿ��е�BeautyUser���е�ͷ���ֶ�
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
						//�޸����ݿ��е�SharedMessage���е�ͷ���ֶ�
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
						//�޸����ݿ��е�Comment���е�ͷ���ֶ�
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
						
						
						//��ImageView����Ϊ�޸ĺ��ͷ��
						ImageView headImageView = (ImageView) SettingFragment.settingFragmentView.findViewById(R.id.setting_fragment_headimg);
						headImageView.setImageBitmap(getBitmapFromUri(Uri.fromFile(croppedFile)));
			
						LoadingActivity.instance.finish();
						Toast.makeText(MainActivity.instance, "�ϴ��ɹ���", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						LoadingActivity.instance.finish();
						Toast.makeText(MainActivity.instance, "�ϴ�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
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
     * ��Uri�л�ȡBitmap����ͼƬ
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
     * ������Ƭ�Ĳ���
     */
    public void startPhotoZoom(Uri paramUri)
    {
    	//��ȡ��Ļ���
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
	    localIntent.putExtra("output", Uri.fromFile(croppedFile)); //���·��
	    
	    //����ͼƬ��������ͷ��
	    if (setHeadimg.equals("no"))
	    	startActivityForResult(localIntent, 3);
	    else if(setHeadimg.equals("yes"))
	    	startActivityForResult(localIntent, 4);
    }
}
