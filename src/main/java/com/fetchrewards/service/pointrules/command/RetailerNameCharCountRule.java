package com.fetchrewards.service.pointrules.command;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommand;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;

import static com.fetchrewards.service.pointrules.ReceiptPointRuleOptions.NON_ALPHA_NUMERIC_WHITESPACE_REGEX;

public class RetailerNameCharCountRule extends ReceiptPointRuleCommand {

    private final int pointMultiplier;


    public RetailerNameCharCountRule(Receipt receipt, ReceiptPointRuleOptions options) {
        super(receipt);
        pointMultiplier = options.getRetailerNameCharCountMultiplier();
    }

    @Override
    public void applyRule() {
        String filteredRetailer = receipt.getRetailer().replaceAll(NON_ALPHA_NUMERIC_WHITESPACE_REGEX, "");
        int additionalPoints = filteredRetailer.length() * pointMultiplier;
        receipt.addPoints(additionalPoints);
    }


}
