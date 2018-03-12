package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.adapter.ForumListAdapter.ForumListItemView;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.ui.InfoListCommonItem;
import com.qianniu.zhaopin.app.ui.InfoListMultiImgTxtItem;
import com.qianniu.zhaopin.app.ui.InfoListSimpleImgTxtItem;
import com.qianniu.zhaopin.app.ui.InfoListTxtItem;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 消息版块，版块二级消息列表Adapter类
 * 
 * @author Administrator
 * 
 */
public class ForumInfoListAdapt extends BaseAdapter {
	private Context context;// 运行上下文
	private List<ItemInfoEntity> listItems;// 数据集合
	private LayoutInflater listContainer;
	private int itemViewResource;
	private BitmapManager bmpManager;

	public static class ForumInfoListItemView {

		public ItemInfoEntity foruminfo;
		public InfoListCommonItem commonItem;
		public InfoListTxtItem txtItem;
		public InfoListSimpleImgTxtItem simpleImgTxtItem;
		public InfoListMultiImgTxtItem multiImgTxtTtem;

		public void inVisbleAllView() {
			if (commonItem != null)
				commonItem.setVisibility(View.GONE);
			if (txtItem != null)
				txtItem.setVisibility(View.GONE);
			if (simpleImgTxtItem != null)
				simpleImgTxtItem.setVisibility(View.GONE);
			if (multiImgTxtTtem != null)
				multiImgTxtTtem.setVisibility(View.GONE);
		}

		public void VisbleView(ItemInfoEntity iteminfo) {
			if (iteminfo == null || iteminfo.getInfoEntitylist() == null
					|| iteminfo.getInfoEntitylist().size() == 0)
				return;
			inVisbleAllView();
			switch (iteminfo.getViewtype()) {
			case 1:
				if (txtItem != null) {
					txtItem.bind(iteminfo.getInfoEntitylist().get(0), true);				
				}
				break;
			case 2:
				if (commonItem != null) {
					commonItem.bind(iteminfo.getInfoEntitylist().get(0), true);
					
				}
				break;
			case 3:
				if (simpleImgTxtItem != null) {
					simpleImgTxtItem.bind(iteminfo.getInfoEntitylist().get(0), true);
					
				}
				break;
			case 4:
				if (multiImgTxtTtem != null) {
					multiImgTxtTtem.bind(iteminfo, true);
				}
				break;

			}
			;

		}
	}

	public ForumInfoListAdapt(Context context, List<ItemInfoEntity> data) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文

		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.widget_dface_loading));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// 自定义视图
		ForumInfoListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.info_listitem_multi,
					null);

			listItemView = new ForumInfoListItemView();
			// 获取控件对象

			listItemView.commonItem = (InfoListCommonItem) convertView
					.findViewById(R.id.info_listitem_commoncontainer);
			listItemView.txtItem = (InfoListTxtItem) convertView
					.findViewById(R.id.info_listitem_txtcontainer);
			listItemView.simpleImgTxtItem = (InfoListSimpleImgTxtItem) convertView
					.findViewById(R.id.info_listitem_simple_imgtxtcontainer);
			listItemView.multiImgTxtTtem = (InfoListMultiImgTxtItem) convertView
					.findViewById(R.id.info_listitem_multi_imgtxtcontainer);
			listItemView.foruminfo = listItems.get(position);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ForumInfoListItemView) convertView.getTag();
			listItemView.foruminfo = listItems.get(position);
			convertView.setTag(listItemView);
		}
		ItemInfoEntity iteminfo = listItemView.foruminfo;
		listItemView.VisbleView(iteminfo);
		return convertView;

	}
}
