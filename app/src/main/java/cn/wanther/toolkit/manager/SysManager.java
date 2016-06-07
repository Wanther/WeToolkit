package cn.wanther.toolkit.manager;

import android.content.Context;

import cn.wanther.toolkit.model.OwnerInfo;
import cn.wanther.toolkit.model.UpdateInfo;

import java.util.concurrent.Future;

public interface SysManager {
	String TAG = "SysManager";
	/**
	 * 收集并上传设备信息
	 * @param context
	 * @param callback
	 * @return
	 */
	Future<?> getDeviceOwnerInfo(Context context, LocalDataCallback<OwnerInfo> callback);
	Future<?> updateOwnerInfo(Context context, OwnerInfo info, LocalDataCallback<OwnerInfo> callback);
	Future<?> getUpdateInfo(Context context, Callback<UpdateInfo> callback);
}
