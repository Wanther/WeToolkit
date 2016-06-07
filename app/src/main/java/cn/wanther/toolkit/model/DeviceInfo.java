package cn.wanther.toolkit.model;

import cn.wanther.toolkit.utils.Compat;

import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceInfo {
	
	public static DeviceInfo create(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

		DeviceInfo info = new DeviceInfo();
		info.setDeviceId(tm.getDeviceId());

		//info.setSerialNumber(android.os.Build.SERIAL);
		
		info.setAndroidId(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));

		Point size = Compat.getScreenSize(context);
		info.setScreenWidth(size.x);
		info.setScreeHeight(size.y);
		
		info.setDensity(context.getResources().getDisplayMetrics().density);
		
		info.setSDKVersion(Compat.getOSSDKVersion());
		info.setPhoneType(android.os.Build.MODEL);
		info.setOSVersion(android.os.Build.VERSION.RELEASE);
		
		WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wi = wm.getConnectionInfo();
		info.setMacAddress(wi.getMacAddress());
		
		return info;
	}

	private String deviceId;
	private String androidId;
	private String serialNumber;
	private String macAddress;
	private String phoneType;
	private String osVersion;
	private int sdkVersion;
	private float density;
	private int screenWidth;
	private int screeHeight;
	private OwnerInfo ownerInfo;
	
	public DeviceInfo(){}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getAndroidId() {
		return androidId;
	}

	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getOSVersion() {
		return osVersion;
	}

	public void setOSVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public int getSDKVersion() {
		return sdkVersion;
	}

	public void setSDKVersion(int sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreeHeight() {
		return screeHeight;
	}

	public void setScreeHeight(int screeHeight) {
		this.screeHeight = screeHeight;
	}

	public OwnerInfo getOwnerInfo() {
		return ownerInfo;
	}

	public void setOwnerInfo(OwnerInfo ownerInfo) {
		this.ownerInfo = ownerInfo;
	}

}
