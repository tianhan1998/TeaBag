package cn.th.teabag.exception;

public class NetErrorException extends Exception{
    public NetErrorException() {
        super();
    }

    public NetErrorException(String message) {
        super(message);
    }
}
