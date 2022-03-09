package uz.mk.springbootwebflux.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uz.mk.springbootwebflux.model.payload.RequestBodyDTO;

import java.util.regex.Pattern;

@Service
public class ClientService {

    private WebClient webClient;

    public ResponseEntity<?> receiver(ReceiverRequest receiverRequest) {
        ResponseEntity<?> responseEntity = null;

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

        Mono<Object> response;
        switch (receiverRequest.getHttpMethodType()) {
            case POST:
                assert requestBodyDTO != null;
                response = webClient
                        .post()
                        .uri(apiUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(requestBodyDTO), RequestBodyDTO.class)
                        .retrieve()
                        .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> Mono.empty())
                        .bodyToMono(Object.class);

                Object obj = response.block();
                responseEntity = ResponseEntity.ok().body(obj);

                break;
            case GET:
                boolean isOneResource = Pattern.compile("\\d+").matcher(apiUrl.substring(apiUrl.lastIndexOf('/') + 1)).matches();
                if (isOneResource) {
                    response = webClient
                            .get()
                            .uri(apiUrl)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                            .bodyToMono(Object.class).log();

                    Object object = response.block();

                    responseEntity = ResponseEntity.ok().body(object);
                }else {
                    Mono<Object[]> responseArr = webClient
                            .get()
                            .uri(apiUrl)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(Object[].class).log();

                    Object[] objects = responseArr.share().block();

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
