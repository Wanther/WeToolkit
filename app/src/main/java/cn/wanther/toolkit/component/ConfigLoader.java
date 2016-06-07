package cn.wanther.toolkit.component;

import java.util.Properties;


public interface ConfigLoader {
	String TAG = "ConfigLoader";
	
	String DEFAULT_MODE = "common";
	
	void load(Properties props);
}
