package cn.wanther.toolkit.exception;

public class HardwareException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public HardwareException(){}
	
	public HardwareException(String message){
		super(message);
	}
	
	public HardwareException(String message, Throwable cause){
		super(message, cause);
	}
	
	public HardwareException(Throwable cause){
		super(cause);
	}
}
