package cn.wanther.toolkit.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;

import org.json.JSONObject;

import java.util.concurrent.Future;

public class Utils {
	
	private static final String TAG = "Utils";
	
	public static void cancelTask(Future<?> task, boolean interrupt){
		if(task != null && !task.isDone()){
			task.cancel(interrupt);
		}
	}
	
	public static String optJSONString(JSONObject json, String key){
		if(json == null || key == null){
			return null;
		}
		String value = json.optString(key, null);
		if("null".equals(value)){
			value = null;
		}
		return value;
	}
	
	public static int getByteCount(String str){
		if(TextUtils.isEmpty(str)){
			return 0;
		}
		
		int totalCount = 0;
		
		for(int i = 0, len = str.length(); i < len; i++){
			char cur = str.charAt(i);
			int byteCount = (cur + "").getBytes().length;
			totalCount += (byteCount > 1 ? 2 : 1);
		}
		
		return totalCount;
	}

	public static String getVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(App.Instance().getPackageName(), 0);
			return pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, e.getMessage(), e);
			}
			return "UNKNOWN";
		}

	}

}
