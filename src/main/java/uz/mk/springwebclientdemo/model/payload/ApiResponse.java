package uz.mk.springwebclientdemo.model.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse {
    private String message;
    private boolean status;
    @JsonProperty("object")
    private Object object;
    private List<?> dataList;
    private Map<String, Object> meta = new HashMap<>();


    public ApiResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }

    public ApiResponse(String message, boolean status, Object object) {
        this.message = message;
        this.status = status;
        this.object = object;
    }

    public ApiResponse(String message, boolean status, List<Object> dataList) {
        this.message = message;
        this.status = status;
        this.dataList = dataList;
    }
}
