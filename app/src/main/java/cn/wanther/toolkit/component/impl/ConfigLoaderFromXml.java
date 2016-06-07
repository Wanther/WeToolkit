package cn.wanther.toolkit.component.impl;

import java.util.List;
import java.util.Properties;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.XmlResourceParser;
import android.util.Log;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.R;

public class ConfigLoaderFromXml extends ConfigLoaderFromFile {

	@Override
	protected void load(Properties props, List<String> modeList) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "load config from xml");
		}
		
		for(String mode : modeList){
			xmlConfigToProperty(getResourceIdOfMode(mode), props);
		}
	}
	
	protected int getResourceIdOfMode(String mode){
		try {
			Class<?> resXml = Class.forName("cn.wanther.toolkit.R$xml");
			return resXml.getField("config_" + mode).getInt(resXml);
		} catch (Exception e) {
			if(BuildConfig.DEBUG){
				Log.e(TAG, "cannot find R.xml.config_" + mode, e);
			}
			return R.xml.config_common;
		}
	}
	
	protected void xmlConfigToProperty(int xmlResourceId, Properties props){
		try{
			XmlResourceParser parser = App.Instance().getResources().getXml(xmlResourceId);
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT){
				if(eventType == XmlPullParser.START_TAG){
					String tagName = parser.getName();
					if("config".equals(tagName)){
						String key = parser.getAttributeValue(null, "key");
						String value = parser.getAttributeValue(null, "value");
						
						props.setProperty(key, value);
					}
				}
				
				eventType = parser.next();
			}
		}catch(Exception e){
			if(BuildConfig.DEBUG){
				Log.e(TAG, e.getMessage(), e);
			}
		}
		
	}

}
