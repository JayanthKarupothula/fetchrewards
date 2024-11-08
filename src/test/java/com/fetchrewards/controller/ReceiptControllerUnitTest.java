

package com.fetchrewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fetchrewards.domain.ItemRequest;
import com.fetchrewards.domain.ReceiptRequest;
import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.ReceiptService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.convert.ApplicationConversionService.configure;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
@WebMvcTest(ReceiptController.class)
public class ReceiptControllerUnitTest  {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceiptService receiptService;


    @Test
    public void testProcessReceipt() throws Exception{

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        ReceiptRequest request = ReceiptRequest.builder()
                .retailer("retailer")
                .purchaseDate(LocalDate.now())
                .purchaseTime(LocalTime.now())
                .total(new BigDecimal("1.12"))
                .items(List.of(new ItemRequest("description", new BigDecimal("1.23"))))
                .build();

        String json = objectMapper.writeValueAsString(request);

        String mockReceiptId = UUID.randomUUID().toString();
        when(receiptService.processReceipt(any(ReceiptRequest.class))).thenReturn(mockReceiptId);

        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("id")))
                .andExpect(content().json("{ id: " + mockReceiptId + "}"));

        verify(receiptService, times(1)).processReceipt(any(ReceiptRequest.class));

    }

    @Test
    public void testGetReceiptPoints() throws Exception {
        int expectedPoints = 22;
        String mockUuid = UUID.randomUUID().toString();
        Receipt mockReceipt = new Receipt();
        mockReceipt.setPoints(expectedPoints);
        when(receiptService.getReceipt(mockUuid)).thenReturn(mockReceipt);
        mockMvc.perform(get("/receipts/" + mockUuid + "/points"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ points: " + expectedPoints + "}"));

        verify(receiptService, times(1)).getReceipt(mockUuid);
    }

}
