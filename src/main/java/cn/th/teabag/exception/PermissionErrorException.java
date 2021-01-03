package cn.th.teabag.exception;

public class PermissionErrorException extends Exception{
    public PermissionErrorException() {
        super();
    }

    public PermissionErrorException(String message) {
        super(message);
    }
}
