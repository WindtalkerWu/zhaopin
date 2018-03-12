package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.ResumeListAdapt;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntityList;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.app.ui.ResumeListActivity.ResumeListItemViewHolder;
import com.qianniu.zhaopin.app.ui.ResumeListActivity.ResumelistRefeshTask;
import com.qianniu.zhaopin.app.view.HorizontalListView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 推荐朋友
 * @author wuzy
 *
 */
public class JobRecommendActivity extends BaseActivity {
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageButton m_btnBack;					// 返回按钮
	private RelativeLayout m_btnQuickRecommend;		// 快速推荐
	private RelativeLayout m_btnRecommend;			// 推荐

	private String m_strTaskId;						// 悬赏任务ID
	private String m_strTaskTitle;					// 悬赏任务Title(此处为职位名)
	private String m_strResumeId;					// 当前被选中的简历id
	private String m_strCompanyName;				// 公司名
	
	private String m_strName;						// 被推荐人姓名
	
//	private View m_vloading;
//	private AnimationDrawable m_loadingAnimation;
	
	private ImageView completeImgview;
	private ImageView photoImgview;
	private BitmapManager m_bmpManager;
	private ProgressDialog mprogressDialog;
	
	private TextView changeTimeText;
	private TextView nameText;
	private TextView hopeJobText;
	
	private ResumeListAdapt mAdapter;
	private List<ResumeSimpleEntity> mlistData = new ArrayList<ResumeSimpleEntity>();
	private HorizontalListView mhorizonlv;
	private int mSelectedPositon = -1;
	private ResumeSimpleEntity mentity;

	private HorizontalScrollView mScrollView;
	private LinearLayout mviewcontainer;
	private LayoutInflater mlayoutInflater;
	private int mWidthPixels;

	private String from;
	
	// handler 处理的事件类型
	private static final int MSG_LOADDATA_ERROR = 102;
	private static final int MSG_LOADDATA_OK = 103;

	private static final int MSG_DATALOAD_START = 104;
	private static final int MSG_DATALOAD_OVER = 105;
	
