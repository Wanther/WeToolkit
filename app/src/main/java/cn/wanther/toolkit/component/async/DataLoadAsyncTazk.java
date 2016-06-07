package cn.wanther.toolkit.component.async;

import android.util.Log;

import cn.wanther.toolkit.BuildConfig;

import java.util.concurrent.Future;

public abstract class DataLoadAsyncTazk<Result> extends AsyncTazk<Result> {
	
	private static final String TAG = "DataLoadAsyncTazk";
	
	@Override
	public Result doInBackground() throws Exception {
		Result result = null;
		try {
			result = loadLocalData();
		} catch (Exception e){
			if(BuildConfig.DEBUG){
				Log.w(TAG, "local data load failed", e);
			}
		}
		
		postIf(new LocalDataCallback(result));

		result = loadRealData();
		
		updateLocalData(result);
		
		return result;
	}
	
	@Override
	public void onSuccess(Result result) {
		onRealDataLoaded(result);
	}

	/**
	 * 读取本地数据 background
	 * @return
	 * @throws Exception
	 */
	protected Result loadLocalData() throws Exception {
		return null;
	}

	/**
	 * 本地数据读取完成 main thread
	 * @param result
	 */
	protected void onLocalDataLoaded(Result result){}
	
	/**
	 * 读取网络数据 background
	 * @return
	 */
	protected Result loadRealData() throws Exception {
		return null;
	}

	/**
	 * 网络数据返回后更新本地数据 background
	 * @param result
	 */
	protected void updateLocalData(Result result){}
	
	/**
	 * 网络数据读取完成 main thread
	 * @param result
	 */
	protected void onRealDataLoaded(Result result){}

	private class LocalDataCallback implements Runnable {
		public Result mResult;
		
		public LocalDataCallback(Result result) {
			mResult = result;
		}

		@Override
		public void run() {
			Future<?> task = getTask();
			if (task != null && !task.isCancelled()) {
				onLocalDataLoaded(mResult);
			}
		}
		
	}
	
}
