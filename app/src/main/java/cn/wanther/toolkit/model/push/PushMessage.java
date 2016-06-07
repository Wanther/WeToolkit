package cn.wanther.toolkit.model.push;

import org.json.JSONObject;

public abstract class PushMessage {
	public static final String TAG = "PushMessage";

	// * 使用系统浏览器打开
	public static final int TYPE_BROWSER = 1;

	public static final String KEY_ID = "_msg_id";
	public static final String KEY_TYPE = "_msg_type";

	public static PushMessage create(String title, String body, JSONObject extra){

		PushMessage message;

		int id = extra == null ? 0 : extra.optInt(KEY_ID);
		int type = extra == null ? 0 : extra.optInt(KEY_TYPE);

		switch(type){
			case TYPE_BROWSER:
				message = new PushMessageBrowser(id, type, title, body, extra);
				break;
			default:
				message = new PushMessageDefault(id, type, title, body);
		}

		return message;
	}
	
	private int id;
	private int type;
	private String title;
	private String body;
	private JSONObject data;
	
	public PushMessage(int id, int type, String title, String body, JSONObject extra){
		setId(id);
		setTitle(title);
		setBody(body);
		setType(type);
		setData(extra);
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
