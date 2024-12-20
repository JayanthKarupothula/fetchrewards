package com.fetchrewards.service.pointrules;

import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.pointrules.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReceiptPointRuleCommandFactoryUnitTests {

    @Spy
    ReceiptPointRuleCommandFactory factory;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @ParameterizedTest
    @MethodSource(value = "getCommandInputs")
    public void getCommandReturnsCommands(ReceiptPointRuleCommandType commandType, Class expectedCommandClass) {

        Receipt receipt = mock(Receipt.class);
        ReceiptPointRuleOptions options = mock(ReceiptPointRuleOptions.class);

        ReceiptPointRuleCommand command = factory.getCommand(commandType, receipt, options);

        assertEquals(expectedCommandClass, command.getClass());
    }

    public static List<Arguments> getCommandInputs() {
        return List.of(
                Arguments.of(ReceiptPointRuleCommandType.ITEM_GROUP_COUNT, ItemGroupCountRule.class),
                Arguments.of(ReceiptPointRuleCommandType.PURCHASE_DAY_ODD, PurchaseDayOddRule.class),
                Arguments.of(ReceiptPointRuleCommandType.TOTAL_ROUND_DOLLAR, TotalRoundDollarRule.class),
                Arguments.of(ReceiptPointRuleCommandType.TOTAL_QUARTER_MULTIPLE, TotalQuarterMultipleRule.class),
                Arguments.of(ReceiptPointRuleCommandType.PURCHASE_HOUR_IN_RANGE, PurchaseHourInRangeRule.class),
                Arguments.of(ReceiptPointRuleCommandType.ITEM_DESC_LENGTH_MULTIPLE, ItemDescLenMultipleRule.class),
                Arguments.of(ReceiptPointRuleCommandType.RETAILER_NAME_CHAR_COUNT, RetailerNameCharCountRule.class)
        );
    }

    @Test
    public void getAllCommandsReturnsAllCommands() {
        Receipt receipt = mock(Receipt.class);
        ReceiptPointRuleOptions options = mock(ReceiptPointRuleOptions.class);
        HashMap<Class, Integer> commandOccurencesMap = new HashMap<>(Map.of(
                ItemGroupCountRule.class, 0,
                PurchaseDayOddRule.class, 0,
                TotalRoundDollarRule.class, 0,
                TotalQuarterMultipleRule.class, 0,
                PurchaseHourInRangeRule.class, 0,
                ItemDescLenMultipleRule.class, 0,
                RetailerNameCharCountRule.class, 0
        ));

        var commands = factory.getAllCommands(receipt, options);

        commands.forEach(ruleCommand -> {
            Class commandClass = ruleCommand.getClass();
            commandOccurencesMap.put(commandClass, commandOccurencesMap.get(commandClass) + 1);
        });

        commandOccurencesMap.values().forEach(commandCount -> assertEquals(1, commandCount));
        assertEquals(commands.size(), ReceiptPointRuleCommandType.values().length);
    }

}
