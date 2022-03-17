package uz.mk.springwebclientdemo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import uz.mk.springwebclientdemo.exception.CustomBadRequestException;
import uz.mk.springwebclientdemo.exception.CustomNotFoundException;
import uz.mk.springwebclientdemo.exception.CustomServerErrorException;
import uz.mk.springwebclientdemo.exception.response.ApiExceptionResponse;
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

            return Mono.just(response);
        });
    }

    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (nonNull(response.statusCode()) && response.statusCode().isError()) {
                HttpStatus status = response.statusCode();


                if (HttpStatus.NOT_FOUND.equals(status)) {
                    return response.bodyToMono(ApiExceptionResponse.class)
                            .flatMap(body  -> {
                                log.debug("Body is {}", body );
                                return Mono.error(new CustomNotFoundException(body .getMessage()));
                            });
                }

                else if (HttpStatus.BAD_REQUEST.equals(status)) {
                    return response.bodyToMono(ApiExceptionResponse.class)
                            .flatMap(body  -> {
                                log.debug("Body is {}", body );
                                return Mono.error(new CustomBadRequestException(body .getMessage()));
                            });
                }


                else if(HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
                    return response.bodyToMono(ApiExceptionResponse.class)
                            .flatMap(body  -> {
                                log.debug("Body is {}", body );
                                return Mono.error(new CustomServerErrorException("Something went wrong. Please wait a minute !"));
                            });
                }


                return response.bodyToMono  (ApiResponse.class)
                        .flatMap(body -> {
                            log.debug("Body is {}", body);
                            return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,"ERROR"));
                        });

            }
            return Mono.just(response);
        });
    }

    private static void logStatus(ClientResponse response) {
        HttpStatus status = response.statusCode();
        log.debug("Returned status code {} ({})", status.value(), status.getReasonPhrase());
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