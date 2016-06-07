package cn.wanther.toolkit;

import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import cn.wanther.http.HttpExecutor;
import cn.wanther.toolkit.component.AppHttpExecutor;
import cn.wanther.toolkit.component.AppThreadFactory;
import cn.wanther.toolkit.component.ConfigLoader;
import cn.wanther.toolkit.component.DataBaseHelper;
import cn.wanther.toolkit.component.Push;
import cn.wanther.toolkit.component.Statistics;
import cn.wanther.toolkit.component.impl.ConfigLoaderFromXml;
import cn.wanther.toolkit.component.impl.StatisticsNothing;
import cn.wanther.toolkit.component.impl.StatisticsUmeng;
import cn.wanther.toolkit.dao.BasicDaoSupport;
import cn.wanther.toolkit.dao.DataBaseHelperV1;
import cn.wanther.toolkit.dao.LocalDataDao;
import cn.wanther.toolkit.dao.SysDao;
import cn.wanther.toolkit.dao.impl.LocalDataDaoImpl;
import cn.wanther.toolkit.dao.impl.SysDaoImpl;
import cn.wanther.toolkit.manager.SysManager;
import cn.wanther.toolkit.manager.impl.SysManagerImpl;
import cn.wanther.toolkit.utils.Compat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WeLearnToolkit extends App {
	private ThreadFactory mThreadFactory;
	private Executor mShortTimeExecutor;
	private Executor mConcurrentExecutor;
	private HttpExecutor mHttpExecutor;
	private Statistics mStatistics;
	private DataBaseHelper mDBHelper;
	private LocalDataDao mLocalDataDao;
	private SysDao mSysDao;

	@Override
	public void onCreate() {
		super.onCreate();

		// analystics
		

		Push.create(this);
	}

	@Override
	protected ConfigLoader getConfigLoader() {
		return new ConfigLoaderFromXml();
	}

	@Override
	public synchronized ThreadFactory getThreadFactory() {
		if(mThreadFactory == null){
			AppThreadFactory threadFactory = new AppThreadFactory();
			threadFactory.setName("WeLearnToolkit");
			
			mThreadFactory = threadFactory;
		}
		return mThreadFactory;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public synchronized Executor getShortTimeExecutor() {
		if(mShortTimeExecutor == null){
			if(Compat.hasHONEYCOMB()){
				mShortTimeExecutor = AsyncTask.SERIAL_EXECUTOR;
			}else{
				mShortTimeExecutor = Executors.newSingleThreadExecutor(getThreadFactory());
			}
		}
		return mShortTimeExecutor;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public synchronized Executor getConcurrentExecutor() {
		if(mConcurrentExecutor == null){
			if(Compat.hasHONEYCOMB()){
				mConcurrentExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
			}else{
				int cpuCount = Runtime.getRuntime().availableProcessors();
				mConcurrentExecutor = new ThreadPoolExecutor(cpuCount + 1, cpuCount * 2 + 1, 1,
						TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128), getThreadFactory());
			}
		}
		return mConcurrentExecutor;
	}

	@Override
	public synchronized HttpExecutor getHttpExecutor() {
		if(mHttpExecutor == null){
			HttpExecutor.setDebug(BuildConfig.DEBUG);
			AppHttpExecutor executor = new AppHttpExecutor();
			//get App package name and version code
			try {
				PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
				executor.setPackageName(pi.packageName);
				executor.setVersionCode(pi.versionCode);
			} catch (NameNotFoundException e) {
				if(BuildConfig.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
				executor.setPackageName("cn.wanther.toolkit");
				//executor.setVersionCode(0);
			}
			
			mHttpExecutor = executor;
		}
		return mHttpExecutor;
	}

	@Override
	public synchronized Statistics getStatistics() {
		if (mStatistics == null) {
			if (BuildConfig.DEBUG) {
				mStatistics = new StatisticsNothing();
			} else {
				mStatistics = new StatisticsUmeng(this);
			}
			//mStatistics = new StatisticsUmeng(this);
		}
		return mStatistics;
	}

	@Override
	public synchronized DataBaseHelper getDBHelper() {
		if (mDBHelper == null) {
			String dbName = getProperty(DataBaseHelper.KEY_DB_DATA_NAME);
			int version = Integer.parseInt(getProperty(DataBaseHelper.KEY_DB_DATA_VERSION));
			mDBHelper = new DataBaseHelperV1(this, dbName, null, version);
		}
		return mDBHelper;
	}

	@Override
	public synchronized LocalDataDao getLocalDataDao() {
		if (mLocalDataDao == null) {
			mLocalDataDao = new LocalDataDaoImpl();
			((BasicDaoSupport)mLocalDataDao).setDBHelper(getDBHelper());
		}
		return mLocalDataDao;
	}

	@Override
	public synchronized SysDao getSysDao() {
		if (mSysDao == null) {
			mSysDao = new SysDaoImpl();
			((BasicDaoSupport)mSysDao).setDBHelper(getDBHelper());
		}
		return mSysDao;
	}

	@Override
	public SysManager getSysManager() {
		return new SysManagerImpl();
	}

}
