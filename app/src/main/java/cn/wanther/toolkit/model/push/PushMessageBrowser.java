package cn.wanther.toolkit.model.push;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;

import org.json.JSONObject;

public class PushMessageBrowser extends PushMessageNotification {

	public static final String KEY_URL = "url";
	
	public PushMessageBrowser(int id, int type, String title, String body, JSONObject extra){
		super(id, type, title, body, extra);
	}
	
	protected String getUrl(){
		JSONObject data = getData();
		if(data == null){
			return null;
		}
		return data.optString(KEY_URL);
	}

	protected Intent getIntent(Context context){
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
		intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		return intent;
	}

}
