package com.fetchrewards.repository;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.model.ReceiptItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiptRepositoryUnitTest {

    private ReceiptRepository receiptRepository;
    private Receipt testReceipt;

    @BeforeEach
    void setUp() {
        receiptRepository = new ReceiptRepository();

        // Create a test receipt with a single item in the items list
        testReceipt = new Receipt();
        testReceipt.setId("test-id")  ;
        testReceipt.setPoints(100);
        testReceipt.setRetailer("Test Retailer");  ;
        testReceipt.setTotal(new BigDecimal("15.99"));
        testReceipt.setPurchaseDateTime(LocalDateTime.now());
        testReceipt.setItems(List.of(new ReceiptItem("Test Item", new BigDecimal("3.50"))));
    }

    @Test
    void testSaveReceipt() {
        // Act
        receiptRepository.saveReceipt(testReceipt);

        // Assert
        Optional<Receipt> retrievedReceipt = receiptRepository.getReceiptById("test-id");
        assertTrue(retrievedReceipt.isPresent(), "Receipt should be saved and retrieved successfully");
        assertEquals(testReceipt.toString(), retrievedReceipt.get().toString(), "Saved and retrieved receipt should match");
    }

    @Test
    void testGetReceiptById_ExistingId() {
        // Arrange
        receiptRepository.saveReceipt(testReceipt);

        // Act
        Optional<Receipt> retrievedReceipt = receiptRepository.getReceiptById("test-id");

        // Assert
        assertTrue(retrievedReceipt.isPresent(), "Receipt should be found with the given ID");
        assertEquals(testReceipt.toString(), retrievedReceipt.get().toString(), "Retrieved receipt should match the saved receipt");
    }

    @Test
    void testGetReceiptById_NonExistingId() {
        // Act
        Optional<Receipt> retrievedReceipt = receiptRepository.getReceiptById("non-existing-id");

        // Assert
        assertFalse(retrievedReceipt.isPresent(), "Receipt should not be found with a non-existing ID");
    }

    @Test
    void testDeleteReceiptById_ExistingId() {
        // Arrange
        receiptRepository.saveReceipt(testReceipt);

        // Act
        boolean isDeleted = receiptRepository.deleteReceiptById("test-id");

        // Assert
        assertTrue(isDeleted, "Delete operation should return true for an existing ID");
        assertFalse(receiptRepository.getReceiptById("test-id").isPresent(), "Receipt should be deleted");
    }

    @Test
    void testDeleteReceiptById_NonExistingId() {
        // Act
        boolean isDeleted = receiptRepository.deleteReceiptById("non-existing-id");

        // Assert
        assertFalse(isDeleted, "Delete operation should return false for a non-existing ID");
    }

    @Test
    void testClear() {
        // Arrange
        receiptRepository.saveReceipt(testReceipt);
        Receipt anotherReceipt = new Receipt();
        anotherReceipt.setId("another-id");
        anotherReceipt.setPoints(50);
        anotherReceipt.setRetailer("Another Retailer");
        anotherReceipt.setTotal(new BigDecimal("5.99"));
        anotherReceipt.setPurchaseDateTime(LocalDateTime.now());
        anotherReceipt.setItems( List.of(new ReceiptItem("Another Item", new BigDecimal("2.50"))));
        receiptRepository.saveReceipt(anotherReceipt);

        // Act
        receiptRepository.clear();

        // Assert
        assertFalse(receiptRepository.getReceiptById("test-id").isPresent(), "Repository should be empty after clear operation");
        assertFalse(receiptRepository.getReceiptById("another-id").isPresent(), "Repository should be empty after clear operation");
    }
}
