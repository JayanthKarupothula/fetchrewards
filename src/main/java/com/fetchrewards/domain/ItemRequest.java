package com.fetchrewards.domain;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class ItemRequest {

    @NotNull(message = "Short description is required")
    @NotBlank(message = "Short description must not be blank")
    String shortDescription;

    @NotNull(message = "Price is required")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 decimal places")
    @DecimalMin(value = "0.00", inclusive = true , message = "Price must be non-negative")
    @DecimalMax(value = "2147483647.00", inclusive = true)
    BigDecimal price;

    @Override
    public String toString() {
        return "ReceiptItem [shortDescription=" + shortDescription + ", price=" + price + "]";
    }
}
