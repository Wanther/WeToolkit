package cn.wanther.toolkit.manager;

import cn.wanther.toolkit.component.async.AsyncTazk;


public abstract class CallbackAsync<Result> extends AsyncTazk<Result> {
	private Callback<Result> mCallback;
	
	public CallbackAsync(Callback<Result> callback){
		mCallback = callback;
	}
	
	protected Callback<Result> getCallback(){
		return mCallback;
	}

	@Override
	public void onStart() {
		if(mCallback != null){
			mCallback.onStart();
		}
	}

	@Override
	public void onSuccess(Result result) {
		if(mCallback != null){
			mCallback.onOperateSuccess(result);
		}
	}

	@Override
	public void onError(Exception e) {
		if(mCallback != null){
			mCallback.onOperateError(e);
		}
	}
	
	@Override
	public void onFinished() {
		if(mCallback != null){
			mCallback.onOperateFinish();
		}
	}

}
