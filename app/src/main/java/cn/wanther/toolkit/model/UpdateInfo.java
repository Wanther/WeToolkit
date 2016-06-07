package cn.wanther.toolkit.model;

import android.util.Log;

import cn.wanther.toolkit.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateInfo {

    private static final String TAG = "UpdateInfo";

    public static final String KEY = "_update_info";

    public static UpdateInfo create(JSONObject json) {
        if (json == null) {
            return null;
        }

        UpdateInfo info = new UpdateInfo();
        info.setVersionCode(json.optInt("ver"));
        info.setVersionName(json.optString("ver_name"));
        info.setDownloadUrl(json.optString("download_url"));
        info.setDescText(json.optString("desc_txt"));
        info.setForceUpdate(json.optString("force_update"));

        return info;
    }

    private int versionCode;
    private String versionName;
    private String descText;
    private String downloadUrl;
    private String forceUpdate;

    public UpdateInfo () {}

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDescText() {
        return descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isForceUpdate() {
        return "Y".equals(forceUpdate);
    }

    public void setForceUpdate(boolean forceUpdate) {
        setForceUpdate(forceUpdate ? "Y" : "N");
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("ver", getVersionCode());
            json.put("ver_name", getVersionName());
            json.put("download_url", getDownloadUrl());
            json.put("desc_txt", getDescText());
            json.put("force_update", getForceUpdate());
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return json.toString();
    }
}
