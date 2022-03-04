package uz.mk.springbootwebflux.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uz.mk.springbootwebflux.model.ReceiverRequest;

@Service
public class ClientService {

    public Mono<?> receiver(ReceiverRequest receiverRequest) {


        return null;
    }
}
