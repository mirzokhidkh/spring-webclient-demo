package uz.mk.springwebclientdemo.exception;

public class CustomServerErrorException extends Exception  {
    public CustomServerErrorException(String message) {
        super(message);
    }
}