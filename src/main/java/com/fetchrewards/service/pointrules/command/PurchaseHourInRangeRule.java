package com.fetchrewards.service.pointrules.command;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommand;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;

import java.time.LocalTime;

public class PurchaseHourInRangeRule extends ReceiptPointRuleCommand {

    private final int pointsToAdd;
    private final LocalTime rangeStart;
    private final LocalTime rangeEnd;

    public PurchaseHourInRangeRule(Receipt receipt, ReceiptPointRuleOptions options) {
        super(receipt);
        pointsToAdd = options.getPurchaseTimeInRangePoints();
        rangeStart = options.getPurchaseTimeRangeStart();
        rangeEnd = options.getPurchaseTimeRangeEnd();
    }

    @Override
    public void applyRule() {
        LocalTime receiptPurchaseTime = receipt.getPurchaseDateTime().toLocalTime();

        if (rangeStart.compareTo(receiptPurchaseTime) <= 0) {
            if (rangeEnd.compareTo(receiptPurchaseTime) >= 0) {
                receipt.addPoints(pointsToAdd);
            }
        }
    }
}
