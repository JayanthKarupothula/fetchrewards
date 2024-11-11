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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TotalQuarterMultipleRuleUnitTest {

    Receipt receipt;
    ReceiptPointRuleOptions options;
    ReceiptPointRuleCommand quarterMultipleCommand;


    @BeforeEach
    void setup() {
        receipt = spy(new Receipt("id", 0, "retailer", BigDecimal.ONE, LocalDateTime.now(),
                List.of(new ReceiptItem("shortDescription", BigDecimal.ONE))));
        options = new ReceiptPointRuleOptions();
        quarterMultipleCommand = new TotalQuarterMultipleRule(receipt, options);
    }

    @ParameterizedTest
    @MethodSource(value = "quarterMultipleInputs")
    void awardsQuarterMultiples(BigDecimal total) {

        receipt.setTotal(total);
        receipt.setPoints(0);
        int additionalPoints = options.getQuarterMultiplePoints();

        quarterMultipleCommand.applyRule();

        assertEquals(receipt.getPoints(), additionalPoints);
        verify(receipt, times(1)).addPoints(additionalPoints);
    }

    public static List<Arguments> quarterMultipleInputs() {
        return List.of(
                Arguments.of(BigDecimal.valueOf(1.00)),
                Arguments.of(BigDecimal.valueOf(2.25)),
                Arguments.of(BigDecimal.valueOf(3.50)),
                Arguments.of(BigDecimal.valueOf(5.75)),
                Arguments.of(BigDecimal.valueOf(40.00)),
                Arguments.of(BigDecimal.valueOf(125.25)),
                Arguments.of(BigDecimal.valueOf(500040.50))
        );
    }


    @ParameterizedTest
    @MethodSource(value = "notQuarterMultipleInputs")
    void doesNotAwardNonQuarterMultiples(BigDecimal total) {
        receipt.setTotal(total);
        receipt.setPoints(0);
        int additionalPoints = options.getEvenTotalPoints();

        quarterMultipleCommand.applyRule();

        assertEquals(receipt.getPoints(), 0);
        verify(receipt, times(0)).addPoints(additionalPoints);
    }

    public static List<Arguments> notQuarterMultipleInputs() {

        List<BigDecimal> nonQuarterMultiples = new ArrayList<>();
        for (int cents = 1; cents <= 99; cents++) {
            System.out.println(cents);
            if (cents != 25 && cents != 50 && cents != 75) {
                nonQuarterMultiples.add(new BigDecimal(cents).movePointLeft(2).setScale(2, RoundingMode.DOWN));
            }
        }

        return nonQuarterMultiples.stream().map(cent -> Arguments.of(cent)).toList();
    }

}
