package uz.mk.springwebclientdemo.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import uz.mk.springwebclientdemo.util.WebClientFilter;

//@Slf4j
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {
//    Logger log = LoggerFactory.getLogger(WebFluxConfig.class);

    @Bean
    public WebClient getWebClient() {

        //        HttpClient httpClient = HttpClient.create()
//                .tcpConfiguration(tcpClient -> tcpClient
//                        .proxy(proxy -> proxy
//                                .type(ProxyProvider.Proxy.HTTP)
//                                .host("10.50.71.253")
//                                .port(3128)));


        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(client ->
                        client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                .doOnConnected(conn -> conn
                                        .addHandlerLast(new ReadTimeoutHandler(10))
                                        .addHandlerLast(new WriteTimeoutHandler(10))));


        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient
//                .wiretap(true
//                        "reactor.netty.http.client.HttpClient",
//                LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL
//                )
        );


        return WebClient.builder()
//                .baseUrl("http://localhost:8181/api")
                .clientConnector(connector)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(exchangeFilterFunctions -> {
//                    exchangeFilterFunctions.add(WebClientFilter.logRequest());
//                    exchangeFilterFunctions.add(WebClientFilter.logResponse());
                    exchangeFilterFunctions.add(WebClientFilter.errorHandler());
                })
                .build();
    }

//    @Override
//    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
//        configurer.defaultCodecs().enableLoggingRequestDetails(true);
//    }


}
