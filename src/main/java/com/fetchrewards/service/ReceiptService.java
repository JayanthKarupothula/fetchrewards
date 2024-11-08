package com.fetchrewards.service;

import com.fetchrewards.domain.ReceiptRequest;
import com.fetchrewards.model.Receipt;
import com.fetchrewards.model.ReceiptItem;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommandFactory;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;
import com.fetchrewards.repository.ReceiptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptPointRuleOptions defaultReceiptPointRuleOptions ;
    private final ReceiptPointRuleCommandFactory pointRuleFactory;


    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository, ReceiptPointRuleOptions defaultReceiptPointRuleOptions, ReceiptPointRuleCommandFactory pointRuleFactory) {
        this.receiptRepository = receiptRepository;
        this.defaultReceiptPointRuleOptions = defaultReceiptPointRuleOptions;
        this.pointRuleFactory = pointRuleFactory;
    }

    public String processReceipt(ReceiptRequest receiptRequest) {
        Receipt receipt = convertReceiptRequestToModel(receiptRequest);
        applyPoints(receipt, defaultReceiptPointRuleOptions);
        receiptRepository.saveReceipt(receipt);
        return receipt.getId();
    }

    public Receipt getReceipt(String id) {
        return receiptRepository.getReceiptById(id).orElse(null);
    }

    public void applyPoints(Receipt receipt, ReceiptPointRuleOptions options) {
        pointRuleFactory.getAllCommands(receipt, options).forEach(
                rule -> rule.applyRule()
        );

        log.info("Points have been applied to receipt: " + receipt);
    }

    public Receipt convertReceiptRequestToModel(ReceiptRequest processRequest) {
        List<ReceiptItem> requestItems = new ArrayList<>();
        processRequest.getItems().forEach((requestItem) -> {
            requestItems.add(new ReceiptItem(requestItem.getShortDescription(), requestItem.getPrice()));
        });

        Receipt requestReceipt = Receipt.builder()
                .id(UUID.randomUUID().toString())
                .points(0)
                .retailer(processRequest.getRetailer())
                .purchaseDateTime(LocalDateTime.of(
                        processRequest.getPurchaseDate(), processRequest.getPurchaseTime().truncatedTo(ChronoUnit.MINUTES))
                )
                .items(requestItems)
                .total(processRequest.getTotal())
                .build();

        return requestReceipt;
    }
}
