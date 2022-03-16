package uz.mk.springwebclientdemo.exception;

public class CustomBadRequestException extends Exception  {
    public CustomBadRequestException(String message) {
        super(message);
    }
}