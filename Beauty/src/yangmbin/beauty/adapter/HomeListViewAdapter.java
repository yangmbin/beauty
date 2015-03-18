package yangmbin.beauty.adapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;

import yangmbin.beauty.application.MyApplication;
import yangmbin.beauty.fragment.LikeFragment;
import yangmbin.beauty.fragment.MainpageFragment;
import yangmbin.beauty.javabean.BeautyUser;
import yangmbin.beauty.javabean.Collection;
import yangmbin.beauty.javabean.LikeOrDislike;
import yangmbin.beauty.javabean.SharedMessage;
import yangmbin.beauty.ui.CommentActivity;
import yangmbin.beauty.ui.MainActivity;
import yangmbin.beauty.ui.MyPublishment;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yangmbin.beauty.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class HomeListViewAdapter extends BaseAdapter {
	private Context context;                            //运行上下文   
    private LinkedList<Map<String, Object>> listItems;  //信息集合   
    private LayoutInflater listContainer;           	//视图容器  
    
    private String pageType; //表示当前显示listview的是哪一个页面
    
    private ImageLoader imageLoader = ImageLoader.getInstance();
    //使用DisplayImageOptions.Builder()创建DisplayImageOptions
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(null) // 设置图片下载期间显示的图片
        .showImageForEmptyUri(null) // 设置图片Uri为空或是错误的时候显示的图片
        .showImageOnFail(null) // 设置图片加载或解码过程中发生错误显示的图片
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
        .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
        .build(); // 构建完成
    
    public final class ListItemView{                	//自定义控件集合     
        public ImageView headimg;     	
        public TextView nickname;     
        public TextView time;
        public ImageView sharedimg;
        
        public LinearLayout btnCommentLayout;
        public LinearLayout btnDislikeLayout;
        public LinearLayout btnLikeLayout;
        
        public TextView commentNum;
        public TextView likeNum;
        public TextView dislikeNum;
        
        public TextView id; //存放条目的id，隐藏属性
        //public TextView username;
        
        public ImageView collection; //收藏
        
    } 
    
    public HomeListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems, String pageType)
    {
    	this.context = context;
    	this.listItems = listItems;
    	listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
    	
    	this.pageType = pageType;
    }
    
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int location) {
		return listItems.get(location);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@SuppressLint("NewApi") @SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        //自定义视图   
        ListItemView  holder = null; 
        if (convertView == null) {   
            holder = new ListItemView();    
            //获取listview_school_item布局文件的视图   
            convertView = listContainer.inflate(R.layout.listview_home_item, null); 
            
            //获取控件对象  
            holder.headimg = (ImageView)convertView.findViewById(R.id.listview_home_item_headimg);
            holder.nickname = (TextView)convertView.findViewById(R.id.listview_home_item_nickname);
            holder.time = (TextView)convertView.findViewById(R.id.listview_home_item_time);
            holder.sharedimg = (ImageView) convertView.findViewById(R.id.listview_home_item_sharedimg);
            
            holder.btnCommentLayout = (LinearLayout) convertView.findViewById(R.id.listview_home_item_comment_layout);
            holder.btnDislikeLayout = (LinearLayout) convertView.findViewById(R.id.listview_home_item_dislike_layout);
            holder.btnLikeLayout = (LinearLayout) convertView.findViewById(R.id.listview_home_item_like_layout);
            
            holder.commentNum = (TextView) convertView.findViewById(R.id.listview_home_item_comment_textview);
            holder.likeNum = (TextView) convertView.findViewById(R.id.listview_home_item_like_textview);
            holder.dislikeNum = (TextView) convertView.findViewById(R.id.listview_home_item_dislike_textview);
            
            holder.id = (TextView) convertView.findViewById(R.id.listview_home_item_id);
            //listItemView.username = (TextView) convertView.findViewById(R.id.listview_home_item_username);
            
            holder.collection = (ImageView) convertView.findViewById(R.id.listview_home_item_collection);
            
            
            //设置控件集到convertView   
            convertView.setTag(holder);
        }
        else {
        	holder = (ListItemView)convertView.getTag();
        }
        
        //设置文字和图片
        //holder.headimg.setBackground(new BitmapDrawable((Bitmap) listItems.get(position).get("headimg")));
        imageLoader.displayImage((String) listItems.get(position).get("headimg"), holder.headimg, options);
        holder.nickname.setText((String) listItems.get(position).get("nickname"));
        holder.time.setText((String) listItems.get(position).get("time"));
        //holder.sharedimg.setBackground(new BitmapDrawable((Bitmap) listItems.get(position).get("sharedimg")));
        imageLoader.displayImage((String) listItems.get(position).get("sharedimg"), holder.sharedimg, options);
        /*
        holder.headimg.setTag((String) listItems.get(position).get("headimg"));
        holder.sharedimg.setTag((String) listItems.get(position).get("sharedimg"));
        */
        
        holder.commentNum.setText((String) listItems.get(position).get("commentNum"));
        holder.likeNum.setText((String) listItems.get(position).get("likeNum"));
        holder.dislikeNum.setText((String) listItems.get(position).get("dislikeNum"));
        
        holder.id.setText((String) listItems.get(position).get("id"));
        //listItemView.username.setText((String) listItems.get(position).get("username"));
        
    
        //对 评论、 喜欢 、不喜欢 进行监听
        holder.btnCommentLayout.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				//跳转到评论页面
				Intent intent = new Intent(context, CommentActivity.class);
				intent.putExtra("articleID", (String) listItems.get(position).get("id"));
				intent.putExtra("itemID", position);
				intent.putExtra("pageType", pageType);
				context.startActivity(intent);
			}
		});
        holder.btnDislikeLayout.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				//构造点赞的对象
				LikeOrDislike lod = new LikeOrDislike();
				lod.setArticleID((String) listItems.get(position).get("id"));
				lod.setUsername(BmobUser.getCurrentUser(context, BeautyUser.class).getUsername());
				lod.setLike("no");
				lod.setDislike("yes");
				lod.save(context, new SaveListener()
				{
					@Override
					public void onSuccess() {
						Toast.makeText(context, "踩成功！", Toast.LENGTH_SHORT).show();
						int dislikeNum = Integer.parseInt((String) MyApplication.listItems.get(position).get("dislikeNum")) + 1;
						//更新前一个页面的踩数
						if (pageType.equals("mainPage")) 
						{
							MyApplication.listItems.get(position).put("dislikeNum", Integer.toString(dislikeNum));
							MainpageFragment.homeListViewAdapter.notifyDataSetChanged();
						}
						else if (pageType.equals("likePage"))
						{
							LikeFragment.listItems.get(position).put("dislikeNum", Integer.toString(dislikeNum));
							LikeFragment.likeListViewAdapter.notifyDataSetChanged();
						}
						else if (pageType.equals("publishPage"))
						{
							MyPublishment.listItems.get(position).put("dislikeNum", Integer.toString(dislikeNum));
							MyPublishment.mypublishmentListViewAdapter.notifyDataSetChanged();
						}
						
						//更新SharedMessage表的dislike数
						SharedMessage sm = new SharedMessage();
						sm.setDislike(Integer.parseInt((String) listItems.get(position).get("dislikeNum")));
						sm.update(context, (String) listItems.get(position).get("id"), new UpdateListener() 
						{
							@Override
							public void onSuccess() {
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
							}
						});
					}
					
					@Override
					public void onFailure(int code, String msg) {
						if (code == 401)
							Toast.makeText(context, "已赞或踩！", Toast.LENGTH_SHORT).show();
						else 
							Toast.makeText(context, "网络错误，赞或踩失败！" + msg, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
        holder.btnLikeLayout.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				//构造点赞的对象
				LikeOrDislike lod = new LikeOrDislike();
				lod.setArticleID((String) listItems.get(position).get("id"));
				lod.setUsername(BmobUser.getCurrentUser(context, BeautyUser.class).getUsername());
				lod.setLike("yes");
				lod.setDislike("no");
				lod.save(context, new SaveListener()
				{
					@Override
					public void onSuccess() {
						Toast.makeText(context, "赞成功！", Toast.LENGTH_SHORT).show();
						int likeNum = Integer.parseInt((String) MyApplication.listItems.get(position).get("likeNum")) + 1;
						//更新前一个页面的踩数
						if (pageType.equals("mainPage")) 
						{
							MyApplication.listItems.get(position).put("likeNum", Integer.toString(likeNum));
							MainpageFragment.homeListViewAdapter.notifyDataSetChanged();
						}
						else if (pageType.equals("likePage"))
						{
							LikeFragment.listItems.get(position).put("likeNum", Integer.toString(likeNum));
							LikeFragment.likeListViewAdapter.notifyDataSetChanged();
						}
						else if (pageType.equals("publishPage"))
						{
							MyPublishment.listItems.get(position).put("likeNum", Integer.toString(likeNum));
							MyPublishment.mypublishmentListViewAdapter.notifyDataSetChanged();
						}
						
						//更新SharedMessage表的like数
						SharedMessage sm = new SharedMessage();
						sm.setLike(Integer.parseInt((String) listItems.get(position).get("likeNum")));
						sm.update(context, (String) listItems.get(position).get("id"), new UpdateListener() 
						{
							@Override
							public void onSuccess() {
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
							}
						});
					}
					
					@Override
					public void onFailure(int code, String msg) {
						if (code == 401)
							Toast.makeText(context, "已赞或踩！", Toast.LENGTH_SHORT).show();
						else  
							Toast.makeText(context, "网络错误，赞或踩失败！" + msg, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
        
        /*设置分享的图片高度为屏幕宽度*/
		//获取屏幕宽度
    	DisplayMetrics dm = new DisplayMetrics();
    	MainActivity.instance.getWindowManager().getDefaultDisplay().getMetrics(dm);
    	int width = dm.widthPixels;
    	//构造参数 
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	params.height = width - (int) (20 * (MainActivity.instance.getResources().getDisplayMetrics().density) + 0.5f);
    	holder.sharedimg.setLayoutParams(params);
    	
    	/*
    	//异步加载图片
    	Map<String, Object> map1 = new HashMap<String, Object>();
    	map1.put("view", holder.headimg);
    	map1.put("url", listItems.get(position).get("headimg"));
    	new DownloadPicFromURL().execute(map1);
    	
    	Map<String, Object> map2 = new HashMap<String, Object>();
    	map2.put("view", holder.sharedimg);
    	map2.put("url", listItems.get(position).get("sharedimg"));
    	new DownloadPicFromURL().execute(map2);
    	*/
    	
    	//对收藏按钮进行监听
    	holder.collection.setOnClickListener(new OnClickListener()
    	{
			@Override
			public void onClick(View arg0) {
				Collection c = new Collection();
				c.setUsername(BmobUser.getCurrentUser(context, BeautyUser.class).getUsername());
				c.setArticleID((String) listItems.get(position).get("id"));
				c.save(context, new SaveListener()
				{
					@Override
					public void onSuccess() {
						Toast.makeText(context, "收藏成功！", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onFailure(int code, String msg) {
						if (code == 401)
							Toast.makeText(context, "已收藏！", Toast.LENGTH_SHORT).show();
						else  
							Toast.makeText(context, "网络错误，收藏失败！" + msg, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
    	
    	
    
		return convertView;
	}
	
	//异步加载listview中的图片
	public class DownloadPicFromURL extends AsyncTask<Map<String, Object>, Void, Drawable>
	{
		private View view;
		private String urlStr;
		@Override
		protected Drawable doInBackground(Map<String, Object>... arg0) {
			Drawable result = null;
			try {
				result = Drawable.createFromStream(new URL((String) arg0[0].get("url")).openStream(), null);
				view = (View) arg0[0].get("view");
				urlStr = (String) arg0[0].get("url");
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
			return result;
		}
		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Drawable result) {
			if (result != null && view.getTag() != null && view.getTag().equals(urlStr))
				view.setBackgroundDrawable(result);
			super.onPostExecute(result);
		}
	}
}
