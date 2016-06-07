package cn.wanther.toolkit.component.async;

import android.os.Handler;
import android.os.Process;
import android.util.Log;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public abstract class AsyncTazk<Result> implements Callable<Result>{
	
	private static final String TAG = "AsyncTazk";

	private FutureTask<Result> mTask;
	
	/**
	 * invoke when is just submit to executor
	 */
	protected void onStart(){}
	/**
	 * this method runs in background
	 */
	protected abstract Result doInBackground() throws Exception;
	
	// following methods run on UI thread
	protected void onSuccess(Result result){}

	protected void onCancelled(){
		if(BuildConfig.DEBUG){
			Log.v(TAG, "onCancelled");
		}
	}
	protected void onError(Exception e){
		if(BuildConfig.DEBUG){
			Log.w(TAG, e.getMessage(), e);
		}
	}
	/**
	 * whenever success or failed or cancelled, this method invoked at last
	 */
	protected void onFinished(){
		if(BuildConfig.DEBUG){
			Log.v(TAG, "onFinished");
		}
	}
	
	public Future<Result> execute(Executor exec){
		onStart();

		if (mTask != null) {
			throw new RuntimeException("do not execute the same task");
		}

		mTask = new AsyncTask(this);
		exec.execute(mTask);

		return mTask;
	}

	@Override
	public final Result call() throws Exception {
		return doInBackground();
	}

	/*package*/ void postIf(Runnable callback){
		App app = App.Instance();
		if(app == null){
			Log.w(TAG, "wanner callback , but app is null");
			return;
		}

		Handler uiHandler = app.getUIHandler();

		if(uiHandler == null){
			Log.w(TAG, "wanner callback , but uiHandler is null");
			return;
		}

		uiHandler.post(callback);
	}

	/*package*/ Future<?> getTask() {
		return mTask;
	}

	/*package*/ class AsyncTask extends FutureTask<Result>{

		public AsyncTask(Callable<Result> callable) {
			super(callable);
		}
		
		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			super.run();
		}

		@Override
		protected void done() {
			postIf(new Runnable(){
				public void run(){
					try {
						if(isCancelled()){
							throw new CancellationException("task is cancelled");
						}

						onSuccess(get());

					} catch (InterruptedException e) {
						onCancelled();
					} catch(CancellationException e){
						onCancelled();
					} catch (ExecutionException e) {
						Throwable cause = e.getCause();
						if(cause instanceof Exception){
							onError((Exception)cause);
						}else{
							// java.lang.Error ...
							throw new RuntimeException(cause);
						}
					} finally {
						onFinished();
						mTask = null;
					}
				}
			});
		}
		
	}
}
