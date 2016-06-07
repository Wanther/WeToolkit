package cn.wanther.toolkit.exception;

import cn.wanther.http.exception.AccessException;


public class ApiException extends AccessException {

	private static final long serialVersionUID = 1L;
	
	public static final String KEY_ERR_CODE = "err_code";
	public static final String KEY_ERR_MSG = "err_msg";
	
	private int errorCode;
	
	public ApiException(int errorCode){
		this.errorCode = errorCode;
	}
	
	public ApiException(int errorCode, String message){
		super(message);
		this.errorCode = errorCode;
	}
	
	public ApiException(int errorCode, Throwable cause){
		super(cause);
		this.errorCode = errorCode;
	}
	
	public ApiException(int errorCode, String message, Throwable cause){
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	public int getErrorCode(){
		return errorCode;
	}

	@Override
	public String getLocalizedMessage() {
		return String.format("[%d]%s", errorCode, getMessage());
	}

}
