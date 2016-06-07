package cn.wanther.toolkit.component.impl;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.component.ConfigLoader;
import cn.wanther.toolkit.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigLoaderFromFile implements ConfigLoader {
	
	protected static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{.+?\\}");

	private String mMode;

	@Override
	public void load(Properties props) {

		mMode = getAppMode();

		List<String> modeList = parseAppMode(mMode);

		load(props, modeList);

		//deal with placeholders ${..}
		Enumeration<?> propNames = props.propertyNames();
		while(propNames.hasMoreElements()){
			String propName = (String)propNames.nextElement();

			replacePlaceholder(propName, props);
		}

	}
	
	protected void load(Properties props, List<String> modeList){
		
		AssetManager assetManager = App.Instance().getAssets();
		
		if(props == null || assetManager == null
				|| modeList == null || modeList.size() <= 0){
			return;
		}
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "load config from assets");
		}
		for(String mode : modeList){
			String confPath = "config/" + mode + ".properties";
			InputStream input = null;
			try {
				input = assetManager.open(confPath);
				
				props.load(input);

			} catch (IOException e) {
				if(BuildConfig.DEBUG){
					Log.e(TAG, confPath + " load failed", e);
				}
			} finally {
				IOUtil.close(input);
			}
		}
		
	}
	
	protected List<String> parseAppMode(String mode){
		// 默认必须会有一个common
		List<String> modeList = new LinkedList<String>();
		modeList.add("common");

		if(TextUtils.isEmpty(mode)){
			return modeList;
		}
		
		String[] modes = mode.split("[,]");
		for(String modeName : modes){
			modeList.add(modeName.trim());
		}
		
		return modeList;
	}
	
	protected void replacePlaceholder(String key, Properties props){
		String value = props.getProperty(key);
		
		Matcher m = PLACEHOLDER_PATTERN.matcher(value);
		
		boolean has = false;
		
		while(m.find()){
			has = true;
			String match = m.group();
			String propName = match.substring(2, match.length() - 1);
			
			replacePlaceholder(propName, props);
			
			value = m.replaceFirst(props.getProperty(propName));
		}
		
		if(has){
			props.put(key, value);
		}
	}

	protected String getAppMode(){
		if(mMode != null){
			return mMode;
		}
		try {
			App app = App.Instance();
			ApplicationInfo appInfo = app.getPackageManager().getApplicationInfo(app.getPackageName(), PackageManager.GET_META_DATA);

			mMode = appInfo.metaData.getString(App.KEY_APP_MODE);

			if (TextUtils.isEmpty(mMode)) {
				mMode = App.KEY_APP_MODE_DEFAULT;
			}

			return mMode;
		} catch (PackageManager.NameNotFoundException e) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "get application meta failed ", e);
			}

			return App.KEY_APP_MODE_DEFAULT;
		}
	}

}
