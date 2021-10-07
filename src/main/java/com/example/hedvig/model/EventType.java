package com.example.hedvig.model;

import java.util.Arrays;

public enum EventType {
    CONTRACT_CREATED("ContractCreatedEvent"),
    PRICE_INCREASED("PriceIncreasedEvent"),
    PRICE_DECREASED("PriceDecreasedEvent"),
    CONTRACT_TERMINATED("ContractTerminatedEvent");

    private String sysName;

    EventType(String sysName) {
        this.sysName = sysName;
    }

    public String getSysName() {
        return this.sysName;
    }
}
