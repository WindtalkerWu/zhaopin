package com.qianniu.zhaopin.app.constant;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.VersionData;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.util.NumberUtils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class ActionConstants {
	
	/*brodcast_type_id---brodcast_type_id---brodcast_type_id---brodcast_type_id---brodcast_type_id---brodcast_type_id---*/
	//消息模块有新消息
	public static final int BRODCAST_ID_FRAGMENTINFO_NEW = 1;

	/*action_jump_id---action_jump_id---action_jump_id---action_jump_id---action_jump_id---action_jump_id---action_jump_id---*/
	//登录界面
	public static final int ACTINNER_ID_LOGIN = 1;
	//注册界面
	public static final int ACTINNER_ID_REGISTER = 2;
	//消息首页
	public static final int ACTINNER_ID_FRAGMENTINFO = 3;
	//任务首页
	public static final int ACTINNER_ID_FRAGMENTREAWRD = 4;
	//号外首页
	public static final int ACTINNER_ID_FRAGMENTEXTRA = 5;
	
	/*action---action---action---action---action---action---action---action---action---action---action---action---*/
	//登录界面启动 :(action:ACTION_LOGIN ； catagory ：Intent.CATEGORY_DEFAULT)
	public static final String ACTION_LOGIN = "com.matrixdigi.action.login";
	
	//注册界面启动 :(action:ACTION_REGISTER ； catagory ：Intent.CATEGORY_DEFAULT)
	public static final String ACTION_REGISTER = "com.matrixdigi.action.register";
	
	//消息首页界面启动 :(action:ACTION_FRAGMENT_INFO ； catagory ：Intent.CATEGORY_DEFAULT)
	//消息首页广播:(action:ACTION_FRAGMENT_INFO ； catagory ：ActionConstants.CATEGORY_FRAGMENT_DEFAULT)
	public static final String ACTION_FRAGMENT_INFO = "com.matrixdigi.action.fragment.info";
	//消息模块有新消息:(action:ACTION_FRAGMENT_INFO_NEW ； catagory ：Intent.CATEGORY_DEFAULT)
	public static final String ACTION_FRAGMENT_INFO_NEW = "com.matrixdigi.action.fragment.info.new";

	//任务首页界面启动 :(action:ACTION_FRAGMENT_INFO ； catagory ：Intent.CATEGORY_DEFAULT)
	//任务首页广播:(action:ACTION_FRAGMENT_INFO ； catagory ：ActionConstants.CATEGORY_FRAGMENT_DEFAULT)
	public static final String ACTION_FRAGMENT_REWARD = "com.matrixdigi.action.fragment.reward";

	//号外首页界面启动 :(action:ACTION_FRAGMENT_INFO ； catagory ：Intent.CATEGORY_DEFAULT)
	//号外首页广播:(action:ACTION_FRAGMENT_INFO ； catagory ：ActionConstants.CATEGORY_FRAGMENT_DEFAULT)
	public static final String ACTION_FRAGMENT_EXTRA = "com.matrixdigi.action.fragment.extra";
	//号外模块有新消息:(action:ACTION_FRAGMENT_EXTRA_NEW ； catagory ：Intent.CATEGORY_DEFAULT)
	public static final String ACTION_FRAGMENT_EXTRA_NEW = "com.matrixdigi.action.fragment.extra.new";

	//我首页界面启动 :(action:ACTION_FRAGMENT_INFO ； catagory ：Intent.CATEGORY_DEFAULT)
	//我首页广播:(action:ACTION_FRAGMENT_INFO ； catagory ：ActionConstants.CATEGORY_FRAGMENT_DEFAULT)
	public static final String ACTION_FRAGMENT_ACTIVE = "com.matrixdigi.action.fragment.active";
	
	public static final String CATEGORY_FRAGMENT_DEFAULT = "com.matrixdigi.category.fragment.default";
	//service 下载
	public static final String ACTION_SERVICE_DOWNLOAD = "com.matrixdigi.action.service.download";
	//service 取消下载
	public static final String ACTION_SERVICE_DOWNLOAD_CANCEL = "com.matrixdigi.action.service.download_cancel";
	
	/** 获取直接启动消息首页intent
	 * @return Intent
	 */
	public static Intent getFragmentInfoStartIntent(){
		Intent intent = new Intent(ActionConstants.ACTION_FRAGMENT_INFO);
		return intent;		
	}
	/**获取广播启动消息首页intent
	 * @return
	 */
	public static Intent getFragmentInfoBrocastStartIntent(){
		Intent intent = new Intent(ActionConstants.ACTION_FRAGMENT_INFO);
		intent.addCategory(ActionConstants.CATEGORY_FRAGMENT_DEFAULT);
		return intent;		
	}
	
	public static Intent getUpdateVersionStartIntent(Context context, String versionDataStr) {
		if (!TextUtils.isEmpty(versionDataStr)) {
			try {
				VersionData versionData = (VersionData) ObjectUtils
						.getObjectFromJsonString(versionDataStr, VersionData.class);
				String oldVersion = ((AppContext)context).getCurrentVersion().getVersion();
				String newVersion = versionData.getVersion();
				if (!TextUtils.isEmpty(versionData.getDownloadurl()) && NumberUtils.isHighVersion(oldVersion, newVersion)) {
					Intent intent = new Intent("qn.update.version");
					intent.putExtra("versionData", versionData);
					return intent;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;		
	}
	
	/**获取消息模块有新消息广播intent
	 * @return
	 */
	public static Intent getFragmentInfoBrocastNewMsgIntent(){
		Intent intent = new Intent(ActionConstants.ACTION_FRAGMENT_INFO_NEW);
		return intent;		
	}
}
