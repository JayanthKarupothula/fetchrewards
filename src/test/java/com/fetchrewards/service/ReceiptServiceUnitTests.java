package com.fetchrewards.service;

import com.fetchrewards.domain.ItemRequest;
import com.fetchrewards.domain.ReceiptRequest;
import com.fetchrewards.model.Receipt;
import com.fetchrewards.model.ReceiptItem;
import com.fetchrewards.repository.ReceiptRepository;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommand;
import com.fetchrewards.service.pointrules.ReceiptPointRuleCommandFactory;
import com.fetchrewards.service.pointrules.ReceiptPointRuleOptions;
import com.fetchrewards.service.pointrules.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReceiptServiceUnitTests {

    @Spy
    @InjectMocks
    ReceiptService service;

    @Mock
    ReceiptRepository repository;

    @Mock
    ReceiptPointRuleCommandFactory ruleFactory;

    @Mock
    ReceiptPointRuleOptions defaultReceiptPointRuleOptions;


    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void processReceipt() {
        Receipt receipt = mock(Receipt.class);
        ReceiptPointRuleOptions options = mock(ReceiptPointRuleOptions.class);
        ReceiptRequest processRequest = mock(ReceiptRequest.class);

        doNothing().when(repository).saveReceipt(receipt);
        doNothing().when(service).applyPoints(receipt, options);
        doReturn(receipt).when(service).convertReceiptRequestToModel(processRequest);

        service.processReceipt(processRequest);

        verify(service, times(1)).convertReceiptRequestToModel(processRequest);
        verify(service).applyPoints(same(receipt), any(ReceiptPointRuleOptions.class));
        verify(repository, times(1)).saveReceipt(receipt);
    }


    @Test
    public void applyPointsAppliesRuleCommandsOnce() {
        Receipt receipt = mock(Receipt.class);
        ReceiptPointRuleOptions options = mock(ReceiptPointRuleOptions.class);
        List<ReceiptPointRuleCommand> mockedRuleCommands = List.of(
                mock(ItemDescLenMultipleRule.class),
                mock(ItemGroupCountRule.class),
                mock(PurchaseDayOddRule.class),
                mock(PurchaseHourInRangeRule.class),
                mock(RetailerNameCharCountRule.class),
                mock(TotalQuarterMultipleRule.class),
                mock(TotalRoundDollarRule.class)
        );

        when(ruleFactory.getAllCommands(receipt, options)).thenReturn(mockedRuleCommands);

        service.applyPoints(receipt, options);


        verify(ruleFactory, times(1)).getAllCommands(receipt, options);
        mockedRuleCommands.forEach(rule -> verify(rule, times(1)).applyRule());
    }


    @Test
    public void convertReceiptRequestToEntityMapsCorrectly() {
        ReceiptRequest receiptRequest = ReceiptRequest.builder()
                .retailer("retailerName")
                .purchaseDate(LocalDate.now())
                .purchaseTime(LocalTime.now())
                .total(new BigDecimal("12.34"))
                .items(List.of(
                        new ItemRequest("desc1", new BigDecimal("10.00")),
                        new ItemRequest("desc2", new BigDecimal("2.34"))
                ))
                .build();

        Receipt convertedEntity = service.convertReceiptRequestToModel(receiptRequest);

        assertEquals(convertedEntity.getPoints(), 0);
        assertEquals(convertedEntity.getRetailer(), receiptRequest.getRetailer());
        assertEquals(convertedEntity.getTotal(), receiptRequest.getTotal());
        assertEquals(convertedEntity.getPurchaseDateTime().toLocalDate(), receiptRequest.getPurchaseDate());
        assertEquals(convertedEntity.getPurchaseDateTime().toLocalTime(), receiptRequest.getPurchaseTime().truncatedTo(ChronoUnit.MINUTES));
        assertEquals(receiptRequest.getItems().size(), convertedEntity.getItems().size());
        for (int i = 0; i < receiptRequest.getItems().size(); i++) {
            ItemRequest requestItem = receiptRequest.getItems().get(i);
            ReceiptItem convertedItem = convertedEntity.getItems().get(i);
            assertEquals(requestItem.getShortDescription(), convertedItem.getShortDescription());
            assertEquals(requestItem.getPrice(), convertedItem.getPrice());
        }
    }
}
