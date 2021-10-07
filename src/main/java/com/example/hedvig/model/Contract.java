package com.example.hedvig.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Contract {

    private Long id;
    private Integer price;

    public Contract(Long id, Integer price) {
        this.id = id;
        this.price = price;
    }
}
