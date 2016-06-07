package cn.wanther.toolkit.dao.impl;

import android.text.TextUtils;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;
import cn.wanther.http.request.GetRequest;
import cn.wanther.http.request.SimplePostRequest;
import cn.wanther.toolkit.App;
import cn.wanther.toolkit.component.ApiJSONParser;
import cn.wanther.toolkit.dao.BasicDaoSupport;
import cn.wanther.toolkit.dao.LocalDataDao;
import cn.wanther.toolkit.dao.SysDao;
import cn.wanther.toolkit.exception.NetworkException;
import cn.wanther.toolkit.exception.StorageException;
import cn.wanther.toolkit.model.DeviceInfo;
import cn.wanther.toolkit.model.OwnerInfo;
import cn.wanther.toolkit.model.UpdateInfo;
import cn.wanther.toolkit.utils.URLUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SysDaoImpl extends BasicDaoSupport implements SysDao {
	
	@Override
	public void save(String appDeviceId, DeviceInfo info) throws NetworkException, AccessException {
		String url = App.Instance().getProperty(KEY_URL_DEVICE_INFO_UPLOAD);
		
		Map<String, String> params = new HashMap<String, String>();

		params.put("app_device_id", appDeviceId);

		if(info.getDeviceId() != null){
			params.put("device_id", info.getDeviceId());
		}

		if(info.getAndroidId() != null){
			params.put("android_id", info.getAndroidId());
		}

		if(info.getSerialNumber() != null){
			params.put("serial_num", info.getSerialNumber());
		}

		if(info.getMacAddress() != null){
			params.put("mac_address", info.getMacAddress());
		}

		if(info.getPhoneType() != null){
			params.put("phone_type", info.getPhoneType());
		}

		if(info.getOSVersion() != null){
			params.put("os_ver", info.getOSVersion());
		}

		params.put("sdk_ver", info.getSDKVersion() + "");
		params.put("screen_w", info.getScreenWidth() + "");
		params.put("screen_h", info.getScreeHeight() + "");
		params.put("density", info.getDensity() + "");

		if(info.getOwnerInfo() != null){
			params.put("owner", info.getOwnerInfo().getOwner() + "");
		}
		
		if(info.getOwnerInfo() != null && info.getOwnerInfo().getUserName() != null){
			params.put("user", info.getOwnerInfo().getUserName());
		}
		
		Request req = new SimplePostRequest(url, params);
		req.setHeader("Accept", "application/json");
		Parser<JSONObject> parser = new ApiJSONParser();
		
		try {
			App.Instance().getHttpExecutor().execute(req, parser);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	@Override
	public OwnerInfo getOwnerInfo(String appDeviceId) throws NetworkException, AccessException {
		String url = App.Instance().getProperty(KEY_URL_DEIVCE_OWNER_INFO_GET);
		url = url.replace("{app_device_id}", appDeviceId);
		
		Request req = new GetRequest(url);
		req.setHeader("Accept", "application/json");
		Parser<JSONObject> parser = new ApiJSONParser();
		
		try {
			JSONObject json = App.Instance().getHttpExecutor().execute(req, parser);
			
			if(json == null){
				return null;
			}
			
			return OwnerInfo.create(json);
			
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	@Override
	public UpdateInfo getUpdateInfo(String packageName, int versionCode) throws NetworkException, AccessException {
		String url = App.Instance().getProperty(KEY_URL_CHECK_UPDATE);
		url = URLUtil.appendParams(url, String.format("package=%s&ver=%d", packageName, versionCode));
		
		Request req = new GetRequest(url);
		req.setHeader("Accept", "application/json");
		Parser<JSONObject> parser = new ApiJSONParser();
		
		try {
			JSONObject json = App.Instance().getHttpExecutor().execute(req, parser);
			if (json == null || json.isNull("ver_name")) {
				return null;
			}
			return UpdateInfo.create(json);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	@Override
	public String getAppDeviceID(DeviceInfo info) throws NetworkException, AccessException {
		String url = App.Instance().getProperty(KEY_URL_APP_DEVICE_ID);

		Map<String, String> params = new HashMap<String, String>();
		if(info.getDeviceId() != null){
			params.put("device_id", info.getDeviceId());
		}

		if(info.getAndroidId() != null){
			params.put("android_id", info.getAndroidId());
		}

		if(info.getSerialNumber() != null){
			params.put("serial_num", info.getSerialNumber());
		}

		if(info.getMacAddress() != null){
			params.put("mac_address", info.getMacAddress());
		}

		if(info.getPhoneType() != null){
			params.put("phone_type", info.getPhoneType());
		}

		if(info.getOSVersion() != null){
			params.put("os_ver", info.getOSVersion());
		}

		params.put("sdk_ver", info.getSDKVersion() + "");
		params.put("screen_w", info.getScreenWidth() + "");
		params.put("screen_h", info.getScreeHeight() + "");
		params.put("density", info.getDensity() + "");

		Request req = new SimplePostRequest(url, params);
		req.setHeader("Accept", "application/json");
		Parser<JSONObject> parser = new ApiJSONParser();

		try {
			JSONObject json = App.Instance().getHttpExecutor().execute(req, parser);
			return json.optString("app_device_id");
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	@Override
	public void saveLocal(OwnerInfo ownerInfo) throws StorageException {
		if (ownerInfo == null) {
			App.Instance().getLocalDataDao().delete(LocalDataDao.CATE_SCOPE_APP, OwnerInfo.KEY);
		} else {
			App.Instance().getLocalDataDao().save(OwnerInfo.KEY, ownerInfo.toString());
		}
	}

	@Override
	public OwnerInfo getLocalOwnerInfo() throws StorageException {
		String ownerInfoJsonString = App.Instance().getLocalDataDao().get(OwnerInfo.KEY);
		if (TextUtils.isEmpty(ownerInfoJsonString)) {
			return null;
		}

		try {
			return OwnerInfo.create(new JSONObject(ownerInfoJsonString));
		} catch (JSONException e) {
			throw new StorageException(e);
		}

	}

	@Override
	public String getLocalAppDeviceID() throws StorageException {
		String json = App.Instance().getLocalDataDao().get(App.KEY_APP_DEVICE_ID);
		if (!TextUtils.isEmpty(json)) {
			try {
				return new JSONObject(json).optString("app_device_id");
			} catch (JSONException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public void saveLocalAppDeviceID(String id) throws StorageException {
		JSONObject json = new JSONObject();
		try {
			json.put("app_device_id", id);
			App.Instance().getLocalDataDao().save(App.KEY_APP_DEVICE_ID, json.toString());
		} catch (JSONException e) {
			throw new StorageException(e);
		}

	}
}
