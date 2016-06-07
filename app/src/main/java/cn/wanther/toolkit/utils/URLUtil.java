package cn.wanther.toolkit.utils;


import android.text.TextUtils;

import cn.wanther.toolkit.App;

public class URLUtil {
	
	private static final String TAG = "URLUtil";
	
	public static String buildServerPath(String path){
		if(TextUtils.isEmpty(path)
				|| path.matches("^[Hh][Tt][Tt][Pp][Ss]?://.+")){
			return path;
		}
		
		return App.Instance().getProperty(App.KEY_URL_SERVER_ROOT) + path;
	}
	
	public static String buildResourcePath(String path){
		if(TextUtils.isEmpty(path)
				|| path.matches("^[Hh][Tt][Tt][Pp][Ss]?://.+")){
			return path;
		}
		
		return App.Instance().getProperty(App.KEY_URL_RES_ROOT) + path;
	}
	
	public static String appendParams(String url, String paramString){
		return url + ((url.indexOf('?') < 0 ? "?" : "&") + paramString);
	}
	
	public static String getFieldString(String fields){
		String[] fieldArr = fields.split("[,]");
		StringBuilder result = new StringBuilder();
		
		for(int i = 0, len = fieldArr.length; i < len; i++){
			if(i > 0){
				result.append("&");
			}
			result.append("fields=").append(fieldArr[i]);
		}
		
		return result.toString();
	}
	
}
