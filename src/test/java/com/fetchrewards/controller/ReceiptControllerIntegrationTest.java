package com.fetchrewards.controller;

import com.fetchrewards.domain.ReceiptPointsResponse;
import com.fetchrewards.domain.ReceiptRequest;
import com.fetchrewards.domain.ReceiptResponse;
import com.fetchrewards.service.ReceiptService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReceiptControllerIntegrationTest {

    @SpyBean
    private ReceiptService receiptService;

    @Autowired
    private WebTestClient webTestClient;

    static String receiptId;

    @Test
    @Order(1)
    void processReceipt() {
        String requestJson = "{" +
                "    \"retailer\": \"M&M Corner Market\"," +
                "    \"purchaseDate\": \"2022-03-20\"," +
                "    \"purchaseTime\": \"14:33\"," +
                "    \"items\": [" +
                "        {\"shortDescription\": \"Gatorade\", \"price\": \"2.25\"}," +
                "        {\"shortDescription\": \"Gatorade\", \"price\": \"2.25\"}," +
                "        {\"shortDescription\": \"Gatorade\", \"price\": \"2.25\"}," +
                "        {\"shortDescription\": \"Gatorade\", \"price\": \"2.25\"}" +
                "    ]," +
                "\"total\": \"9.00\" }";

        // Perform POST request to process receipt
        ReceiptResponse response = this.webTestClient
                .post()
                .uri("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ReceiptResponse.class)
                .returnResult().getResponseBody();

        // Verify ID is generated and points are calculated
        assertNotNull(response.getId());
        receiptId = response.getId();
        verify(receiptService, times(1)).processReceipt(any(ReceiptRequest.class));
    }

    @Test
    @Order(2)
    void retrievePoints() {
        // Perform GET request to retrieve points for the processed receipt
        ReceiptPointsResponse response = this.webTestClient
                .get()
                .uri("/receipts/" + receiptId + "/points")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReceiptPointsResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(response);
        assertTrue(response.getPoints() > 0);// assuming points were calculated
        assertEquals(109, response.getPoints());
        System.out.println("Points in integration test: " + response.getPoints());
        verify(receiptService, times(1)).getReceipt(receiptId);
    }

    @Test
    @Order(3)
    void retrievePointsNotFound() {
        // Perform GET request for a nonexistent ID to confirm 404 response
        this.webTestClient
                .get()
                .uri("/receipts/nonexistent_id/points")
                .exchange()
                .expectStatus().isNotFound();

        verify(receiptService, times(1)).getReceipt("nonexistent_id");
    }
}