	// 是否在推荐中
	private boolean m_bLockRecommend;
	
//	private AskPopupWindow m_askPW;					// 推荐提示框
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobrecommend);
		
		m_Context = this;
		m_appContext = (AppContext) this.getApplication();
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		initData();
		initControl();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
			switch (requestCode) {
			case ActivityRequestCode.RESULT_ACTIVITY_LOGIN:
				{
					if(m_bLockRecommend){
						Recommend();
					}else{
						loadResumeData(m_appContext, m_handler);						
					}
				}
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 友盟统计
		UmShare.UmResume(m_Context);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(m_Context);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_strTaskId = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID);
			m_strTaskTitle = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE);
			m_strCompanyName = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME);
		}
		
		m_bLockRecommend = false;
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
//		m_vloading = findViewById(R.id.rewardrecommend_loading);
//		m_loadingAnimation = (AnimationDrawable)m_vloading.getBackground();
		
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.rewardrecommend_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 快速推荐
		m_btnQuickRecommend = (RelativeLayout)findViewById(R.id.rewardrecommend_lp_btnquickrecommend);
		m_btnQuickRecommend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				// 友盟统计--推荐简历--快速推荐按钮
				UmShare.UmStatistics(m_Context, "JobRecommend_QuickRecommendButton");
				// 数据传输
				Bundle bundle = new Bundle();
				bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID, m_strTaskId);
				bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE, m_strTaskTitle);
				bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME, m_strCompanyName);

				// 进入快速推荐界面
		        Intent intent = new Intent();
		        intent.setClass(m_Context, QuickRecommendActivity.class);
		        intent.putExtras(bundle);
		        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
		});
		
		// 推荐
		m_btnRecommend = (RelativeLayout)findViewById(R.id.rewardrecommend_lp_btnrecommend);
		m_btnRecommend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				// 友盟统计--推荐简历--推荐按钮
				UmShare.UmStatistics(m_Context, "JobRecommend_RecommendButton");
				
				// showAskPopupWindow(v);
				showPromptAlertDialog();
			}
		});
		
		// 设置推荐按钮状态
		m_btnRecommend.setBackgroundResource(R.drawable.common_button_gray);
		m_btnRecommend.setEnabled(false);
		
		initHeadControl();
	}
	
	private void initHeadControl(){
		m_bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				getResources(), R.drawable.widget_dface_loading));

		completeImgview = (ImageView) findViewById(R.id.complete_imgview);
		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this, 0,
				Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
				Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
		completeImgview.setImageBitmap(completebmp);
		
		photoImgview = (ImageView) findViewById(R.id.headphoto_imgview);
		photoImgview = (ImageView) findViewById(R.id.headphoto_imgview);
		Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_normal_big);
		photobmp = ImageUtils.createRoundHeadPhoto(this, photobmp);
		photoImgview.setImageBitmap(photobmp);
		photoImgview.setOnClickListener(mlistener);
		
		changeTimeText = (TextView) findViewById(R.id.resume_time_tv);
		nameText = (TextView) findViewById(R.id.resume_name_tv);
		nameText.setOnClickListener(mlistener);
		hopeJobText = (TextView) findViewById(R.id.resume_job_tv);

		mprogressDialog = new ProgressDialog(this);
		// mprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mhorizonlv = (HorizontalListView) findViewById(R.id.resumelist_horizonlv);
		mAdapter = new ResumeListAdapt(JobRecommendActivity.this, mlistData);
		mhorizonlv.setAdapter(mAdapter);
		mhorizonlv.setOnItemClickListener(mitemlistener);

		mWidthPixels = getResources().getDisplayMetrics().widthPixels;
		mScrollView = (HorizontalScrollView) findViewById(R.id.resumelist_horizonScrollView);
		mviewcontainer = (LinearLayout) findViewById(R.id.resumelistcontainer);
		mlayoutInflater = LayoutInflater.from(m_Context);
		loadResumeData(m_appContext, m_handler);
	}

	/**
	 * 发送接受任务请求
	 */
	private void LinkRecommend(){
		try {
			AppContext ac = (AppContext)getApplication(); 
			
			Result res = ac.applyReward(m_strTaskId, String.valueOf(HeadhunterPublic.APPLYTASK_SENDTYPE_RECOMMEND),
					m_strResumeId);
            if( res.OK()){			
            	//	成功
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_SUCCESS));
            }else{
            	if(Result.CODE_TOKEN_INVALID == res.getErrorCode() ||
            			Result.CODE_TOKEN_OVERTIME == res.getErrorCode()){
					// 重新登录
                	m_handler.sendMessage(m_handler.obtainMessage(
    						HeadhunterPublic.REWARDRECOMMEND_MSG_QUICKRECOMMEND_LOGIN));
            	}else{
	            	//	失败
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_FAIL, res.getErrorMessage()));
            	}
           }
		} catch (AppException e) {
        	e.printStackTrace();			
	    	// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_ABNORMAL, e));
		}
	}
	
	private void Recommend(){
    	showProgressDialog();
    	
    	m_bLockRecommend = true;
    	
    	new Thread(){
        	public void run(){
        		LinkRecommend();
        	}
        }.start();
	}
	
	/**
	 * 
	 */
    private Handler m_handler = new Handler() {
    	public void handleMessage(Message msg){
    		switch(msg.what){
			case HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_SUCCESS:
				{
					dismissProgressDialog();
					
					UIHelper.ToastMessage(JobRecommendActivity.this, 
							R.string.msg_rewardrecommend_recommend_success);
					
					m_bLockRecommend = false;
				}
				break;
			case HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_ABNORMAL:
				{
					dismissProgressDialog();
					((AppException)msg.obj).makeToast(JobRecommendActivity.this);
				}
				break;
			case HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_FAIL:
				{
					dismissProgressDialog();
					
					UIHelper.ToastMessage(JobRecommendActivity.this, 
							R.string.msg_rewardrecommend_recommend_fail);
					
					m_bLockRecommend = false;
				}
				break;
			case HeadhunterPublic.REWARDRECOMMEND_MSG_QUICKRECOMMEND_LOGIN:
				{
					dismissProgressDialog();
					
					UIHelper.showLoginActivityForResult(JobRecommendActivity.this);
				}
				break;

			case MSG_DATALOAD_START:
				mprogressDialog.setMessage(R.string.dialog_dataloadmsg);
				mprogressDialog.show();
				break;
			case MSG_DATALOAD_OVER:

				mprogressDialog.dismiss();
				break;
			case MSG_LOADDATA_ERROR:
				UIHelper.ToastMessage(m_Context, R.string.dialog_data_get_err);
				break;
			case MSG_LOADDATA_OK:
				ResumeSimpleEntityList entitylist = (ResumeSimpleEntityList) msg.obj;
				Result res = entitylist.getValidate();
				if (!res.OK()) {
					if (res.getErrorCode() == Result.CODE_RESUME_LIST_EMPTY) {
						UIHelper.ToastMessage(m_Context,
								R.string.msg_rewardrecommend_resume_list_empty);
						break;
					}
					boolean bflag = res.handleErrcode(JobRecommendActivity.this);
					if (!bflag) {
						UIHelper.ToastMessage(m_Context,
								R.string.dialog_data_get_err);
					}
					
				} else {
					if (entitylist.getEntitylist() != null
							&& entitylist.getEntitylist().size() > 0) {
						mlistData.clear();
						mlistData.addAll(0, entitylist.getEntitylist());
						ResumelistRefeshTask task = new ResumelistRefeshTask();
						task.execute(mlistData);
					}
					
					// 设置推荐按钮状态
					m_btnRecommend.setBackgroundResource(R.drawable.common_button_green);
					m_btnRecommend.setEnabled(true);
				}
				break;
			default:
				break;
			}	    		
    	}
    };
    
    /*********************************************************/
    private void loadResumeData(final AppContext appcontext,
			final Handler handler) {
    	// 判断网络是否连接
		if(!UIHelper.isNetworkConnected(m_appContext)){
			return;
		}
		
		new Thread() {
			public void run() {
				handler.sendEmptyMessage(MSG_DATALOAD_START);
				boolean bnetConnect = appcontext.isNetworkConnected();
				boolean bnetDataException = false;
				ResumeSimpleEntityList entitylist = null;
				Message msg = new Message();
				if (bnetConnect) {
					try {
						entitylist = appcontext
								.getResumeSimpleEntityListFromNet(appcontext,
										true);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						bnetDataException = true;
						msg.obj = e;
					}
				}
				if (entitylist == null || bnetDataException) {
					try {
						entitylist = appcontext
								.getSimpleResumeListFromDb(m_appContext);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						msg.obj = e;
					}
				}

				if (entitylist != null) {
					msg.what = MSG_LOADDATA_OK;
					msg.obj = entitylist;
				} else {
					msg.what = MSG_LOADDATA_ERROR;

				}

				handler.sendEmptyMessage(MSG_DATALOAD_OVER);
				handler.sendMessage(msg);
			}
		}.start();
	}
    
	public class ResumelistRefeshTask extends AsyncTask<List<ResumeSimpleEntity> ,  Integer,  List<View>> {

		private int defaultlocation = -1;
		

		public ResumelistRefeshTask() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onPostExecute(List<View> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mviewcontainer.removeAllViews();
			for(int i = 0; i<result.size();i++ ){
				ResumeListItemViewHolder viewholder = (ResumeListItemViewHolder) result.get(i).getTag();
				ResumeSimpleEntity entity = viewholder.entity;
				String name = entity.getName();
				if (name != null && name.length() > 0) {
					viewholder.name.setText(name);
				}
				
				int percent = entity.getPercent();

				Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(JobRecommendActivity.this,
						percent,
						Constants.ResumeModule.COMPLETEBMP_SMALL_RADIUS,
						Constants.ResumeModule.COMPLETEBMP_SMALL_BODER_WIDTH,
						Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
						Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
				viewholder.completeIv.setImageBitmap(completebmp);

				CommonRoundImgCreator creator = new CommonRoundImgCreator(
						JobRecommendActivity.this,
						Constants.ResumeModule.HEADPHOTO_SMALL_RADIUS,
						Constants.ResumeModule.HEADPHOTO_SMALL_BODER_WIDTH,
						Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

				Bitmap defaultbmp = BitmapFactory.decodeResource(m_Context.getResources(),
						R.drawable.person_photo_normal_small);
				m_bmpManager.loadBitmap(entity.getHeadphotoUrl(), viewholder.photoIv,
						defaultbmp, creator);
				viewholder.bottomline.setVisibility(View.INVISIBLE);
				mviewcontainer.addView(result.get(i));
			}
			setSelectedView(result.get(defaultlocation));
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected List<View> doInBackground(List<ResumeSimpleEntity>... params) {
			// TODO Auto-generated method stub
			ArrayList<View> listview = new ArrayList<View>();
			List<ResumeSimpleEntity> listentity = params[0];
			if (listentity.size() > 0) {
				defaultlocation = 0;
			}

			for(int i = 0; i<listentity.size();i++){
				View convertView = mlayoutInflater.inflate(R.layout.resumelist_item,
						null);
				ResumeSimpleEntity entity = listentity.get(i);
				if (entity.getDefaultAuthenticat() == ResumeSimpleEntity.RESUME_DEFAULT_SELECTED) {
					defaultlocation = i;
				}
				ResumeListItemViewHolder viewholder = new ResumeListItemViewHolder();
				// 获取控件对象
				viewholder.name = (TextView) convertView
						.findViewById(R.id.resume_name_tv);
				viewholder.completeIv = (ImageView) convertView
						.findViewById(R.id.complete_imgview);
				viewholder.photoIv = (ImageView) convertView
						.findViewById(R.id.headphoto_imgview);
				viewholder.bottomline =  convertView
						.findViewById(R.id.bottomline);
				viewholder.postion = i;
				viewholder.entity = entity;
				convertView.setTag(viewholder);
				convertView.setOnClickListener(listviewlistener);								

				listview.add(convertView);
				
			}
			return listview;
		}
	}
	
	private OnClickListener listviewlistener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setSelectedView(v);
		}
	};
	
	private View mselectedView;
	private void setSelectedView(View view) {
		ResumeListItemViewHolder newholder = (ResumeListItemViewHolder) view.getTag();

		if(mselectedView != null){
			ResumeListItemViewHolder oldholder = (ResumeListItemViewHolder) mselectedView.getTag();
			oldholder.bottomline.setVisibility(View.INVISIBLE);
		}
		newholder.bottomline.setVisibility(View.VISIBLE);
		mselectedView = view;
		int left = view.getLeft();
		int width = view.getWidth();
    	int x = (int) (view.getLeft()+view.getWidth()/2.0-mWidthPixels/2.0);
		mScrollView.smoothScrollTo(x, 0);
		displayResume(newholder.entity);
	}
	
	private void displayResume(ResumeSimpleEntity entity) {
		mentity = entity;
		m_strResumeId = entity.getResumeId();
		int completedegree = 0;
		String time = entity.getModifyTime();
		if (time != null) {
			time = StringUtils.formatDateString(time);
			if (time != null && time.length() > 0){
				changeTimeText.setText(time);
			}else{
				changeTimeText.setText("");
			}
		}

		m_strName = entity.getName();
		if (m_strName != null && m_strName.length() > 0) {
			nameText.setText(m_strName);
		}else{
			nameText.setText("");
		}

		String job = entity.getPersonalTitle();
		if (job != null && job.length() > 0) {
			hopeJobText.setText(job);
		}else{
			hopeJobText.setText("");
		}

		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this,
				completedegree,
				Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
				Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
		completeImgview.setImageBitmap(completebmp);

		CommonRoundImgCreator creator = new CommonRoundImgCreator(
				JobRecommendActivity.this,
				Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

		Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_normal_big);
		m_bmpManager.loadBitmap(entity.getHeadphotoUrl(), photoImgview,
				defaultbmp, creator);
	}

	private void setSelectedItem(int position, boolean bjump) {
		ResumeSimpleEntity entity = mlistData.get(position);
		entity.setSelected(true);
		if (mSelectedPositon >= 0) {
			ResumeSimpleEntity preentity = mlistData.get(mSelectedPositon);
			entity.setSelected(false);
		}
		mSelectedPositon = position;
		if (bjump)
			mhorizonlv.setSelection(position);

		mAdapter.notifyDataSetInvalidated();
		displayResume(entity);
	}
	
	OnItemClickListener mitemlistener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			setSelectedItem(position, false);
		}
	};

	OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.headphoto_imgview: {
				if (mentity != null) {
					String url = mentity.getResumeUrl();
					int resumeid = StringUtils.toInt(mentity.getResumeId(), 0);
					if (resumeid == 0 || url == null || url.length() == 0) {
						UIHelper.ToastMessage(m_Context, "当前简历无法预览");
					} else {
						UIHelper.showResumePreview(m_Context, mentity);
					}
				}

			}
				break;
			case R.id.resume_name_tv: {
				UIHelper.showResumePreview(m_Context, mentity);
			}
				break;
			}
		}
	};
	
	/**
	 * 显示快速推荐询问界面
	 * @param v
	 */
