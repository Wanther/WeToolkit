package cn.wanther.toolkit.component;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;

public class ApiJSONParser implements Parser<JSONObject> {

	@Override
	public JSONObject parse(Request req, HttpResponse resp) throws IOException, AccessException {
		ApiErrorParser errorParser = new ApiErrorParser();
		errorParser.parse(req, resp);
		
		JSONObject data = errorParser.getCachedJSONObject();
		if(data == null){
			try {
				data = new JSONObject(cn.wanther.http.Utils.toString(resp.getEntity()));
			} catch (JSONException e) {
				throw new AccessException(e);
			}
		}
		
		return data;
	}

}
