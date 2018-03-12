package com.qianniu.zhaopin.app.bean;

import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.ActionConstants;
import com.qianniu.zhaopin.app.ui.InsidersListActivity;
import com.qianniu.zhaopin.app.ui.SeekWorthyActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActionJumpEntity extends Entity {
	/*
	 * 动作类型**********************************************************************
	 * ***************
	 */
	public static final int ACTTYPE_DEFAULT = 0;
	// 不做任何操作
	public static final int ACTTYPE_NONE = 1;
	// 内部跳转
	public static final int ACTTYPE_INNERJUMP = 2;
	// url跳转
	public static final int ACTTYPE_URLJUMP = 3;
	/** 内部跳转对应数据 ******************************************************************************************/
	// 登录界面
	public static final int ACTINNER_LOGIN = 1;
	// 注册界面
	public static final int ACTINNER_REGISTER = 2;
	// 消息界面
	public static final int ACTINNER_FRAGMENTINFO = 3;
	// 任务界面
	public static final int ACTINNER_FRAGMENTREAWRD = 4;
	// 号外界面
	public static final int ACTINNER_FRAGMENTEXTRA = 5;
	// 名人界面
	public static final int ACTINNER_BOSS = 6;
	// 名企界面
	public static final int ACTINNER_COMPANY = 7;
	// 活动界面
	public static final int ACTINNER_COMPAGIN = 8;
	// 行业咨询界面
	public static final int ACTINNER_CONSULTATION = 9;
	// 薪酬调查界面
	public static final int ACTINNER_SALARY_SURVEY = 10;
	// 个税计算界面
	public static final int ACTINNER_TAXCALCULATOR = 11;
	// 牵牛推荐界面
	public static final int ACTINNER_APP_RECOMMENT = 12;
	// 我的简历库界面
	public static final int ACTINNER_RESUMES_LIST = 13;
	// 订阅管理界面
	public static final int ACTINNER_SUBSSCRIPTION_MANAGER = 14;

	/********************************************************************************************/

	public static void performInnerActionJump(Context activity, int actionId,
			String jsondata) {
		if (activity == null)
			return;
		switch (actionId) {
		case ACTINNER_LOGIN: {
			/*
			 * Intent intent = new Intent(ActionConstants.ACTION_LOGIN);
			 * activity.startActivity(intent);
			 */
			UIHelper.showLoginActivity(activity);
		}
			break;
		case ACTINNER_REGISTER: {
			/*
			 * Intent intent = new Intent(ActionConstants.ACTION_REGISTER);
			 * activity.startActivity(intent);
			 */
			UIHelper.showRegisterActivity(activity);
		}
			break;
		case ACTINNER_FRAGMENTINFO: {
			Intent intenta = new Intent();
			intenta.setAction(ActionConstants.ACTION_FRAGMENT_INFO);
			intenta.addCategory(Intent.CATEGORY_DEFAULT);
			activity.startActivity(intenta);

			Intent intentx = new Intent();
			intentx.setAction(ActionConstants.ACTION_FRAGMENT_INFO);
			intentx.addCategory(ActionConstants.CATEGORY_FRAGMENT_DEFAULT);
			activity.sendBroadcast(intentx);
		}
			break;
		case ACTINNER_FRAGMENTREAWRD: {
			Intent intenta = new Intent();
			intenta.setAction(ActionConstants.ACTION_FRAGMENT_REWARD);
			intenta.addCategory(Intent.CATEGORY_DEFAULT);
			activity.startActivity(intenta);

			Intent intentx = new Intent();
			intentx.setAction(ActionConstants.ACTION_FRAGMENT_REWARD);
			intentx.addCategory(ActionConstants.CATEGORY_FRAGMENT_DEFAULT);
			activity.sendBroadcast(intentx);
		}
			break;
		case ACTINNER_FRAGMENTEXTRA: {

			Intent intenta = new Intent();
			intenta.setAction(ActionConstants.ACTION_FRAGMENT_EXTRA);
			intenta.addCategory(Intent.CATEGORY_DEFAULT);
			activity.startActivity(intenta);

			Intent intentx = new Intent();
			intentx.setAction(ActionConstants.ACTION_FRAGMENT_EXTRA);
			intentx.addCategory(ActionConstants.CATEGORY_FRAGMENT_DEFAULT);
			activity.sendBroadcast(intentx);
		}
			break;
		case ACTINNER_BOSS: {
			UIHelper.startInsidersOrCompanyActivity(activity,
					InsidersAndCompany.TYPEINSIDERS);
		}
			break;
		case ACTINNER_COMPANY: {
			UIHelper.startInsidersOrCompanyActivity(activity,
					InsidersAndCompany.TYPECOMPANY);
		}
			break;
		
		case ACTINNER_COMPAGIN :{
			UIHelper.startCampaignListActivity(activity);
		}
			break;
		case ACTINNER_CONSULTATION:
		{
			UIHelper.startProfessionalInfoActivity(activity);
		}
			break;
		case ACTINNER_SALARY_SURVEY: {
			//UIHelper.startTaxActivity(activity);
		}
			break;
		case ACTINNER_TAXCALCULATOR: {
			UIHelper.startTaxActivity(activity);
		}
		
			break;
		case ACTINNER_APP_RECOMMENT: {
			UIHelper.startAppRecommentActivity(activity);
		}
			break;
		case ACTINNER_RESUMES_LIST: {
			UIHelper.showResumeListActivityBehindCheck(activity);
		}
			break;
		case ACTINNER_SUBSSCRIPTION_MANAGER: {
			UIHelper.showSubscriptionActivityBehindCheck(activity);
		}
			break;

		default:
			break;

		}

	}

	private static class ActionDataForBossAndCompany {
		private int id;
		private String type;

		public static ActionDataForBossAndCompany parse(String jsonStr) {

			JSONObject jsonObj = null;
			ActionDataForBossAndCompany entity = null;
			try {
				if (jsonStr != null
						&& (jsonObj = new JSONObject(jsonStr)) != null) {
					entity = new ActionDataForBossAndCompany();
					entity.id = jsonObj.getInt("id");
					entity.type = jsonObj.getString("type");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				entity = null;
			}

			return entity;
		}
	}

	private static void jumpToSeekWorthyActivity(Context context,
			ActionDataForBossAndCompany entity) {
		if (context == null || entity == null)
			return;
		Intent intent = new Intent(context, SeekWorthyActivity.class);
		InsidersAndCompany insidersAndCompany = new InsidersAndCompany();
		insidersAndCompany.setId(String.valueOf(entity.id));
		if (entity.type.equals(URLs.RSS_TYPEBOSS))
			insidersAndCompany.setType(insidersAndCompany.TYPEINSIDERS);
		else if (entity.type.equals(URLs.RSS_TYPECOMPANY)) {
			insidersAndCompany.setType(insidersAndCompany.TYPECOMPANY);
		}
		intent.putExtra("ic", insidersAndCompany);
		context.startActivity(intent);
	}
}
