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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("webClient")
public class ThisController {

    private static final String EXTERNAL_SERVICE_URL = "externalService/";
    private final WebClient webClient;

    @Autowired
    public ThisController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public ResponseEntity<Flux<Data>> getDataListFromExternalService() {
        log.info("Calling External API using WebClient to GET data list");

        Flux<Data> dataFlux = webClient
                .get()
                .uri(EXTERNAL_SERVICE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Data.class);

        return new ResponseEntity<>(dataFlux, HttpStatus.OK);
    }

    @GetMapping("responseEntity")
    public ResponseEntity<Data[]> getDataListFromExternalServiceAsResponseEntity() {
        log.info("Calling External API using WebClient to GET data list as Response Entity");

        ResponseEntity<Data[]> responseEntity = webClient
                .get()
                .uri(EXTERNAL_SERVICE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Data[].class)
                .block();

        assert responseEntity != null;
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
    }

    @GetMapping("exception")
    public ResponseEntity<Data[]> exceptionHandlingInWebClient() {
        log.info("Calling External API using WebClient and handling Exception");
        ResponseEntity<Data[]> responseEntity;

        try {
            responseEntity = webClient
                    .get()
                    .uri(EXTERNAL_SERVICE_URL + "exception")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Data[].class)
                    .block();
        } catch (WebClientResponseException ex) {
            // WebClientResponseException is thrown when the response status code of External API
            // is not 2xx.
            log.error("Failure response from External API");
            log.info("External API Response Status Code: {}", ex.getRawStatusCode());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        assert responseEntity != null;
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
    }

    @PostMapping
    public Mono<String> postDataToExternalService(@RequestBody Data data) {
        log.info("Calling External API using WebClient to POST data");

        return webClient
                .post()
                .uri(EXTERNAL_SERVICE_URL)
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .bodyToMono(String.class);
    }

    @PutMapping
    public ResponseEntity<Flux<Data>> updateData(@RequestBody Data data) {
        log.info("Calling External API using WebClient to UPDATE data");

        Flux<Data> dataFlux = webClient
                .put()
                .uri(EXTERNAL_SERVICE_URL)
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
                .uri(EXTERNAL_SERVICE_URL + id)
                .retrieve()
                .bodyToMono(String.class);
    }
}
