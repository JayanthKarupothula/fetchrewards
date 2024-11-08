package com.fetchrewards.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Receipt {

    String id;
    int points;
    String retailer;
    BigDecimal total;
    LocalDateTime purchaseDateTime;
    List<ReceiptItem> items;


    @Override
    public String toString() {
        return "Receipt [id=" + id + ", points=" + points + ", retailer=" + retailer + ", purchaseDateTime="
                + purchaseDateTime + ", total=" + total + ", items=" + items + "]";
    }

    public void addPoints(int points) {
        this.points += points;
    }
}
