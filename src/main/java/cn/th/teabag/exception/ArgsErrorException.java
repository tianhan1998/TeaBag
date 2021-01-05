package cn.th.teabag.exception;

public class ArgsErrorException extends Exception{
    public ArgsErrorException() {
        super();
    }

    public ArgsErrorException(String message) {
        super(message);
    }
}
