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
	private Context context; //������
	private LinkedList<Map<String, Object>> listItems; //���ݼ���
	private LayoutInflater inflater;
	
	//ʹ��DisplayImageOptions.Builder()����DisplayImageOptions
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(null) // ����ͼƬ�����ڼ���ʾ��ͼƬ
        .showImageForEmptyUri(null) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
        .showImageOnFail(null) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ
        .cacheInMemory(true) // �������ص�ͼƬ�Ƿ񻺴����ڴ���
        .cacheOnDisk(true) // �������ص�ͼƬ�Ƿ񻺴���SD����
        .displayer(new RoundedBitmapDisplayer(20)) // ���ó�Բ��ͼƬ
        .build(); // �������
	
	//���캯��
	public CommentListViewAdapter(Context context, LinkedList<Map<String, Object>> listItems) {
		this.context = context;
		this.listItems = listItems;
		inflater = LayoutInflater.from(context);
	}
	
	//�Զ���ؼ�����
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
			
			//ͨ��id��ȡ�ؼ���view
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
