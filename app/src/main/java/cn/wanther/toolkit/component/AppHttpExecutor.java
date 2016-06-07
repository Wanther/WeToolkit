package cn.wanther.toolkit.component;

import java.net.HttpURLConnection;

import org.apache.http.protocol.HTTP;

import cn.wanther.http.Request;
import cn.wanther.http.executor.SimpleHttpExecutor;

public class AppHttpExecutor extends SimpleHttpExecutor {
	
	private String mPackageName;
	private int mVersionCode;
	
	public AppHttpExecutor(){}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String mPackageName) {
		this.mPackageName = mPackageName;
	}

	public int getVersionCode() {
		return mVersionCode;
	}

	public void setVersionCode(int versionCode) {
		mVersionCode = versionCode;
	}
	
	@Override
	protected void setCommonHeaders(HttpURLConnection conn, Request req) {
		final String userAgent = String.format("%s/%d", mPackageName, mVersionCode);
		conn.setRequestProperty(HTTP.USER_AGENT, userAgent);
	}
}
