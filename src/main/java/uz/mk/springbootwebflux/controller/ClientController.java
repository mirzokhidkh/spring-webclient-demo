package uz.mk.springbootwebflux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uz.mk.springbootwebflux.model.ReceiverRequest;
import uz.mk.springbootwebflux.service.ClientService;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/client")
public class ClientController {


    private  WebClient webClient;


    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

//    @PostConstruct
//    private void setUpWebClient(){
//        webClient = WebClient.create("https://jsonplaceholder.typicode.com/");
//    }

    @PostMapping(value = "/receive",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<?> receiver(@RequestBody ReceiverRequest receiverRequest) {
        return clientService.receiver(receiverRequest);

    }

}
