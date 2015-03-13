package yangmbin.beauty.adapter;

import java.util.LinkedList;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yangmbin.beauty.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentListViewAdapter extends BaseAdapter {
	private Context context; //上下文
	private LinkedList<Map<String, Object>> listItems; //数据集合
	private LayoutInflater inflater;
	
	//使用DisplayImageOptions.Builder()创建DisplayImageOptions
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(null) // 设置图片下载期间显示的图片
        .showImageForEmptyUri(null) // 设置图片Uri为空或是错误的时候显示的图片
        .showImageOnFail(null) // 设置图片加载或解码过程中发生错误显示的图片
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
        .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
        .build(); // 构建完成
	
	//构造函数
	public CommentListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems) {
		this.context = context;
		this.listItems = listItems;
		inflater = LayoutInflater.from(context);
	}
	
	//自定义控件集合
	public final class Holder
	{
		ImageView headimg;
		TextView nickname, time, comment;
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

	@Override
	public View getView(int location, View converView, ViewGroup parent) {
		Holder holder = null;
		if (converView == null) {
			holder = new Holder();
			converView = inflater.inflate(R.layout.listview_comment_item, null);
			
			//通过id获取控件的view
			holder.headimg = (ImageView) converView.findViewById(R.id.listview_comment_item_headimg);
			holder.nickname = (TextView) converView.findViewById(R.id.listview_comment_item_nickname);
			holder.time = (TextView) converView.findViewById(R.id.listview_comment_item_time);
			holder.comment = (TextView) converView.findViewById(R.id.listview_comment_item_comment);
			
			converView.setTag(holder);
		}
		else {
			holder = (Holder) converView.getTag();
		}
		
		ImageLoader.getInstance().displayImage((String) listItems.get(location).get("headimg"), holder.headimg, options); 
		holder.nickname.setText((String) listItems.get(location).get("nickname"));
		holder.time.setText((String) listItems.get(location).get("time"));
		holder.comment.setText((String) listItems.get(location).get("comment"));
		
		return converView;
	}

}
