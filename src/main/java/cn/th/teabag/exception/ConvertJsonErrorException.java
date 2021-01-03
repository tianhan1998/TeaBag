package cn.th.teabag.exception;

public class ConvertJsonErrorException extends Exception{
    public ConvertJsonErrorException() {
        super();
    }

    public ConvertJsonErrorException(String message) {
        super(message);
    }
}
