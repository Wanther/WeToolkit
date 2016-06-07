package cn.wanther.toolkit.component;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;
import cn.wanther.http.parser.ErrorParser;
import cn.wanther.toolkit.exception.ApiException;

public class ApiErrorParser implements Parser<Void> {
private JSONObject mJsonCache;
	
	public JSONObject getCachedJSONObject(){
		return mJsonCache;
	}

	@Override
	public Void parse(Request req, HttpResponse resp) throws IOException, AccessException {
		int statusCode = resp.getStatusLine().getStatusCode();
		
		if(statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES){
			// 成功的时候看看是不是{"error_code":xxx, "detail":"xxxxxx"}
			parseLogicError(req, resp);
			return null;
		}
		
		return new ErrorParser().parse(req, resp);
	}
	
	protected void parseLogicError(Request req, HttpResponse resp) throws IOException, AccessException{
		HttpEntity entity = resp.getEntity();
		if(entity != null){
			String contentMimeType = cn.wanther.http.Utils.getContentMimeType(entity);
	        
			if("application/json".equals(contentMimeType)){
				String respJson = cn.wanther.http.Utils.toString(entity);
				try {
					mJsonCache = new JSONObject(respJson);
					int errorCode = mJsonCache.optInt(ApiException.KEY_ERR_CODE);
					if(errorCode != 0){
						throw new ApiException(errorCode, mJsonCache.optString(ApiException.KEY_ERR_MSG));
					}
				} catch (JSONException e) {
	                
				}
			}
		}
	}
}
