package com.fetchrewards.service;

import com.fetchrewards.domain.ItemRequest;
import com.fetchrewards.domain.ReceiptRequest;
import com.fetchrewards.model.Receipt;
import com.fetchrewards.repository.ReceiptRepository;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReceiptServiceIntegrationTest {

    @Autowired
    ReceiptService receiptService;

    @Autowired
    ReceiptRepository receiptRepository;

    @Autowired
    ReceiptPointRuleOptions defaultPointRuleOptions;

    static ReceiptRequest testRequest;

    @BeforeAll
    public static void setup() {
        testRequest = ReceiptRequest.builder()
                .retailer("M&M Corner Market")
                .purchaseDate(LocalDate.of(2022, 03, 20))
                .purchaseTime(LocalTime.of(14, 33))
                .items(
                        List.of(
                                new ItemRequest("Gatorade", new BigDecimal("2.25")),
                                new ItemRequest("Gatorade", new BigDecimal("2.25")),
                                new ItemRequest("Gatorade", new BigDecimal("2.25")),
                                new ItemRequest("Gatorade", new BigDecimal("2.25"))
                        )
                )
                .total(new BigDecimal("9.00"))
                .build();
    }

    @Nested
    class SaveAndRetrieve {

        static String testUuid;

        @Test
        public void processReceiptShouldSaveReceipt() {

            testUuid = receiptService.processReceipt(testRequest);
            assertNotNull(testUuid);
            Receipt savedReceipt = receiptRepository.getReceiptById(testUuid).orElse(null);
            assertNotNull(savedReceipt);
            assertEquals(savedReceipt.getId(), testUuid);
            assertEquals(savedReceipt.getRetailer(), testRequest.getRetailer());
            assertEquals(savedReceipt.getTotal().setScale(2), testRequest.getTotal());
            assertEquals(savedReceipt.getPurchaseDateTime().toLocalDate(), testRequest.getPurchaseDate());
            assertEquals(savedReceipt.getPurchaseDateTime().toLocalTime().truncatedTo(ChronoUnit.MINUTES), testRequest.getPurchaseTime());
            assertEquals(savedReceipt.getItems().size(), testRequest.getItems().size());
            assertEquals(savedReceipt.getPoints(), 109); // Points should be calculated correctly based on your rules
        }

        @Test
        public void getReceiptReturnsReceipt() {

            Receipt retrievedReceipt = receiptService.getReceipt(testUuid);


            assertNotNull(retrievedReceipt);
            assertEquals(retrievedReceipt.getPoints(), 109);  // Points verification (assuming rules apply here)
            assertEquals(retrievedReceipt.getId(), testUuid);
            assertEquals(retrievedReceipt.getRetailer(), testRequest.getRetailer());
            assertEquals(retrievedReceipt.getTotal().setScale(2), testRequest.getTotal());
            assertEquals(retrievedReceipt.getPurchaseDateTime().toLocalDate(), testRequest.getPurchaseDate());
            assertEquals(retrievedReceipt.getPurchaseDateTime().toLocalTime().truncatedTo(ChronoUnit.MINUTES), testRequest.getPurchaseTime());
            HashMap<String, BigDecimal> requestItemsMap = new HashMap<>();
            testRequest.getItems().forEach(item -> requestItemsMap.put(item.getShortDescription(), item.getPrice()));
            retrievedReceipt.getItems().forEach(savedItem -> {
                String savedDescription = savedItem.getShortDescription();
                assertTrue(requestItemsMap.containsKey(savedDescription));
                assertEquals(savedItem.getPrice(), requestItemsMap.get(savedDescription));
            });

            assertTrue(testRequest.getItems().size() >= 1);
            assertEquals(testRequest.getItems().size(), retrievedReceipt.getItems().size());
        }
    }

    @Test
    public void getReceiptShouldReturnNullWhenNotFound() {

        String notFoundUuid = "this_id_is_not_found";
        Receipt retrievedReceipt = receiptService.getReceipt(notFoundUuid);

        assertNull(retrievedReceipt);
    }
}
