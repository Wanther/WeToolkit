package cn.wanther.toolkit.dao;

import cn.wanther.http.exception.AccessException;
import cn.wanther.toolkit.exception.NetworkException;
import cn.wanther.toolkit.exception.StorageException;
import cn.wanther.toolkit.model.DeviceInfo;
import cn.wanther.toolkit.model.OwnerInfo;
import cn.wanther.toolkit.model.UpdateInfo;

public interface SysDao {
	String TAG = "SysDao";

	String KEY_URL_DEVICE_INFO_UPLOAD = "api.infoupload.url";
	String KEY_URL_DEIVCE_OWNER_INFO_GET = "api.ownerinfo.get.url";
	String KEY_URL_CHECK_UPDATE = "api.checkupdate.url";
	String KEY_URL_APP_DEVICE_ID = "api.appdeviceid.get.url";
	
	void save(String appDeviceId, DeviceInfo info) throws NetworkException, AccessException;
	OwnerInfo getOwnerInfo(String appDeviceId) throws NetworkException, AccessException;
	UpdateInfo getUpdateInfo(String packageName, int versionCode) throws NetworkException, AccessException;
	String getAppDeviceID(DeviceInfo info) throws NetworkException, AccessException;

	void saveLocal(OwnerInfo ownerInfo) throws StorageException;
	OwnerInfo getLocalOwnerInfo() throws StorageException;
	String getLocalAppDeviceID() throws StorageException;
	void saveLocalAppDeviceID(String deviceId) throws StorageException;
}
