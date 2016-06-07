package cn.wanther.toolkit.manager.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.BuildConfig;
import cn.wanther.toolkit.component.Push;
import cn.wanther.toolkit.exception.StorageException;
import cn.wanther.toolkit.manager.Callback;
import cn.wanther.toolkit.manager.CallbackAsync;
import cn.wanther.toolkit.manager.LocalDataCallback;
import cn.wanther.toolkit.manager.LocalDataCallbackAsync;
import cn.wanther.toolkit.manager.SysManager;
import cn.wanther.toolkit.model.DeviceInfo;
import cn.wanther.toolkit.model.OwnerInfo;
import cn.wanther.toolkit.model.UpdateInfo;

import java.util.concurrent.Future;

public class SysManagerImpl implements SysManager {
	
	@Override
	public Future<?> getDeviceOwnerInfo(Context context, LocalDataCallback<OwnerInfo> callback) {
		return new OwnerInfoGetTask(context, callback).execute(App.Instance().getConcurrentExecutor());
	}

	@Override
	public Future<?> updateOwnerInfo(Context context, OwnerInfo info, LocalDataCallback<OwnerInfo> callback) {
		return new OwnerInfoUpdateTask(context, info, callback).execute(App.Instance().getConcurrentExecutor());
	}

	@Override
	public Future<?> getUpdateInfo(Context context, Callback<UpdateInfo> callback) {
		return new UpdateInfoGetTask(context, callback).execute(App.Instance().getConcurrentExecutor());
	}

	private class OwnerInfoGetTask extends LocalDataCallbackAsync<OwnerInfo> {
		
		private Context mContext;

		private String mAppDeviceId;

		public OwnerInfoGetTask(Context context, LocalDataCallback<OwnerInfo> callback) {
			super(callback);
			mContext = context;
		}

		@Override
		public OwnerInfo doInBackground() throws Exception {

			mAppDeviceId = App.Instance().getSysDao().getLocalAppDeviceID();

			if (TextUtils.isEmpty(mAppDeviceId)) {
				mAppDeviceId = App.Instance().getSysDao().getAppDeviceID(DeviceInfo.create(mContext));
				App.Instance().getSysDao().saveLocalAppDeviceID(mAppDeviceId);
			}

			try {
				PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
				Push.Instance().setAliasAndTags("dev_id_" + mAppDeviceId, "vc_" + packageInfo.versionCode);
			} catch (Exception e) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, e.getMessage(), e);
				}
			}

			return super.doInBackground();
		}

		@Override
		protected OwnerInfo loadLocalData() throws Exception {
			return App.Instance().getSysDao().getLocalOwnerInfo();
		}

		@Override
		protected OwnerInfo loadRealData() throws Exception {
			return App.Instance().getSysDao().getOwnerInfo(mAppDeviceId);
		}

		@Override
		protected void updateLocalData(OwnerInfo ownerInfo) {
			try {
				App.Instance().getSysDao().saveLocal(ownerInfo);
			} catch (StorageException e) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "save owner to local failed", e);
				}
			}
		}
	}
	
	private class OwnerInfoUpdateTask extends CallbackAsync<OwnerInfo>{
		private Context mContext;
		private OwnerInfo mInfo;

		public OwnerInfoUpdateTask(Context context, OwnerInfo info, LocalDataCallback<OwnerInfo> callback) {
			super(callback);
			mContext = context;
			mInfo = info;
		}

		@Override
		protected OwnerInfo doInBackground() throws Exception {

			DeviceInfo deviceInfo = DeviceInfo.create(mContext);

			String appDeviceId = App.Instance().getSysDao().getLocalAppDeviceID();
			if (TextUtils.isEmpty(appDeviceId)) {
				appDeviceId = App.Instance().getSysDao().getAppDeviceID(deviceInfo);
				App.Instance().getSysDao().saveLocalAppDeviceID(appDeviceId);
			}

			deviceInfo.setOwnerInfo(mInfo);
			App.Instance().getSysDao().save(appDeviceId, deviceInfo);

			return App.Instance().getSysDao().getOwnerInfo(appDeviceId);
		}
	}

	private class UpdateInfoGetTask extends CallbackAsync<UpdateInfo> {

		private Context mContext;

		public UpdateInfoGetTask(Context context, Callback<UpdateInfo> callback) {
			super(callback);
			mContext = context;
		}

		@Override
		protected UpdateInfo doInBackground() throws Exception {
			try {
				PackageInfo pi = mContext.getPackageManager().getPackageInfo(App.Instance().getPackageName(), 0);
				return App.Instance().getSysDao().getUpdateInfo(pi.packageName, pi.versionCode);
			} catch (PackageManager.NameNotFoundException e) {
				if (BuildConfig.DEBUG) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
			return null;
		}
	}

}
