package cn.wanther.toolkit.component;

public interface Preferences {
	String getString(String key);
	String getString(String key, String defValue);
	int getInt(String key);
	int getInt(String key, int defValue);
	boolean getBool(String key);
	boolean getBool(String key, boolean defValue);
}
