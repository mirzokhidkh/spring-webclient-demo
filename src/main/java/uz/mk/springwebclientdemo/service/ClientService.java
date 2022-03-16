package uz.mk.springwebclientdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import uz.mk.springwebclientdemo.exception.CustomBadRequestException;
import uz.mk.springwebclientdemo.model.ReceiverRequest;
import uz.mk.springwebclientdemo.model.ResultAsync;
import uz.mk.springwebclientdemo.model.payload.ApiResponse;
import uz.mk.springwebclientdemo.model.payload.RequestBodyDTO;

import java.util.regex.Pattern;

@Slf4j
@Service
public class ClientService {
    private final WebClient webClient;
    private final ObjectMapper mapper;

    public ClientService(WebClient webClient, ObjectMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    @SneakyThrows
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
//                String receiverRequestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(receiverRequest);
//                log.info("Request: " + receiverRequestJson);
                assert requestBodyDTO != null;
                objectMono = webClient.post()
                        .uri(apiUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(requestBodyDTO), RequestBodyDTO.class)
                        .retrieve()
//                        .onStatus(httpStatus -> httpStatus.value() == HttpStatus.BAD_REQUEST.value(),
//                                clientResponse -> clientResponse
//                                        .bodyToMono(ApiResponse.class)
//                                        .flatMap(apiResponse -> Mono.error(new ResponseStatusException(
//                                                HttpStatus.BAD_REQUEST,
//                                                apiResponse.getMessage()
//                                        )))
////                                        .map(apiResponse -> new CustomBadRequestException(apiResponse.getMessage()
////                                        ))
//                        )
                        .bodyToMono(ApiResponse.class)
                        .doOnNext(apiResponse -> {
                            try {
                                log.info("Response: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiResponse));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        })
                ;


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
