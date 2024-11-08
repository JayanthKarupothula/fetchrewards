package com.fetchrewards.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItem {
    String shortDescription;
    BigDecimal price;

    @Override
    public String toString() {
        return "ReceiptItem [shortDescription=" + shortDescription + ", price=" + price + "]";
    }

}
