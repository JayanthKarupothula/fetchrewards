package com.fetchrewards.service.pointrules.command;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.model.ReceiptItem;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommand;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RetailerNameCharCountRuleUnitTest {

    Receipt receipt;
    ReceiptPointRuleOptions options;
    ReceiptPointRuleCommand retailerCharCountRule;

    static String nonAlphaNumericalChars = "~`!@#$%^&*()-_=+{}[];:'\\|/?,.<>";

    @BeforeEach
    void setup() {
        receipt = spy(new Receipt("id", 0, "retailer", BigDecimal.ONE, LocalDateTime.now(),
                List.of(new ReceiptItem("shortDescription", BigDecimal.ONE))));
        options = new ReceiptPointRuleOptions();
        retailerCharCountRule = new RetailerNameCharCountRule(receipt, options);
    }

    @ParameterizedTest
    @MethodSource(value = "roundTotalInputs")
    void awardsPointsForAlphaNumericsOnly(String retailerName, int expectedCharCount) {

        receipt.setRetailer(retailerName);
        receipt.setPoints(0);
        int additionalPoints = expectedCharCount * options.getRetailerNameCharCountMultiplier();

        retailerCharCountRule.applyRule();

        assertEquals(receipt.getPoints(), additionalPoints);
        verify(receipt, times(1)).addPoints(additionalPoints);
    }

    public static List<Arguments> roundTotalInputs() {

        Map<String, Integer> namePoints = new LinkedHashMap<String, Integer>(Map.of(
                " ", 0,
                "!", 0,
                "Walgreens", 9,
                "Walgreens123", 12,
                "123Walgreens", 12,
                "Wal123greens", 12,
                "123Walgreens456", 15,
                "123Wal456greens", 15,
                "123Wal456greens7890", 19,
                "0987654321Wal1234567890greens0987654321", 39)
        );

        Map<String, Integer> nonAlphaNumericCombinations = new LinkedHashMap<>();
        namePoints.entrySet().forEach(
                entry -> {
                    String retailer = entry.getKey();
                    nonAlphaNumericCombinations.put(retailer, entry.getValue());
                    nonAlphaNumericCombinations.put(nonAlphaNumericalChars + retailer, entry.getValue());
                    nonAlphaNumericCombinations.put(retailer + nonAlphaNumericalChars, entry.getValue());
                    nonAlphaNumericCombinations.put(retailer.substring(0, Math.min(2, retailer.length())) +
                            nonAlphaNumericalChars +
                            retailer.substring(Math.min(2, retailer.length()), retailer.length()), entry.getValue());
                }
        );

        return nonAlphaNumericCombinations.entrySet()
                .stream()
                .map(entry -> {
                    return Arguments.of(entry.getKey(), entry.getValue());
                })
                .toList();
    }
}
