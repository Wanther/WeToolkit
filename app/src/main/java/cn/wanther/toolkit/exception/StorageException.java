package cn.wanther.toolkit.exception;

public class StorageException extends HardwareException {
    private static final long serialVersionUID = 1L;

    public StorageException(){}

    public StorageException(String message){
        super(message);
    }

    public StorageException(String message, Throwable cause){
        super(message, cause);
    }

    public StorageException(Throwable cause){
        super(cause);
    }
}
