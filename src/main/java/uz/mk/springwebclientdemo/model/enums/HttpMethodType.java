package uz.mk.springwebclientdemo.model.enums;

public enum HttpMethodType {
    POST("POST"), GET("GET"), PUT("PUT"), DELETE("DELETE");

    private final String name;

    HttpMethodType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
