package uz.mk.springbootwebflux.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import uz.mk.springbootwebflux.model.ReceiverRequest;
import uz.mk.springbootwebflux.model.payload.ApiResponse;
import uz.mk.springbootwebflux.model.payload.RequestBodyDTO;

import java.util.regex.Pattern;

@Service
public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private WebClient webClient;

    public Mono<?> receiver(ReceiverRequest receiverRequest) {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient -> tcpClient
                        .proxy(proxy -> proxy
                                .type(ProxyProvider.Proxy.HTTP)
                                .host("10.50.71.253")
                                .port(3128)));

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        webClient = WebClient.builder()
//                .clientConnector(connector)
                .baseUrl(receiverRequest.getServiceUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();


        String apiUrl;
        apiUrl = "/" + receiverRequest.getMethodName();

        Object requestBody = receiverRequest.getRequestBody();
        RequestBodyDTO requestBodyDTO = null;
        if (requestBody != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            requestBodyDTO =
                    objectMapper.convertValue(requestBody, new TypeReference<RequestBodyDTO>() {
                    });

            if (requestBodyDTO.getId() != null) {
                apiUrl += "/" + requestBodyDTO.getId();
            }

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
                        .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> Mono.empty())
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
