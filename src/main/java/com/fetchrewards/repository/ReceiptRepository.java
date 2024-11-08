package com.fetchrewards.repository;

import com.fetchrewards.model.Receipt;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ReceiptRepository {

    private final ConcurrentHashMap<String, Receipt> receiptStorage = new ConcurrentHashMap<>();

    // Save a receipt in the repository
    public void saveReceipt(Receipt receipt) {
        receiptStorage.put(receipt.getId(), receipt);
    }

    // Retrieve a receipt by ID
    public Optional<Receipt> getReceiptById(String id) {
        return Optional.ofNullable(receiptStorage.get(id));
    }

    // Delete a receipt by ID
    public boolean deleteReceiptById(String id) {
        return receiptStorage.remove(id) != null;
    }

    // Clear all receipts (optional helper for testing)
    public void clear() {
        receiptStorage.clear();
    }
}
