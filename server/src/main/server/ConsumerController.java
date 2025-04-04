package com.knowMoreQR.server;

import java.util.Arrays;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class ConsumerController {
    @Value("${ASTRA_DB_ID}")
    private String ASTRA_DB_ID;
    @Value("${ASTRA_DB_REGION}")
    private String ASTRA_DB_REGION;
    @Value("${ASTRA_DB_KEYSPACE}")
    private String ASTRA_DB_KEYSPACE;
    @Value("${ASTRA_DB_APPLICATION_TOKEN}")
    private String ASTRA_DB_APPLICATION_TOKEN;

    // collection-id is consumer (for astra)
    @GetMapping("/consumers")
    public ResponseEntity<String> all() {
        // Create uri with env variables.
        String uri = "https://" + ASTRA_DB_ID + "-" + ASTRA_DB_REGION +
                ".apps.astra.datastax.com/api/rest/v2/namespaces/" +
                ASTRA_DB_KEYSPACE + "/collections/consumer?page-size=20";

        // Call The DataStax Astra API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("X-Cassandra-Token", ASTRA_DB_APPLICATION_TOKEN);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return res;
    }

    @GetMapping("/consumers/{id}")
    public ResponseEntity<String> tags(@PathVariable("id") String id) {
        // Create uri with env variables.
        String uri = "https://" + ASTRA_DB_ID + "-" + ASTRA_DB_REGION +
                ".apps.astra.datastax.com/api/rest/v2/namespaces/" +
                ASTRA_DB_KEYSPACE + "/collections/consumer/" + id;

        // Call The DataStax Astra API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("X-Cassandra-Token", ASTRA_DB_APPLICATION_TOKEN);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return res;
    }

    @PostMapping("/consumers")
    public ResponseEntity<String> create(@RequestBody Consumer newConsumer) {
        String uri = "https://" + ASTRA_DB_ID + "-" + ASTRA_DB_REGION +
                ".apps.astra.datastax.com/api/rest/v2/namespaces/" +
                ASTRA_DB_KEYSPACE + "/collections/consumer";

        String newConsumerJsonInString = new Gson().toJson(newConsumer);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Cassandra-Token", ASTRA_DB_APPLICATION_TOKEN);
        HttpEntity<String> entity = new HttpEntity<String>(newConsumerJsonInString, headers);
        ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        //returns id of newConsumer
        return res;
    }

    @PutMapping("/consumers/{id}")
    public ResponseEntity<String> update(@PathVariable("id") String id, @RequestBody Consumer updatedConsumer) {

        String uri = "https://" + ASTRA_DB_ID + "-" + ASTRA_DB_REGION +
                ".apps.astra.datastax.com/api/rest/v2/namespaces/" +
                ASTRA_DB_KEYSPACE + "/collections/consumer/" + id;

        String newConsumerJsonInString = new Gson().toJson(updatedConsumer);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Cassandra-Token", ASTRA_DB_APPLICATION_TOKEN);
        HttpEntity<String> entity = new HttpEntity<String>(newConsumerJsonInString, headers);
        ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

        //returns id of updatedConsumer (same id)
        return res;
    }

    @DeleteMapping("/consumers/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {

        String uri = "https://" + ASTRA_DB_ID + "-" + ASTRA_DB_REGION +
                ".apps.astra.datastax.com/api/rest/v2/namespaces/" +
                ASTRA_DB_KEYSPACE + "/collections/consumer/" + id;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Cassandra-Token", ASTRA_DB_APPLICATION_TOKEN);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

        //returns nothing
        return res;
    }
}