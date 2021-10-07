package com.example.hedvig.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Event {

    private EventType type;

    private Long contractId;

    private Integer value;

    private LocalDate date;
}
