package uz.mk.springwebclientdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.mk.springwebclientdemo.model.enums.HttpMethodType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiverRequest {
    private String serviceUrl;
    private String methodName;
    private HttpMethodType httpMethodType;
    private Object requestBody;
}
