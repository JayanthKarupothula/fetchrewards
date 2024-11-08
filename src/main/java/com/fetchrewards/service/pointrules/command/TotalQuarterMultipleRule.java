package com.fetchrewards.service.pointrules.command;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommand;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;

import java.math.BigDecimal;

public class TotalQuarterMultipleRule extends ReceiptPointRuleCommand {

    private final int pointsToAdd;

    public TotalQuarterMultipleRule(Receipt receipt, ReceiptPointRuleOptions options) {
        super(receipt);
        pointsToAdd = options.getQuarterMultiplePoints();
    }

    @Override
    public void applyRule() {
        // Use remainder / modulus operator to get only cent amount
        BigDecimal totalCents = receipt.getTotal().remainder(BigDecimal.ONE);
        if (totalCents.remainder(BigDecimal.valueOf(0.25)).signum() == 0) {
            receipt.addPoints(pointsToAdd);
        }
    }
}
