package com.fetchrewards.service.pointrules.command;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommand;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;

public class ItemGroupCountRule extends ReceiptPointRuleCommand {
    private final int itemGroupSize;
    private final int pointsPerGroup;

    public ItemGroupCountRule(Receipt receipt, ReceiptPointRuleOptions options) {
        super(receipt);
        itemGroupSize = options.getItemGroupSize();
        pointsPerGroup = options.getItemGroupPointCount();
    }

    @Override
    public void applyRule() {
        int itemGroups = receipt.getItems().size() / itemGroupSize;
        if (itemGroups > 0) {
            receipt.addPoints(itemGroups * pointsPerGroup);
        }
    }
}
