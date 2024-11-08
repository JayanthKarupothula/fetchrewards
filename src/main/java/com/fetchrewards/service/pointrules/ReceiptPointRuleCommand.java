package com.fetchrewards.service.pointrules;

import com.fetchrewards.model.Receipt;

public abstract class ReceiptPointRuleCommand {

    protected final Receipt receipt;

    public ReceiptPointRuleCommand(Receipt receipt) {
        this.receipt = receipt;
    }

    public abstract void applyRule();
}
