package cn.wanther.toolkit.ui.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.ui.activity.BaseActivity;

public abstract class BaseFragment extends Fragment {
	
	private static final String TAG = "BaseFragment";

	private boolean mPageBehavior;
	
	public App getApp(){
		return getBaseActivity().getApp();
	}

	protected void requestPageBehavior() {
		mPageBehavior = true;
	}
	
	public BaseActivity getBaseActivity(){
		return (BaseActivity)getActivity();
	}
	
	protected void postIf(Runnable command){
		BaseActivity activity = getBaseActivity();
		if(activity != null){
			activity.post(command);
		}
	}
	
	protected void postDelayedIf(Runnable command, int delayedMs){
		BaseActivity activity = getBaseActivity();
		if(activity != null){
			activity.postDelayed(command, delayedMs);
		}
	}
	
	public void dealWithException(Exception e){
		BaseActivity activity = getBaseActivity();
		if(activity != null){
			activity.dealWithException(e);
		}else{
			if(BuildConfig.DEBUG){
				Log.e(TAG, e.getLocalizedMessage(), e);
			}
		}
	}
	
	public void showProgressDialog(int resId){
		showProgressDialog(getString(resId));
	}
	
	public void showProgressDialog(CharSequence msg){
		BaseActivity activity = getBaseActivity();
		
		if(activity != null){
			activity.showProgressDialog(msg);
		}
		
	}
	
	public void dismissProgressDialog(){
		BaseActivity activity = getBaseActivity();
		
		if(activity != null){
			activity.dismissProgressDialog();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mPageBehavior) {
			getApp().getStatistics().onPageStart(getActivity(), getPageName());
		}
	}

	@Override
	public void onPause() {

		if (mPageBehavior) {
			getApp().getStatistics().onPageEnd(getActivity(), getPageName());
		}

		super.onPause();
	}

	protected abstract String getPageName();
}
