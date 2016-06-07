package cn.wanther.toolkit;

import java.util.Properties;

import cn.wanther.http.exception.AccessException;
import cn.wanther.toolkit.component.ConfigLoader;
import cn.wanther.toolkit.component.Preferences;
import cn.wanther.toolkit.component.impl.AppPreferences;
import cn.wanther.toolkit.exception.ApiException;
import cn.wanther.toolkit.exception.NetworkException;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public abstract class App extends Application implements Constants, BeanContext{
	
	public static final String TAG = "App";
	
	private static App sInstance;
	private static Handler sUIHandler;
	
	public static App Instance(){
		return sInstance;
	}
	
	// * 应用参数/配置
	private Properties mProperties = new Properties();
	// * 用户行为相关的属性/选项
	private Preferences mPreferences;
	
	public String getProperty(String key){
		return mProperties.getProperty(key);
	}
	
	public Preferences getPreferences(){
		return mPreferences;
	}
	
	public Handler getUIHandler(){
		return sUIHandler;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		sInstance = this;
		
		mPreferences = new AppPreferences(this);
		
		getConfigLoader().load(mProperties);
		
		sUIHandler = onCreateUIHandler();
	}
	
	protected Handler onCreateUIHandler(){
		return new Handler();
	}
	
	protected Properties getProperties(){
		return mProperties;
	}

	public void dealWithException(Context context, Exception e){
		if(BuildConfig.DEBUG){
			Log.e(TAG, e.getMessage(), e);
		}

		try {
			throw e;
		} catch (NetworkException ex) {
			Toast.makeText(context, R.string.netwrok_error, Toast.LENGTH_SHORT).show();
		} catch (ApiException ex) {
			Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		} catch (AccessException ex) {
			Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();;
		} catch (Exception ex) {
			Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}

	}
	
	protected abstract ConfigLoader getConfigLoader();
	
}
