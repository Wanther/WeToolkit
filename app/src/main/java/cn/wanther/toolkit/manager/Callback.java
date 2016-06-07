package cn.wanther.toolkit.manager;


public interface Callback<T> {
	void onStart();
	void onOperateSuccess(T data);
	void onOperateError(Exception e);
	void onOperateFinish();
}
