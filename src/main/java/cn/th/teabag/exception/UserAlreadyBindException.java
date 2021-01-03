package cn.th.teabag.exception;

public class UserAlreadyBindException extends Exception{
    public UserAlreadyBindException() {
        super();
    }

    public UserAlreadyBindException(String message) {
        super(message);
    }
}
