package com.qianniu.zhaopin.app.ui;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.DiskItemEntity;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Fragment_extrahome extends BaseFragment {
	private final static int ACTION_MORE = 0;// 更多
	private final static int ACTION_FAMOUS_MAN = 1;// 名人
	private final static int ACTION_FAMOUS_COMPANY = 2;// 名企
	private final static int ACTION_CAMPAIGN = 3;// 活动
	private final static int ACTION_PROFESSIONAL_INFO = 4;// 行业资讯
	private final static int ACTION_TAX_CALCULATOR = 5;// 个税计算
	private final static int ACTION_APP_RECOMMENT = 6;// 牵牛推荐

	private Context m_Context;
	private AppContext appContext;// 全局Context
	private View mContainer;
	private GridView mdisk_gv;
	private DiskAdapter madapter;
	private ViewGroup mAdzoneContainer;
	private AdZoneView adzoneView;

	private DiskItemEntity items[] = {
			new DiskItemEntity(R.drawable.ic_famousman,
					R.string.disk_item_famous_man, ACTION_FAMOUS_MAN),
			new DiskItemEntity(R.drawable.ic_famouscompany,
					R.string.disk_item_famous_campany, ACTION_FAMOUS_COMPANY),
			new DiskItemEntity(R.drawable.ic_campaign,
					R.string.disk_item_campaign, ACTION_CAMPAIGN),
			new DiskItemEntity(R.drawable.ic_professionalinfo,
					R.string.disk_item_professional_info,
					ACTION_PROFESSIONAL_INFO),
			new DiskItemEntity(R.drawable.ic_taxcalc,
					R.string.disk_item_tax_calculator, ACTION_TAX_CALCULATOR),
			new DiskItemEntity(R.drawable.ic_recommand,
					R.string.disk_item_app_recommand, ACTION_APP_RECOMMENT),
			new DiskItemEntity(R.drawable.ic_more, R.string.disk_item_more,
					ACTION_MORE) };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_Context = this.getActivity();
		appContext = (AppContext) this.getActivity().getApplication();
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mContainer != null) {
			ViewGroup p = (ViewGroup) mContainer.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}
			if (adzoneView != null) {
				adzoneView.refreshView();
			}
			return mContainer;
		}
		mContainer = inflater.inflate(R.layout.activity_extra_main, container,
				false);
		mAdzoneContainer = (ViewGroup) mContainer
				.findViewById(R.id.extra_mian_adzone);
		/*
		 * adzoneView = new AdZoneView(m_Context, AdZoneView.ADZONE_ID_INFO);
		 * mAdzoneContainer.addView(adzoneView);
		 */
		mdisk_gv = (GridView) mContainer.findViewById(R.id.extra_main_girdview);
		ImageView m_btnSet = (ImageView) mContainer.findViewById(R.id.fragment_extra_set);
		m_btnSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 设置界面
				UIHelper.showSetActivity(m_Context);
			}

		});

		madapter = new DiskAdapter();
		mdisk_gv.setAdapter(madapter);
		return mContainer;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// 友盟统计
		UmShare.UmPause(m_Context);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 友盟统计
		UmShare.UmResume(m_Context);
	}

	public class DiskAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public DiskAdapter() {
			super();
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(m_Context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				// 获取list_item布局文件的视图
				convertView = inflater.inflate(R.layout.disk_gridview_item,
						null);
				DisplayMetrics dm = AppContext.getPhoneDisplayMetrics(appContext);
				convertView.setLayoutParams(new GridView.LayoutParams(
						LayoutParams.MATCH_PARENT, (int) (100 * dm.density)));
				DiskViewHolder vh = new DiskViewHolder(convertView);
				convertView.setTag(vh);

			} else {

			}
			DiskViewHolder vh = (DiskViewHolder) convertView.getTag();
			DiskItemEntity entity = items[position];
			vh.entity = entity;

			vh.logo.setImageResource(entity.getImgresId());

			vh.title.setText(entity.getTitleresId());

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					DiskViewHolder vh = (DiskViewHolder) v.getTag();
					switch (vh.entity.getActionId()) {
					case ACTION_FAMOUS_MAN: {
						// 友盟统计--号外--圈内人按钮
						UmShare.UmStatistics(m_Context, "Extra_CoterieButton");
						
						UIHelper.startInsidersOrCompanyActivity(getActivity(),
								InsidersAndCompany.TYPEINSIDERS);
					}
						break;
					case ACTION_FAMOUS_COMPANY: {
						// 友盟统计--号外--名企专区按钮
						UmShare.UmStatistics(m_Context, "Extra_FamousEnterprisesButton");
						
						UIHelper.startInsidersOrCompanyActivity(getActivity(),
								InsidersAndCompany.TYPECOMPANY);
					}
						break;
					case ACTION_CAMPAIGN: {
						// 友盟统计--号外--活动按钮
						UmShare.UmStatistics(m_Context, "Extra_CampaignButton");
						
						UIHelper.startCampaignListActivity(getActivity());
					}
						break;
					case ACTION_PROFESSIONAL_INFO: {
						// 友盟统计--号外--行业资讯按钮
						UmShare.UmStatistics(m_Context, "Extra_ProfessionalInfoButton");
						
						UIHelper.startProfessionalInfoActivity(getActivity());
					}
						break;
					case ACTION_TAX_CALCULATOR: {
						// 友盟统计--号外--个税计算按钮
						UmShare.UmStatistics(m_Context, "Extra_TaxButton");
						
						UIHelper.startTaxActivity(getActivity());
					}
						break;
					case ACTION_APP_RECOMMENT: {
						// 友盟统计--号外--牵牛推荐按钮
						UmShare.UmStatistics(m_Context, "Extra_AppRecommentButton");
						
						UIHelper.startAppRecommentActivity(getActivity());
					}
						break;
					case ACTION_MORE: {
						// 友盟统计--号外--更多按钮
						UmShare.UmStatistics(m_Context, "Extra_MoreButton");
						
						UIHelper.startMoreModuleActivity(getActivity());
					}
						break;
					}
				}
			});

			return convertView;
		}

		private class DiskViewHolder {
			public ImageView logo;
			public TextView title;
			public DiskItemEntity entity;

			public DiskViewHolder(View v) {
				logo = (ImageView) v.findViewById(R.id.item_logo_iv);
				title = (TextView) v.findViewById(R.id.item_title_tv);
			}

		}
	}
}
