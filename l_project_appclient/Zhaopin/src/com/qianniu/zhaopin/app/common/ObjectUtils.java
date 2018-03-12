package com.qianniu.zhaopin.app.common;

import com.alibaba.fastjson.JSONArray;

public class ObjectUtils {
	/**
	 * 判断一个对象是否为null,如果这个对象时字符串，则判断这个字符串是否为null或者“”
	 * 
	 * @param source
	 * @return
	 */
	public static boolean isEmpty(Object source) {
		if (source instanceof String) {
			return StringUtils.isEmpty((String) source);
		}
		if (null == source) {
			return true;
		}
		return false;
	}

	/**
	 * 将对象类型转换为字符串，并过滤掉对象中指定的属性
	 * 
	 * @param incFields
	 *            被过滤的属性数组
	 * @param excFields
	 *            被排除在外的属性
	 * @param obj
	 *            被类型转换的对象
	 * @return
	 */
/*	public static String getObjectJsonString(String[] incFields,
			String excFields, Object obj) {
		return new flexjson.JSONSerializer().include(incFields)
				.exclude(excFields).serialize(obj);
	}
*/
	/**
	 * 把对象解析为json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String getJsonStringFromObject(Object obj) {
		return com.alibaba.fastjson.JSON.toJSONString(obj);

	}

	/**
	 * 把json字符串解析为对象
	 * 
	 * @param jsonStr
	 * @param clz
	 * @return
	 */
	public static Object getObjectFromJsonString(String jsonStr, Class clz) {
		return com.alibaba.fastjson.JSON.parseObject(jsonStr, clz);
	}
//	public static String getStringFromJsonArray(String[] json) {
//		return com.alibaba.fastjson.JSON.toJSONString(json);
//	}
//	public static String[] getStringArrayFromString(String jsonStr) {
//		JSONArray jsonArray = com.alibaba.fastjson.JSON.parseArray(jsonStr);
//		return jsonArray.toArray();
//	}
	
	/**
	 * 把json字符串解析为String[]
	 * @param jsonStr
	 * @return
	 */
	public static String[] getStringArrayFormJsonString(String jsonStr){
		
		JSONArray json = JSONArray.parseArray(jsonStr);
		
		Object[] objArray = json.toArray();
		
		if(null != objArray){
			int m = objArray.length;
			String[] strArray = new String[m];
			for(int i = 0; i< m; i++){
				strArray[i] = (String)objArray[i];
			}
			
			return strArray;
		}else{
			return null;
		}
	}
}
