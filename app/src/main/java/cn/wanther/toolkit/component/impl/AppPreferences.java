package cn.wanther.toolkit.component.impl;

import android.content.SharedPreferences;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.component.Preferences;
import cn.wanther.toolkit.utils.Compat;

/**
 * 选项的SharedPreferences实现
 * @author wanghe
 *
 */
public class AppPreferences implements Preferences {
	
	private App mApp;
	
	public AppPreferences(App app){
		mApp = app;
	}

	@Override
	public String getString(String key) {
		return getString(key, null);
	}

	@Override
	public String getString(String key, String defValue) {
		SharedPreferences prefs = Compat.getSharedPreferences(mApp.getApplicationContext(), "prefs");
		return prefs.getString(key, defValue);
	}

	@Override
	public int getInt(String key) {
		return getInt(key, 0);
	}

	@Override
	public int getInt(String key, int defValue) {
		SharedPreferences prefs = Compat.getSharedPreferences(mApp.getApplicationContext(), "prefs");
		return prefs.getInt(key, defValue);
	}

	@Override
	public boolean getBool(String key) {
		return getBool(key, false);
	}

	@Override
	public boolean getBool(String key, boolean defValue) {
		SharedPreferences prefs = Compat.getSharedPreferences(mApp.getApplicationContext(), "prefs");
		return prefs.getBoolean(key, defValue);
	}

}
