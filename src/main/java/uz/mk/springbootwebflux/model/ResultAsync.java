package uz.mk.springbootwebflux.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"responseBody", "oraMsg"})
public class ResultAsync {
    private int code;
    private String msg;

    public ResultAsync() {
    }


    public ResultAsync(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
