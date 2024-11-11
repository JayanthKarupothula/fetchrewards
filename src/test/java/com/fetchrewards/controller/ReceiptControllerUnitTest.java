package com.fetchrewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fetchrewards.domain.ItemRequest;
import com.fetchrewards.domain.ReceiptRequest;
import com.fetchrewards.model.Receipt;
import com.fetchrewards.service.ReceiptService;
import org.json.JSONException;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.json.JSONObject;


@WebMvcTest(ReceiptController.class)
public class ReceiptControllerUnitTest  {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceiptService receiptService;

    private JSONObject processReceiptRequestJson;


    @BeforeEach
    public void setup() throws JSONException {

        String requestJson = "{" +
                "    'retailer': 'Walgreens'," +
                "    'purchaseDate': '2023-01-03'," +
                "    'purchaseTime': '16:04'," +
                "    'total': '12.34'," +
                "    'items': [" +
                "        {'shortDescription': 'Pepsi - 12-oz', 'price': '1.25'}," +
                "        {'shortDescription': 'Dasani', 'price': '5.1'}," +
                "    ]" +
                "}";
        processReceiptRequestJson = new JSONObject(requestJson);
    }

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

    @Test
    public void testGetReceiptPointsNotFound() throws Exception {
        String notFoundUuid = "this_id_is_not_found";


        when(receiptService.getReceipt(notFoundUuid)).thenReturn(null);

        mockMvc.perform(get("/receipts/" + notFoundUuid + "/points"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());

        verify(receiptService, times(1)).getReceipt(notFoundUuid);
    }

    @Test
    public void testProcessReceiptMissingRequestBody() throws Exception {
        mockMvc.perform(post("/receipts/process"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidReceiptRequestFields")
    public void returns4xxOnInvalidReceiptRequestFields(String field, String value) throws Exception {
        processReceiptRequestJson.put(field, value);
        mockMvc.perform(post("/receipts/process").contentType(MediaType.APPLICATION_JSON)
                        .content(processReceiptRequestJson.toString()))
                .andExpect(status().isBadRequest());  // Expect 400 status for invalid input

}

    public static List<Arguments> invalidReceiptRequestFields() {
        return List.of(
                Arguments.of("retailer", ""),
                Arguments.of("retailer", "           "),
                Arguments.of("purchaseDate", "2023--01-03"),
                Arguments.of("purchaseDate", "2023-01--03"),
                Arguments.of("purchaseTime", "24:00"),
                Arguments.of("purchaseTime", "111:00"),
                Arguments.of("total", "-1.00"),
                Arguments.of("total", "2147483648.00"),
                Arguments.of("total", "1.23 badText")
        );
    }

    @ParameterizedTest
    @MethodSource(value = "missingRequestFields")
    public void returns4xxOnMissingReceiptRequestFields(String field) throws Exception {
        processReceiptRequestJson.remove(field);
        mockMvc.perform(post("/receipts/process").contentType(MediaType.APPLICATION_JSON)
                        .content(processReceiptRequestJson.toString()))
                .andExpect(status().isBadRequest());
    }

    public static List<Arguments> missingRequestFields() throws JSONException {
        return List.of(
                Arguments.of("retailer"),
                Arguments.of("purchaseDate"),
                Arguments.of("purchaseTime"),
                Arguments.of("total"),
                Arguments.of("items")
        );
    }

    @ParameterizedTest
    @MethodSource(value = "invalidItemFields")
    public void returns4xxOnInvalidItemFields(JSONArray itemsJson) throws Exception {
        processReceiptRequestJson.put("items", itemsJson);
        mockMvc.perform(post("/receipts/process").contentType(MediaType.APPLICATION_JSON)
                        .content(processReceiptRequestJson.toString()))
                .andExpect(status().isBadRequest());
    }

    public static List<Arguments> invalidItemFields() throws JSONException {
        return List.of(
                Arguments.of(new JSONArray("[]")),
                Arguments.of(new JSONArray("[ {'price': '0.00'} ]")),
                Arguments.of(new JSONArray("[ {shortDescription: 'blah'} ]")),
                Arguments.of(new JSONArray("[ {shortDescription: 'blah', price: '-1.00'} ]")),
                Arguments.of(new JSONArray("[ {shortDescription: 'blah', price: '2147483648.00'} ]")),
                Arguments.of(new JSONArray("[ {shortDescription: '', price: '1.23'} ]")),
                Arguments.of(new JSONArray("[ {shortDescription: '   ', price: '1.23'} ]")),
                Arguments.of(new JSONArray("[ {shortDescription: 'blah', price: ''} ]")),
                Arguments.of(new JSONArray("[ {shortDescription: 'blah', price: '     '} ]"))
        );
    }




}
