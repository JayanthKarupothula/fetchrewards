package com.fetchrewards.domain;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReceiptRequest {

    @NotBlank(message = "Retailer name must not be blank")
    @NotNull(message = "Retailer name is required")
    String retailer;

    @NotNull(message = "Purchase date is required")
    @DateTimeFormat(pattern = "YYYY:MM:dd")
    LocalDate purchaseDate;

    @NotNull(message = "Purchase time is required")
    @DateTimeFormat(pattern = "HH:mm")
    LocalTime purchaseTime;

    // Cap at max int for now: 2^31 -> 2147483648.00
    @NotNull(message = "Total is required")
    @Digits(integer = 10, fraction = 2, message = "Total must have up to 10 integer digits and 2 decimal places")
    @DecimalMin(value = "0.00", inclusive = true, message = "Total must be non-negative")
    @DecimalMax(value = "2147483647.00", inclusive = true)
    BigDecimal total;

    @Valid
    @NotNull(message = "Items are required")
    @Size(min = 1, message = "At least one item is required")
    List<ItemRequest> items;

    @Override
    public String toString() {
        return "ProcessReceiptRequest{" +
                "retailer='" + retailer + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", purchaseTime=" + purchaseTime +
                ", total=" + total +
                ", items=" + items +
                '}';
    }
}
