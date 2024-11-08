package com.fetchrewards.service.pointrules.command;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommand;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;

public class PurchaseDayOddRule extends ReceiptPointRuleCommand {

    private final int pointsToAdd;

    public PurchaseDayOddRule(Receipt receipt, ReceiptPointRuleOptions options) {
        super(receipt);
        pointsToAdd = options.getOddPurchaseDayPoints();
    }

    @Override
    public void applyRule() {
        if (receipt.getPurchaseDateTime().getDayOfMonth() % 2 == 1) {
            receipt.addPoints(pointsToAdd);
        }
    }
}
