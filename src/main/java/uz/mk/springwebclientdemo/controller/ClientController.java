package uz.mk.springwebclientdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uz.mk.springwebclientdemo.model.ReceiverRequest;
import uz.mk.springwebclientdemo.service.ClientService;

//@Slf4j
@RestController
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping(value = "/receive", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<?> receiver(@RequestBody ReceiverRequest receiverRequest){
        Mono<?> mono = clientService.receiver(receiverRequest);
        return mono;
    }

}
