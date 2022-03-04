package uz.mk.springbootwebflux.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uz.mk.springbootwebflux.model.ReceiverRequest;

@Service
public class ClientService {

    public Mono<?> receiver(ReceiverRequest receiverRequest) {


        return null;
    }
}
