package cn.wanther.toolkit.component;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;

public class ApiJSONArrayParser implements Parser<JSONArray> {

	@Override
	public JSONArray parse(Request req, HttpResponse resp) throws IOException, AccessException {
		String result = cn.wanther.http.Utils.toString(resp.getEntity());
		try {
			return new JSONArray(result);
		} catch (JSONException e) {
			throw new AccessException(e);
		}
	}

	

}
