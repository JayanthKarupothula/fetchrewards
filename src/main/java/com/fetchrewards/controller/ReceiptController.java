package com.fetchrewards.controller;

import com.fetchrewards.domain.ReceiptPointsResponse;
import com.fetchrewards.domain.ReceiptRequest;
import com.fetchrewards.domain.ReceiptResponse;
import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.ReceiptService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/process")
    public ResponseEntity<ReceiptResponse> processReceipt(@Valid @RequestBody ReceiptRequest processRequest) {
        log.info("Process request received: " + processRequest);
        String id = receiptService.processReceipt(processRequest);
        ReceiptResponse response = new ReceiptResponse(id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<ReceiptPointsResponse> getReceiptPoints(@PathVariable @Valid String id) {
        log.info("Get Receipt Point request received for id: " + id);
        Receipt receipt = receiptService.getReceipt(id);
        if (receipt == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new ReceiptPointsResponse(receipt.getPoints()), HttpStatus.OK);
    }

}
