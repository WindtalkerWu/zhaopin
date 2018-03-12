package com.qianniu.zhaopin.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.DiskItemEntity;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.thp.UmShare;

public class Fragment_extraMain extends BaseFragment {
	private final static int ACTION_MORE = 0;// 更多
	private final static int ACTION_FAMOUS_MAN = 1;// 名人
	private final static int ACTION_FAMOUS_COMPANY = 2;// 名企
	private final static int ACTION_CAMPAIGN = 3;// 活动
	private final static int ACTION_PROFESSIONAL_INFO = 4;// 行业资讯
	private final static int ACTION_TAX_CALCULATOR = 5;// 个税计算
	private final static int ACTION_APP_RECOMMENT = 6;// 牵牛推荐
	private final static int ACTION_APP_SHAIGONGZI = 7;// 晒工资
	private final static int ACTION_APP_AIDIAOYAN = 8;// 爱调研
	private final static int ACTION_APP_SHANGCHENG = 9;// 商城
	private final static int ACTION_APP_QIANDAO = 10;// 签到
	private final static int ACTION_APP_MINGPIAN = 11;// 名片

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
//			new DiskItemEntity(R.drawable.ic_more,
//					R.string.disk_item_shaigognzi, ACTION_APP_SHAIGONGZI),
			// new DiskItemEntity(R.drawable.ic_more,
			// R.string.disk_item_aidiaoyan, ACTION_APP_AIDIAOYAN),
			// new DiskItemEntity(R.drawable.ic_more,
			// R.string.disk_item_shangcheng, ACTION_APP_SHANGCHENG),
			// new DiskItemEntity(R.drawable.ic_more,
			// R.string.disk_item_qiandao, ACTION_APP_QIANDAO),
			// new DiskItemEntity(R.drawable.ic_more,
			// R.string.disk_item_mingpian, ACTION_APP_MINGPIAN),
			new DiskItemEntity(R.drawable.ic_more, R.string.disk_item_more,
					ACTION_MORE) };

	private Context m_Context;
	private AppContext appContext;// 全局Context
	private View mContainer;
	private AdZoneView adzoneView;
	private ListView mListView;
	private DiskAdapter mListAdapt;

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
		mContainer = inflater.inflate(R.layout.fragment_extra_main, container,
				false);

		mListView = (ListView) mContainer
				.findViewById(R.id.extra_main_listview);

		ImageView m_btnSet = (ImageView) mContainer
				.findViewById(R.id.fragment_extra_set);
		m_btnSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 设置界面
				UIHelper.showSetActivity(m_Context);
			}

		});
		ViewGroup headview = (ViewGroup) inflater.inflate(
				R.layout.banner_layout, null);
		adzoneView = new AdZoneView(getActivity(), AdZoneView.ADZONE_ID_MIX);

		headview.addView(adzoneView);
		mListAdapt = new DiskAdapter();
		mListView.addHeaderView(headview);
		mListView.setAdapter(mListAdapt);
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
		private int columns = 3;

		public DiskAdapter() {
			super();
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(m_Context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			int i = (items.length / columns)
					+ ((items.length % columns > 0) ? 1 : 0);
			return (items.length / columns)
					+ ((items.length % columns > 0) ? 1 : 0);
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

				convertView = inflater.inflate(R.layout.disk_listitem, null);

			}
			for (int i = 0; i < columns; i++) {
				View view = null;
				DiskItemEntity entity = null;
				switch (i) {
				case 0: {
					view = convertView.findViewById(R.id.item_01);
				}
					break;
				case 1: {
					view = convertView.findViewById(R.id.item_02);
				}
					break;
				case 2: {
					view = convertView.findViewById(R.id.item_03);
				}
					break;
				}
				if (position * columns + i < items.length) {
					entity = items[position * columns + i];

				} else {
					entity = null;
				}

				if (entity != null) {
					ImageView logo = (ImageView) view
							.findViewById(R.id.item_logo_iv);
					TextView title = (TextView) view
							.findViewById(R.id.item_title_tv);
					logo.setImageResource(entity.getImgresId());
					title.setText(entity.getTitleresId());
					view.setVisibility(View.VISIBLE);
					view.setTag(entity);
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							DiskItemEntity entity = (DiskItemEntity) v.getTag();
							if (entity == null)
								return;
							switch (entity.getActionId()) {
							case ACTION_FAMOUS_MAN: {
								UIHelper.startInsidersOrCompanyActivity(
										getActivity(),
										InsidersAndCompany.TYPEINSIDERS);
							}
								break;
							case ACTION_FAMOUS_COMPANY: {
								UIHelper.startInsidersOrCompanyActivity(
										getActivity(),
										InsidersAndCompany.TYPECOMPANY);
							}
								break;
							case ACTION_CAMPAIGN: {
								UIHelper.startCampaignListActivity(getActivity());
							}
								break;
							case ACTION_PROFESSIONAL_INFO: {
								UIHelper.startProfessionalInfoActivity(getActivity());
							}
								break;
							case ACTION_TAX_CALCULATOR: {
								UIHelper.startTaxActivity(getActivity());
							}
								break;
							case ACTION_APP_RECOMMENT: {
								UIHelper.startAppRecommentActivity(getActivity());
							}
								break;
							case ACTION_MORE: {
								UIHelper.startMoreModuleActivity(getActivity());
							}
								break;
							case ACTION_APP_SHAIGONGZI: {
								UIHelper.startExposureWageActivity(getActivity());
							}
								break;
							case ACTION_APP_AIDIAOYAN: {
								UIHelper.startExposureWageActivity(getActivity());
							}
								break;
							case ACTION_APP_SHANGCHENG: {
								UIHelper.startExposureWageActivity(getActivity());
							}
								break;
							case ACTION_APP_QIANDAO: {
								UIHelper.startExposureWageActivity(getActivity());
							}
								break;
								
							case ACTION_APP_MINGPIAN: {
								UIHelper.startExposureWageActivity(getActivity());
							}
								break;
							}
						}
					});
				} else {
					view.setVisibility(View.INVISIBLE);
				}
			}

			return convertView;
		}

	}
}
