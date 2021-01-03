package cn.th.teabag.exception;

public class ConvertArgsErrorException extends Exception{
    public ConvertArgsErrorException() {
        super();
    }

    public ConvertArgsErrorException(String message) {
        super(message);
    }
}
