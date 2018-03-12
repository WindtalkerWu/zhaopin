package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.adapter.ForumInfoListAdapt.ForumInfoListItemView;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.ui.InfoListCommonItem;
import com.qianniu.zhaopin.app.ui.InfoListMultiImgTxtItem;
import com.qianniu.zhaopin.app.ui.InfoListSimpleImgTxtItem;
import com.qianniu.zhaopin.app.ui.InfoListTxtItem;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExtraHomeListAdapt extends BaseAdapter {

	private Context context;// 运行上下文
	private List<ItemInfoEntity> listItems;// 数据集合
	private LayoutInflater mlayoutInflater;
	private int itemViewResource;
	private BitmapManager bmpManager;
	private Bitmap defbmp;
	private final static int maxOneLine = 2; 

	public ExtraHomeListAdapt(Context context, List<ItemInfoEntity> mlistData) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mlayoutInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文

		this.listItems = mlistData;
		this.bmpManager = new BitmapManager(null);
	}

	public void setDefaultDisplayImg(Bitmap defbmp) {
		bmpManager.setDefaultBmp(defbmp);
		this.defbmp = defbmp;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (listItems.size()/maxOneLine +1);
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
		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = mlayoutInflater.inflate(R.layout.extra_list_item,
					null);

		} else {

		}
		
		ViewGroup itemcontainer = (ViewGroup) convertView
				.findViewById(R.id.extra_list_item_container);

		int maxviews = maxOneLine;
		int start = position*maxviews;
		int end = position*maxviews +maxviews-1;
		int datasize  = listItems.size();
		itemcontainer.removeAllViews();

		
		float density = context.getResources().getDisplayMetrics().density;
		int wpx = context.getResources().getDisplayMetrics().widthPixels;
			LinearLayout.LayoutParams lp = new LinearLayout
		.LayoutParams((int) ((wpx-10*density)/2), LayoutParams.WRAP_CONTENT);
		//RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) ((wpx-10*density)/2), LayoutParams.WRAP_CONTENT);
		for(int i = 0;i < maxviews ; i++){
			if(datasize >(start +i)){
				ItemInfoEntity item = listItems.get(start +i);
				InfoEntity  entity = item.getInfoEntitylist().get(0);
				if(entity != null && entity.getValidate().OK()){
					ViewGroup itemView = (ViewGroup) mlayoutInflater.inflate(
							R.layout.extra_home_list_itemview, null);
					
					TextView titleview = (TextView) itemView.findViewById(R.id.extra_home_list_item_title);	
					ImageView imgview = (ImageView) itemView.findViewById(R.id.extra_home_list_item_imgview);
					TextView subtitleview = (TextView) itemView.findViewById(R.id.extra_home_list_item_subtitle);
					String faceURL = entity.getInfoImg();
					if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
						// face.setImageResource(R.drawable.widget_dface);
						if (defbmp != null)
							imgview.setImageBitmap(defbmp);
					} else {
						bmpManager.loadBitmap(faceURL, imgview);
					}
					titleview.setText(entity.getInfoTitle());
					subtitleview.setText(entity.getInfoDigest());
					itemView.setTag(entity);
					itemView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							InfoEntity entity = (InfoEntity) v.getTag();
							InfoEntity.actionJump(context, entity);
							//UIHelper.showInfoDetial(context, entity.getInfoId(), entity.getInfoTitle(), entity.getInfoUrl());
						}
					});
					if(i == 0)
						lp.setMargins((int) (2*density), (int) (2*density), 0, 0);//lp.setMargins(6, 0, 1, 0);
					else
						lp.setMargins((int) (2*density), (int) (2*density), (int) (2*density),0 );//lp.setMargins(1, 0, 6, 0);
					
					itemcontainer.addView(itemView, lp);
				}

			}else{
				LinearLayout v_ll= new LinearLayout(context);
				lp.setMargins((int) (2*density), (int) (2*density), (int) (0*density),0 );
				v_ll.setOrientation(LinearLayout.VERTICAL);
				itemcontainer.addView(v_ll, lp);
				break;
			}
		}
		return convertView;
	}
}
