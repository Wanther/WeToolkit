package cn.wanther.toolkit.manager;

import cn.wanther.toolkit.component.async.DataLoadAsyncTazk;

public abstract class LocalDataCallbackAsync<Result> extends DataLoadAsyncTazk<Result> {

    private LocalDataCallback<Result> mCallback;

    public LocalDataCallbackAsync(LocalDataCallback<Result> callback) {
        mCallback = callback;
    }

    protected LocalDataCallback<Result> getCallback(){
        return mCallback;
    }

    @Override
    public void onStart() {
        if(mCallback != null){
            mCallback.onStart();
        }
    }

    @Override
    protected void onRealDataLoaded(Result result) {
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

    @Override
    protected void onLocalDataLoaded(Result result) {
        if(mCallback != null){
            mCallback.onLocalDataLoaded(result);
        }
    }

}
