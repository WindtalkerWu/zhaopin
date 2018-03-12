package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 消息版块列表Adapter类
 */
public class ForumListAdapter extends BaseAdapter {
	private final static String TAG = "ForumListAdapter";
	private Activity context;// 运行上下文
	private List<ForumType> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private BitmapManager bmpManager;
	

	public static class ForumListItemView { // 自定义控件集合
		public TextView title;
		public TextView digest;
		public TextView date;
		public TextView count;
		public ImageView unreaderFlag;;
		public ImageView face;
		public ForumType infotype;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ForumListAdapter(Activity context, List<ForumType> data) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文

		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.qianniu_logobg));

	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int position) {
		listItems.get(position);
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		// 自定义视图
		ForumListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.forumtype_listitem,
					null);

			listItemView = new ForumListItemView();
			// 获取控件对象
			listItemView.title = (TextView) convertView
					.findViewById(R.id.infotype_listitem_title);
			listItemView.digest = (TextView) convertView
					.findViewById(R.id.infotype_listitem_digest);
			listItemView.count = (TextView) convertView
					.findViewById(R.id.infotype_listitem_count);
			listItemView.unreaderFlag = (ImageView) convertView
					.findViewById(R.id.infotype_listitem_flag);
			listItemView.date = (TextView) convertView
					.findViewById(R.id.infotype_listitem_date);
			listItemView.face = (ImageView) convertView
					.findViewById(R.id.infotype_listitem_userface);
			listItemView.infotype = listItems.get(position);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ForumListItemView) convertView.getTag();
			listItemView.infotype = listItems.get(position);
		}

		// 设置文字和图片
		ForumType infotype = listItemView.infotype;

		String faceURL = infotype.getInfoLogo_url();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
			listItemView.face.setImageResource(R.drawable.widget_dface);
		} else {
			CommonRoundImgCreator creator = new CommonRoundImgCreator(context,
					Constants.InfoModule.LIST_ITEM_FACEIMG_RADIUS, 0, 0);


			bmpManager.loadBitmap(faceURL, listItemView.face, null,
					creator);
		}
		// listItemView.face.setOnClickListener(faceClickListener);
		listItemView.face.setTag(infotype);

		listItemView.title.setText(infotype.getTitle());
		listItemView.title.setTag(infotype);// 设置隐藏参数(实体类)
		listItemView.digest.setText(infotype.getDigest());

		String date = infotype.getTimeStamp();
		
		String title = infotype.getTitle();
		String digest = infotype.getDigest();
		listItemView.date.setText(StringUtils.friendly_time(infotype
				.getTimeStamp()));
		int count = infotype.getNewCount();
		listItemView.count.setText(count + "");
		MyLogger.getLogger(TAG).i("new msg count = " + count +";title = "+ title);
		if (count > 0) {
			listItemView.unreaderFlag.setVisibility(View.VISIBLE);
		} else {
			listItemView.unreaderFlag.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
}