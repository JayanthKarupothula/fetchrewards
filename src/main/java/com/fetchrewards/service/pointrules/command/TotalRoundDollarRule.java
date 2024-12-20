package com.fetchrewards.service.pointrules.command;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommand;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;

import java.math.BigDecimal;

public class TotalRoundDollarRule extends ReceiptPointRuleCommand {
    private final int pointsToAdd;

    public TotalRoundDollarRule(Receipt receipt, ReceiptPointRuleOptions options) {
        super(receipt);
        pointsToAdd = options.getEvenTotalPoints();
    }

    @Override
    public void applyRule() {
        BigDecimal totalCents = receipt.getTotal().remainder(BigDecimal.ONE);
        if (totalCents.signum() == 0) {
            receipt.addPoints(pointsToAdd);
        }
    }
}
