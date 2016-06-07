package cn.wanther.toolkit.model;

import cn.wanther.toolkit.App;
import cn.wanther.toolkit.R;
import cn.wanther.toolkit.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class OwnerInfo {

	public static final String KEY = "_owner_info";

	public static final String KEY_OWNER = "owner";
	public static final String KEY_USER_NAME = "user";

	public static final int OWNER_TYPE_COMPANY = 1;
	public static final int OWNER_TYPE_PERSONAL = 2;
	
	public static OwnerInfo create(JSONObject json){
		if (json == null) {
			return null;
		}

		OwnerInfo info = new OwnerInfo();
		info.setOwner(json.optInt(KEY_OWNER));
		info.setUserName(Utils.optJSONString(json, KEY_USER_NAME));
		
		return info;
	}
	
	private int owner;
	private String userName;
	
	public OwnerInfo() {}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public String getOwnerName() {
		if(owner <= 0){
			return "请选择";
		}
		
		if(owner > 2){
			return owner + "";
		}
		
		String[] array = App.Instance().getResources().getStringArray(R.array.owner_type);
		return array[owner - 1];
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		try {
			json.put(KEY_USER_NAME, userName);
			json.put(KEY_OWNER, owner);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return json.toString();
	}
}
