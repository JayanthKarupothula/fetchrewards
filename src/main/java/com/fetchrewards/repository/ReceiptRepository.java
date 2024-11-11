package com.fetchrewards.repository;

import com.fetchrewards.model.Receipt;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ReceiptRepository {

    private final ConcurrentHashMap<String, Receipt> receiptStorage = new ConcurrentHashMap<>();


    public void saveReceipt(Receipt receipt) {
        receiptStorage.put(receipt.getId(), receipt);
    }

    public Optional<Receipt> getReceiptById(String id) {
        return Optional.ofNullable(receiptStorage.get(id));
    }


    public boolean deleteReceiptById(String id) {
        return receiptStorage.remove(id) != null;
    }

    public void clear() {
        receiptStorage.clear();
    }
}
