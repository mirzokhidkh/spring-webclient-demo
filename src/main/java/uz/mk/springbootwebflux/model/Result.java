package uz.mk.springbootwebflux.model;


public class Result extends ResultAsync {

    private Object responseBody;

    public Result() {
    }


    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }
}
