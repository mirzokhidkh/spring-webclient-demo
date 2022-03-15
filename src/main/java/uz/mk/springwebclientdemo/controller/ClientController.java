package uz.mk.springwebclientdemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uz.mk.springwebclientdemo.model.ReceiverRequest;
import uz.mk.springwebclientdemo.service.ClientService;

@Slf4j
@RestController
public class ClientController {
    private final ClientService clientService;
    private final ObjectMapper mapper;


    @Autowired
    public ClientController(ClientService clientService, ObjectMapper mapper) {
        this.clientService = clientService;
        this.mapper = mapper;
    }

    @PostMapping(value = "/receive", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<?> receiver(@RequestBody ReceiverRequest receiverRequest) throws JsonProcessingException {

        String receiverRequestJson = mapper.writeValueAsString(receiverRequest);
        log.info("Request-Body-JSON: " + receiverRequestJson);
        Mono<?> mono = clientService.receiver(receiverRequest);
        return mono;
    }

}
