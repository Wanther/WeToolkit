package cn.wanther.toolkit.ui.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import cn.wanther.toolkit.App;

public abstract class BaseActivity extends AppCompatActivity {
	
	private static final String TAG = "BaseActivity";

	private boolean mPageBehavior = true;
	
	private ProgressDialog mProgressDialog;

	public App getApp(){
		return App.Instance();
	}

	protected void requestNoPageBehavior() {
		mPageBehavior = false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showProgressDialog(int resId){
		showProgressDialog(getString(resId));
	}
	
	public void showProgressDialog(CharSequence msg){
		if(mProgressDialog == null){
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setCancelable(false);
		}
		
		mProgressDialog.setMessage(msg);
		
		if(!mProgressDialog.isShowing()){
			mProgressDialog.show();
		}
	}
	
	public void dismissProgressDialog(){
		if(mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}
	
	public void post(Runnable command){
		getApp().getUIHandler().post(command);
	}
	
	public void postDelayed(Runnable command, long delayedMs){
		getApp().getUIHandler().postDelayed(command, delayedMs);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mPageBehavior) {
			getApp().getStatistics().onPageStart(this, getPageName());
		}
	}

	@Override
	protected void onPause() {

		if (mPageBehavior) {
			getApp().getStatistics().onPageEnd(this, getPageName());
		}

		super.onPause();
	}

	public void dealWithException(Exception e){
		getApp().dealWithException(this, e);
	}

	@Override
	protected void onDestroy() {
		dismissProgressDialog();

		super.onDestroy();
	}

	protected abstract String getPageName();
	
}
