package uz.mk.springwebclientdemo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uz.mk.springwebclientdemo.model.ReceiverRequest;
import uz.mk.springwebclientdemo.model.payload.ApiResponse;
import uz.mk.springwebclientdemo.model.payload.RequestBodyDTO;

import java.util.regex.Pattern;

@Service
public class ClientService {
    private final WebClient webClient;

    public ClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<?> receiver(ReceiverRequest receiverRequest) {
        String apiUrl = receiverRequest.getServiceUrl() + "/" + receiverRequest.getMethodName();

        Object requestBody = receiverRequest.getRequestBody();
        RequestBodyDTO requestBodyDTO = null;
        if (requestBody != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            requestBodyDTO =
                    objectMapper.convertValue(requestBody, new TypeReference<RequestBodyDTO>() {
                    });

//            if (requestBodyDTO.getId() != null) {
//                apiUrl += "/" + requestBodyDTO.getId();
//            }

        }

        Mono<?> objectMono = null;
        switch (receiverRequest.getHttpMethodType()) {
            case POST:
                assert requestBodyDTO != null;
                objectMono = webClient.post()
                        .uri(apiUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(requestBodyDTO), RequestBodyDTO.class)
                        .retrieve()
                        .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> Mono.just(new Exception("Something went wrong")))
                        .bodyToMono(ApiResponse.class);
                break;
            case GET:
                boolean isOneResource = Pattern.compile("\\d+").matcher(apiUrl.substring(apiUrl.lastIndexOf('/') + 1)).matches();

                if (isOneResource) {
                    objectMono = webClient.get()
                            .uri(apiUrl)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                            .bodyToMono(ApiResponse.class);

                } else {
                    objectMono = webClient.get()
                            .uri(apiUrl)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(Object[].class);

                }
                break;
            case PUT:
                assert requestBodyDTO != null;
                objectMono = webClient.put()
                        .uri(apiUrl)
                        .body(Mono.just(requestBodyDTO), RequestBodyDTO.class)
                        .retrieve()
                        .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                        .bodyToMono(ApiResponse.class);
                break;
            case DELETE:
                objectMono = webClient.delete()
                        .uri(apiUrl)
                        .retrieve()
                        .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                        .bodyToMono(ApiResponse.class);
                break;
            default:

        }

        return objectMono;
    }
}
