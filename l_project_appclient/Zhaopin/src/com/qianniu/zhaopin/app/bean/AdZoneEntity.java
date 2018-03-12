package com.qianniu.zhaopin.app.bean;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.content.ContentValues;
import android.text.TextUtils;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.database.DBUtils;


/*{
    "id": "宣传位实体ID",
    "type_id": "宣传位类型 1=>pic, 2=>action, 3=>link",
    "position_id": "宣传位所在位置 1=>招聘通栏, 2=>消息模块, 3=>登陆页",
    "pic_url": "图片地址",
    "title": "标题",
    "action_id": "app内跳转地址. 当 type_id=>2时 有效",
    "click_url": "点击地址. 当 type_id=>3时 有效"
  }*/

/*resid为自增id, label为 app内页编号 1=>登录界面, 2=>注册界面, 3=>消息首屏, 4=>任务首屏, 5=>号外首屏*/
/**
 * @author Administrator
 *宣传位数据实体
 */
public class AdZoneEntity extends Entity{

	
	private final static String NODE_ID = "id";
	private final static String NODE_TYPE = "type_id";
	private final static String NODE_PIC_URL = "pic_url";
	private final static String NODE_CLICK_URL = "click_url";
	private final static String NODE_ACTION_ID = "action_id";
	private final static String NODE_TITLE = "title";
	



	private String adId;
	private int type;
	private String pic_url;
	private String click_url;
	private String title;
	private int actionId;
	
	private Result validate;
	
	public final int getActionId() {
		return actionId;
	}

	public final void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}
	public final String getAdId() {
		return adId;
	}

	public final void setAdId(String zoneId) {
		this.adId = zoneId;
	}

	public final int getType() {
		return type;
	}

	public final void setType(int type) {
		this.type = type;
	}

	public final String getPic_url() {
		return pic_url;
	}

	public final void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public final String getClick_url() {
		return click_url;
	}

	public final void setClick_url(String click_url) {
		this.click_url = click_url;
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public static AdZoneEntity parse(AppContext appContext,
			JSONObject jsonObj, int zoneid, boolean bsavetoDb)
			throws IOException, AppException {

		AdZoneEntity entity = new AdZoneEntity();
		Result res = null;

		try {
			entity.adId = jsonObj.getString(NODE_ID);
			entity.type = jsonObj.getInt(NODE_TYPE);
			entity.pic_url = jsonObj.getString(NODE_PIC_URL);
			entity.click_url = jsonObj.getString(NODE_CLICK_URL);
			entity.title = jsonObj.getString(NODE_TITLE);
			entity.actionId = jsonObj.getInt(NODE_ACTION_ID);
			res = new Result(1, "ok");
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				DBUtils db = DBUtils.getInstance(appContext);

				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_AD_ID, entity.adId);
				values.put(DBUtils.KEY_AD_TYPE, entity.type);
				values.put(DBUtils.KEY_PIC_URL, entity.pic_url);
				values.put(DBUtils.KEY_CLICK_URL, entity.click_url);
				values.put(DBUtils.KEY_AD_TITLE, entity.title);
				values.put(DBUtils.KEY_AD_ACTIONID, entity.actionId);
				values.put(DBUtils.KEY_TIMESTAMP,
						String.valueOf(System.currentTimeMillis()));
				values.put(DBUtils.KEY_ZONE_ID, String.valueOf(zoneid));
				db.save(DBUtils.adZoneTableName, values);
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			entity.validate = res;

		}
		return entity;

	}
	public static AdZoneEntity parse(AppContext appContext, String adZoneEntityStr) {
		AdZoneEntity entity = null;
		if (!TextUtils.isEmpty(adZoneEntityStr)) {
			try {
				entity = new AdZoneEntity();
				JSONObject jsonObj = new JSONObject(adZoneEntityStr);
				entity.adId = jsonObj.getString(NODE_ID);
				entity.type = jsonObj.getInt(NODE_TYPE);
				entity.pic_url = jsonObj.getString(NODE_PIC_URL);
				entity.click_url = jsonObj.getString(NODE_CLICK_URL);
				entity.title = jsonObj.getString(NODE_TITLE);
				entity.actionId = jsonObj.getInt(NODE_ACTION_ID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return entity;

	}
}
