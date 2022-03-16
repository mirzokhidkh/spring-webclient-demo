package uz.mk.springwebclientdemo.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import uz.mk.springwebclientdemo.exception.CustomBadRequestException;
import uz.mk.springwebclientdemo.exception.CustomServerErrorException;
import uz.mk.springwebclientdemo.exception.ServiceException;
import uz.mk.springwebclientdemo.model.ResultAsync;
import uz.mk.springwebclientdemo.model.payload.ApiResponse;

import static java.util.Objects.nonNull;

//@Slf4j
public class WebClientFilter {

    private static final Logger log = LoggerFactory.getLogger(WebClientFilter.class);


    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            logMethodAndUrl(request);
            logHeaders(request);

            return Mono.just(request);
        });
    }


    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            logStatus(response);
            logHeaders(response);

            return logBody(response);
        });
    }

    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (nonNull(response.statusCode()) && response.statusCode().isError()) {
                HttpStatus status = response.statusCode();
                if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
                    return response.bodyToMono(ApiResponse.class)
                            .flatMap(body -> Mono.error(new CustomServerErrorException(body.getMessage())));
                }
//                if (HttpStatus.BAD_REQUEST.equals(status)) {
//                    return response.bodyToMono(ApiResponse.class)
//                            .flatMap(apiResponse -> Mono.error(new CustomBadRequestException(apiResponse.getMessage())))
////                            .flatMap(apiResponse -> Mono.error(new ServiceException(apiResponse.getMessage(), response.statusCode().value())))
//                            ;
//                }

                if (HttpStatus.BAD_REQUEST.equals(status)) {
                    return response.bodyToMono(ApiResponse.class)
                            .flatMap(apiResponse -> Mono.error(new CustomBadRequestException(apiResponse.getMessage())));
                }

            }
            return Mono.just(response);
        });
    }

    private static void logStatus(ClientResponse response) {
        HttpStatus status = response.statusCode();
        log.debug("Returned status code {} ({})", status.value(), status.getReasonPhrase());
    }


    private static Mono<ClientResponse> logBody(ClientResponse response) {
        if (response.statusCode() != null && (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError())) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> {
                        log.debug("Body is {}", body);
                        return Mono.just(response);
                    });
        } else {
            return Mono.just(response);
        }
    }


    private static void logHeaders(ClientResponse response) {
        response.headers().asHttpHeaders().forEach((name, values) -> {
            values.forEach(value -> {
                logNameAndValuePair(name, value);
            });
        });
    }


    private static void logHeaders(ClientRequest request) {
        request.headers().forEach((name, values) -> {
            values.forEach(value -> {
                logNameAndValuePair(name, value);
            });
        });
    }


    private static void logNameAndValuePair(String name, String value) {
        log.debug("{}={}", name, value);
    }


    private static void logMethodAndUrl(ClientRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.method().name());
        sb.append(" to ");
        sb.append(request.url());

        log.debug(sb.toString());
    }


}