//	private void showAskPopupWindow(View v) {
//		if (m_askPW == null) {
//			String strTitle = m_Context.getString(R.string.str_quickrecommend_title);
//			String strContent = String.format(m_Context.getString(R.string.str_quickrecommend_ask_content), 
//					m_strName, m_strTaskTitle, m_strCompanyName);
//			
//			m_askPW = new AskPopupWindow(m_Context, strTitle, strContent);
//			m_askPW.setListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					switch (v.getId()) {
//					case R.id.popupwindow_ask_img_ok:
//						{
//							hideAskPopupWindow();
//							// 确认要快速推荐
//							Recommend();
//						}
//						break;
//					case R.id.popupwindow_ask_img_cancel:
//						{
//							hideAskPopupWindow();
//						}
//						break;
//					default:
//						break;
//					}
//				}
//			});
//		}
//		
//		m_askPW.showAtLocation(v, Gravity.TOP, 0, 0);
//	}
	
	/**
	 * 关闭提示框
	 */
//	private void hideAskPopupWindow() {
//		if (m_askPW != null) {
//			m_askPW.dismiss();
//		}
//	}
	
	/**
	 * 应聘提示框
	 */
	private void showPromptAlertDialog(){
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.str_quickrecommend_title));
		
		LayoutInflater inflater = (LayoutInflater) m_Context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View textView = (View) inflater.inflate(R.layout.alertdialog_prompt_twotext, null);
		TextView tvMsg = (TextView)textView.findViewById(R.id.alertdialog_prompt_twotext_tv_msg);
		
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	tvMsg.setTextColor(m_Context.getResources().getColor(R.color.white));
        }
		
		// 消息内容
		String strMsg = String.format(m_Context.getString(R.string.str_quickrecommend_ask_content), 
				m_strName, m_strCompanyName, m_strTaskTitle);
		tvMsg.setText(strMsg);
		
		dl.setView(textView);
		dl.setPositiveButton(getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						// 确认要快速推荐
						Recommend();
					}
			
			});
		dl.setNegativeButton(getString(R.string.dialog_cancel),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
			
			});
		dl.show();
	}
}
