package com.webclient.thisService;

import com.webclient.dto.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("webClient")
public class ThisController {

    private WebClient webClient;

    @Autowired
    public ThisController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public ResponseEntity<Flux<Data>> getDataListFromExternalService() {
        log.info("Calling External API using WebClient to GET data list");

        Flux<Data> dataFlux = webClient
                .get()
                .uri("http://localhost:8080/externalService")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Data.class);

        return new ResponseEntity<>(dataFlux, HttpStatus.OK);
    }

    @PostMapping
    public Mono<String> postDataToExternalService(@RequestBody Data data) {
        log.info("Calling External API using WebClient to POST data");

        return webClient
                .post()
                .uri("http://localhost:8080/externalService")
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .bodyToMono(String.class);
    }

    @PutMapping
    public ResponseEntity<Flux<Data>> updateData(@RequestBody Data data) {
        log.info("Calling External API using WebClient to UPDATE data");

        Flux<Data> dataFlux = webClient
                .put()
                .uri("http://localhost:8080/externalService")
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .bodyToFlux(Data.class);

        return new ResponseEntity<>(dataFlux, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public Mono<String> deleteData(@PathVariable String id) {
        log.info("Calling External API using WebClient to DELETE data");

        return webClient
                .delete()
                .uri("http://localhost:8080/externalService/" + id)
                .retrieve()
                .bodyToMono(String.class);
    }
}
