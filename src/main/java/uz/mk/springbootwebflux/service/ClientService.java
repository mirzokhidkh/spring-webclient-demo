package uz.mk.springbootwebflux.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import uz.mk.springbootwebflux.model.ReceiverRequest;
import uz.mk.springbootwebflux.model.payload.RequestBodyDTO;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private WebClient webClient;

    public ResponseEntity<?> receiver(ReceiverRequest receiverRequest) {
        ResponseEntity<?> responseEntity = null;

//        HttpClient httpClient = HttpClient.create()
//                .tcpConfiguration(tcpClient -> tcpClient
//                        .proxy(proxy -> proxy
//                                .type(ProxyProvider.Proxy.HTTP)
//                                .host("10.50.71.253")
//                                .port(3128)));

//        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector();

        webClient = WebClient.builder()
                .clientConnector(connector)
                .baseUrl(receiverRequest.getServiceUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();


        String apiUrl;
        apiUrl = "/" + receiverRequest.getMethodName();

        Object requestBody = receiverRequest.getRequestBody();
        if (requestBody != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            RequestBodyDTO requestBodyDTO =
                    objectMapper.convertValue(requestBody, new TypeReference<RequestBodyDTO>() {
                    });

            if (requestBodyDTO.getId() != null) {
                apiUrl += "/" + requestBodyDTO.getId();
            }

        }


        switch (receiverRequest.getHttpMethodType()) {
            case POST:
                webClient
                        .post()
                        .uri(apiUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(Object[].class).log();

                break;
            case GET:
                boolean isOneResource = Pattern.compile("\\d+").matcher(apiUrl.substring(apiUrl.lastIndexOf('/') + 1)).matches();
                if (isOneResource) {
                    Mono<Object> response = webClient
                            .get()
                            .uri(apiUrl)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(Object.class).log();

                    Object object = response.share().block();

                    responseEntity = ResponseEntity.ok().body(object);
                }else {
                    Mono<Object[]> response = webClient
                            .get()
                            .uri(apiUrl)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(Object[].class).log();

                    Object[] objects = response.share().block();

//                    List<Object> collect = Arrays.stream(objects).collect(Collectors.toList());
                    responseEntity = ResponseEntity.ok().body(objects);
                }

                break;
            case PUT:


                break;
            default:

        }
        return responseEntity;
    }
}
