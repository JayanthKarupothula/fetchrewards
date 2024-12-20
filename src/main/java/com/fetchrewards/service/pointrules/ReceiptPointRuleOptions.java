package com.fetchrewards.service.pointrules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptPointRuleOptions {


    public static final String NON_ALPHA_NUMERIC_WHITESPACE_REGEX = "[^a-zA-Z\\d]+";

    private LocalTime purchaseTimeRangeEnd = LocalTime.of(16, 00);
    private LocalTime purchaseTimeRangeStart = LocalTime.of(14, 00);
    private BigDecimal itemDescriptionPointMultiplier = BigDecimal.valueOf(0.20);
    private int itemDescriptionLengthMultiple = 3;
    private int itemGroupSize = 2;
    private int itemGroupPointCount = 5;
    private int evenTotalPoints = 50;
    private int oddPurchaseDayPoints = 6;
    private int quarterMultiplePoints = 25;
    private int purchaseTimeInRangePoints = 10;
    private int retailerNameCharCountMultiplier = 1;

}
