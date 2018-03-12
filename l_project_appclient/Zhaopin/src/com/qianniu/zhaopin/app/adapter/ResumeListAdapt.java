package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.adapter.ForumListAdapter.ForumListItemView;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.ui.ResumeListActivity;
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
import android.widget.TextView;

public class ResumeListAdapt extends BaseAdapter{
	private final static String TAG = "ResumeListAdapt";
	private Activity context;// 运行上下文
	private List<ResumeSimpleEntity> listItems;// 数据集合
	private LayoutInflater mlayoutInflater;
	private BitmapManager bmpManager;
	private String resumeSelectedId;
	
	public ResumeListAdapt(Activity context,List<ResumeSimpleEntity> mlistData) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mlayoutInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文

		this.listItems = mlistData;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.person_photo_normal_small));
	}

	public String getResumeSelectedId() {
		return resumeSelectedId;
	}

	public void setResumeSelectedId(String resumeSelectedId) {
		this.resumeSelectedId = resumeSelectedId;
	}

	public static class ResumeListItemView { // 自定义控件集合
		public TextView name;
		public ImageView photoIv;
		public ImageView completeIv;
		public View bottomline;
		public ResumeSimpleEntity entity;
		public int postion;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (listItems.size());
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		 return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		 return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyLogger.d(TAG, "getView position = " + position);

		// 自定义视图
		ResumeListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = mlayoutInflater.inflate(R.layout.resumelist_item,
					null);

			listItemView = new ResumeListItemView();
			// 获取控件对象
			listItemView.name = (TextView) convertView
					.findViewById(R.id.resume_name_tv);
			listItemView.completeIv = (ImageView) convertView
					.findViewById(R.id.complete_imgview);
			listItemView.photoIv = (ImageView) convertView
					.findViewById(R.id.headphoto_imgview);
			listItemView.bottomline =  convertView
					.findViewById(R.id.bottomline);
			//listItemView.entity =  listItems.get(position);		
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ResumeListItemView) convertView.getTag();
					
		}
		listItemView.entity = listItems.get(position);	
		// 设置文字和图片
		ResumeSimpleEntity entity = listItemView.entity ;
		listItemView.postion = position;

		String name = entity.getName();
		if (name != null && name.length() > 0) {
			listItemView.name.setText(name+position);
		}

		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(context,
				75,
				Constants.ResumeModule.COMPLETEBMP_SMALL_RADIUS,
				Constants.ResumeModule.COMPLETEBMP_SMALL_BODER_WIDTH,
				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
		listItemView.completeIv.setImageBitmap(completebmp);

		CommonRoundImgCreator creator = new CommonRoundImgCreator(
				context,
				Constants.ResumeModule.HEADPHOTO_SMALL_RADIUS,
				Constants.ResumeModule.HEADPHOTO_SMALL_BODER_WIDTH,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

		Bitmap defaultbmp = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.person_photo_normal_small);
		bmpManager.loadBitmap(entity.getHeadphotoUrl(), listItemView.photoIv,
				defaultbmp, creator);
		if(entity.isSelected() || entity.getResumeId().equals(getResumeSelectedId())){
			listItemView.bottomline.setVisibility(View.VISIBLE);
		}else{
			listItemView.bottomline.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

}
