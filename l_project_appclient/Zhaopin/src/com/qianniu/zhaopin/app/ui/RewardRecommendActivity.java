package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.reqQuickRecommendData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我要赚钱界面
 * @author wuzy
 *
 */
public class RewardRecommendActivity extends BaseActivity{
	private Context m_Context;
	
//	private AskPopupWindow m_askPW;			// 推荐提示框
	
	private ImageButton m_btnBack;			// 返回按钮
	private RelativeLayout m_btnSubmit;		// 提交按钮
	private RelativeLayout m_btnReason;		// 推荐理由选择
	
	private RelativeLayout m_rlSubmit;		// 提交按钮
	
	private EditText m_etName;				// 名字
	private EditText m_etTel;				// 联系电话
	private EditText m_etDetailSubmit;		// 推荐理由(详细)
	private TextView m_tvSubmit;			// 推荐理由
	
	private String m_strTaskId;				// 悬赏任务ID
	private String m_strChooseId;			// 选中的理由ID

//	private View m_vloading;
//	private AnimationDrawable m_loadingAnimation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rewardrecommend);
		
		m_Context = this;
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		initData();
		initControl();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if( HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode){
			switch(resultCode){
			case HeadhunterPublic.RESULT_RECOMMENDREASON_OK:
				{
					// 获取选中的推荐理由数据
					OneLevelChooseData rcd  =  (OneLevelChooseData)data.getSerializableExtra(
							HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_BACKDATA);
					
					m_tvSubmit.setText(rcd.getName());
					m_strChooseId = rcd.getID();
				}
				break;
			case RESULT_OK:		// 登录成功
				{
					jobRecommend();
				}
				break;
			default:
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
		m_strChooseId = "";

		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_strTaskId = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID);
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
//		m_vloading = findViewById(R.id.jobrecommend_loading);
//		m_loadingAnimation = (AnimationDrawable)m_vloading.getBackground();
		
		// 名字
		m_etName = (EditText)findViewById(R.id.jobrecommend_et_name);
		// 联系电话
		m_etTel = (EditText)findViewById(R.id.jobrecommend_et_tel);
		// 推荐理由
		m_tvSubmit = (TextView)findViewById(R.id.jobrecommend_tv_reason);
		// 推荐理由(详细)
		m_etDetailSubmit = (EditText)findViewById(R.id.jobrecommend_et_detailreason);
		
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.jobrecommend_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 提交
		m_btnSubmit = (RelativeLayout)findViewById(R.id.jobrecommend_lp_submit);
		m_btnSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--任务悬赏--我来推荐--提交
				UmShare.UmStatistics(m_Context, "RewardInfo_RewardRecommend_Submit");
				
				if(checkControlValue()){
					// 询问是否要接受者个任务
					//showAskPopupWindow(v);
					showPromptAlertDialog();
				}
			}
		});
		
		// 推荐理由选择
		m_btnReason = (RelativeLayout)findViewById(R.id.jobrecommend_rl_reason);
		m_btnReason.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 数据传输
				Bundle bundle = new Bundle();
				
				bundle.putString(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_TITLE, 
						getString(R.string.str_recommendreasonchoose_title));
				bundle.putSerializable(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_DATA,
						getOneLevelData());
				bundle.putString(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_SELECTED, m_strChooseId);
				
		        Intent intent = new Intent();
		        intent.setClass(m_Context, OneLevelChooseActivity.class);
		        intent.putExtras(bundle);
		        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
		});
	}
	
	/**
	 * @return
	 */
	private boolean checkControlValue(){

		String strTemp = null;
		
		// 检查名字是否为空
		strTemp = m_etName.getText().toString();
		if(strTemp.isEmpty()){
			UIHelper.ToastMessage(this, 
					getString(R.string.msg_jobrecommend_name_empty));	
			return false;
		}
		
		// 检查联系方法是否为空
		strTemp = m_etTel.getText().toString();
		if(strTemp.isEmpty()){
			UIHelper.ToastMessage(this, 
					getString(R.string.msg_jobrecommend_tel_empty));
			return false;
		}
		
		// 检查推荐理由是否为空
		strTemp = m_tvSubmit.getText().toString();
		if(strTemp.isEmpty()){
			UIHelper.ToastMessage(this, 
					getString(R.string.msg_jobrecommend_reason_empty));
			return false;
		}
		
		// 检查推荐详细理由是否为空
		strTemp = m_etDetailSubmit.getText().toString();
		if(strTemp.isEmpty()){
			UIHelper.ToastMessage(this, 
					getString(R.string.msg_jobrecommend_detailreason_empty));
			return false;
		}
		
		return true;
	}
	
	/**
	 * 向服务器提交推荐请求
	 */
	private void linkJobRecommend(){
		try {
			AppContext ac = (AppContext)getApplication(); 
			
			reqQuickRecommendData reqQRD = new reqQuickRecommendData();
			reqQRD.setApply_Type(String.valueOf(HeadhunterPublic.APPLYTASK_SENDTYPE_RECOMMEND));
			reqQRD.setTask_Id(m_strTaskId);
			reqQRD.setPhone(m_etTel.getText().toString());
			reqQRD.setName(m_etName.getText().toString());
			String strTemp = m_tvSubmit.getText().toString() + 
					m_etDetailSubmit.getText().toString();
			reqQRD.setMemo(strTemp);
			
			Result res = ac.quickRecommend(reqQRD);
            if( res.OK()){
            	// 成功
            	m_handler.sendMessage(m_handler.obtainMessage(
            			HeadhunterPublic.JOBRECOMMEND_MSG_QUICKRECOMMEND_SUCCESS));
            }else{
            	if(Result.CODE_TOKEN_INVALID == res.getErrorCode() ||
            			Result.CODE_TOKEN_OVERTIME == res.getErrorCode()){
					// 重新登录
                	m_handler.sendMessage(m_handler.obtainMessage(
                			HeadhunterPublic.JOBRECOMMEND_MSG_QUICKRECOMMEND_LOGIN));
            	}else{
                	// 失败
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.JOBRECOMMEND_MSG_QUICKRECOMMEND_FAIL, res.getErrorMessage()));
            	}
            }
		} catch (AppException e) {
        	e.printStackTrace();
        	// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.JOBRECOMMEND_MSG_QUICKRECOMMEND_ABNORMAL, e));
		}		
	}
	
	/**
	 * 
	 */
	private void jobRecommend(){
    	showProgressDialog();
    	
    	new Thread(){
        	public void run(){
        		linkJobRecommend();
        	}
        }.start();
	}
	
	/**
	 * 
	 */
    private Handler m_handler = new Handler() {
    	public void handleMessage(Message msg){
    		switch(msg.what){
			case HeadhunterPublic.JOBRECOMMEND_MSG_QUICKRECOMMEND_SUCCESS:
				{
					dismissProgressDialog();
					
					UIHelper.ToastMessage(RewardRecommendActivity.this, 
							R.string.msg_jobrecommend_quickrecommend_success);
					
					finish();
				}
				break;
			case HeadhunterPublic.JOBRECOMMEND_MSG_QUICKRECOMMEND_ABNORMAL:
				{
					dismissProgressDialog();
					((AppException)msg.obj).makeToast(RewardRecommendActivity.this);
				}
				break;
			case HeadhunterPublic.JOBRECOMMEND_MSG_QUICKRECOMMEND_FAIL:
				{
					dismissProgressDialog();
					
					UIHelper.ToastMessage(RewardRecommendActivity.this, 
							R.string.msg_jobrecommend_quickrecommend_fail);
				}
				break;
			case HeadhunterPublic.JOBRECOMMEND_MSG_QUICKRECOMMEND_LOGIN:
				{
					dismissProgressDialog();
					
					login();
				}
				break;
			default:
				break;
			}	    		
    	}
    };
    
	/**
	 * @return
	 */
	private ArrayList<OneLevelChooseData> getOneLevelData(){
		ArrayList<OneLevelChooseData> alsOLCD = new ArrayList<OneLevelChooseData>();
		
//		String strChoose = m_tvSubmit.getText().toString();
		
		try {
			List<GlobalDataTable> lvGDT = getRecomendReasonData();
			
			for(GlobalDataTable gdt : lvGDT){
				OneLevelChooseData olcd = new OneLevelChooseData();
				
				olcd.setID(gdt.getID());
				olcd.setName(gdt.getName());
				olcd.setNamePinYin(gdt.getNamePinYin());
				olcd.setParentID(gdt.getParentID());
				
				alsOLCD.add(olcd);
			}
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return alsOLCD;
	}
	
    /**
     * 登录
     */
    private void login(){
        Intent intent = new Intent();
        intent.setClass(m_Context, UserLoginActivity.class);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
    }
    
	/**
	 * 显示应聘询问界面
	 * @param v
	 */
//	private void showAskPopupWindow(View v) {
//		if (m_askPW == null) {
//			String strTitle = m_Context.getString(R.string.str_jobrecommend_title);
//			String strContent = m_Context.getString(R.string.str_jobrecommend_ask_content);
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
//							// 向服务器提交推荐请求
//							jobRecommend();
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
	 * 从全局数据中取出城市数据
	 * @param nType
	 * @return
	 * @throws AppException
	 */
	private List<GlobalDataTable> getRecomendReasonData() throws AppException {
		 List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();
		 
		 AppContext ac = (AppContext)getApplication(); 
		 lsGDT = ac.getTpyeDataNoSort(DBUtils.GLOBALDATA_TYPE_RECOMMENDREASON);
		 
		 return lsGDT;
	}
	
	/**
	 * 提示框
	 */
	private void showPromptAlertDialog(){
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.str_jobrecommend_title));
		LayoutInflater inflater = (LayoutInflater) m_Context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View textView = (View) inflater.inflate(R.layout.alertdialog_prompt_twotext, null);
		TextView tvMsg = (TextView)textView.findViewById(R.id.alertdialog_prompt_twotext_tv_msg);
		
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	tvMsg.setTextColor(m_Context.getResources().getColor(R.color.white));
        }
		
		// 消息内容
		String strMsg = m_Context.getString(R.string.str_jobrecommend_ask_content);
		tvMsg.setText(strMsg);
		
		dl.setView(textView);
		dl.setPositiveButton(getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						// 向服务器提交推荐请求
						jobRecommend();
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
