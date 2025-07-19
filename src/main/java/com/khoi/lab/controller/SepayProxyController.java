package com.khoi.lab.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

/**
 * Simple sepay proxy controller
 */
@RestController
@RequestMapping("/api/sepay")
public class SepayProxyController {
    @GetMapping("/transactions")
    public ResponseEntity<String> getTransactions(@RequestParam String accountNumber, @RequestParam String token) {
        String url = "https://my.sepay.vn/userapi/transactions/list?account_number=" + accountNumber + "&limit=3";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return ResponseEntity.ok(response.getBody());
    }
}