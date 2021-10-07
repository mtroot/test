package com.example.hedvig.utils;

import com.example.hedvig.model.Event;
import com.example.hedvig.model.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class EventConvertor {

    private static String NAME = "name";
    private static String CONTRACT_ID = "contractId";
    private static String PREMIUM = "premium";
    private static String PREMIUM_INCREASE = "premiumIncrease";
    private static String PREMIUM_REDUCTION = "premiumReduction";
    private static String AT_DATE = "atDate";
    private static String START_DATE = "startDate";
    private static String TERMINATION_DATE = "terminationDate";

    public static List<Event> convert(String data) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(data));
        List<Event> result = new ArrayList<>();
        reader.lines().forEach(json -> {
            try {
                getEvent(json).ifPresent(result::add);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }

    private static Optional<Event> getEvent(String json) throws JsonProcessingException {
        if (StringUtils.hasText(json)) {
            Map<String, Object> nameFieldToValueMap =
                    new ObjectMapper().readValue(json.trim(), HashMap.class);
            Event event = new Event();
            getType((String) nameFieldToValueMap.get(NAME))
                    .ifPresent(type -> fill(event, nameFieldToValueMap, type));
            return Optional.of(event);
        }
        return Optional.empty();
    }

    private static Optional<EventType> getType(String name) {
        return Arrays.stream(EventType.values())
                .filter(eventType -> Objects.equals(eventType.getSysName(), name))
                .findFirst();
    }

    private static Event fill(Event event, Map<String, Object> nameFieldToValueMap, EventType type) {
        Long aLong = getLong(nameFieldToValueMap.get(CONTRACT_ID));
        event.setContractId(aLong);
        event.setValue(getValue(nameFieldToValueMap, type));
        event.setDate(getDate(nameFieldToValueMap, type));
        event.setType(type);
        return event;
    }

    private static Long getLong(Object ob) {
        String s = (String) ob;
        return Long.valueOf(s);
    }

    private static Integer getInteger(Object ob) {
        return (Integer) ob;
    }

    private static Integer getValue(Map<String, Object> nameFieldToValueMap, EventType type) {
        switch (type) {
            case CONTRACT_CREATED:
                return getInteger(nameFieldToValueMap.get(PREMIUM));
            case PRICE_INCREASED:
                return getInteger(nameFieldToValueMap.get(PREMIUM_INCREASE));
            case PRICE_DECREASED:
                return getInteger(nameFieldToValueMap.get(PREMIUM_REDUCTION));
            case CONTRACT_TERMINATED:
                return null;
            default:
                throw new RuntimeException("unsupported type " + type);
        }
    }

    private static LocalDate getDate(Object ob) {
        return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse((String) ob));
    }

    private static LocalDate getDate(Map<String, Object> nameFieldToValueMap, EventType type) {
        switch (type) {
            case CONTRACT_CREATED:
                return getDate(nameFieldToValueMap.get(START_DATE));
            case PRICE_INCREASED:
            case PRICE_DECREASED:
                return getDate(nameFieldToValueMap.get(AT_DATE));
            case CONTRACT_TERMINATED:
                return getDate(nameFieldToValueMap.get(TERMINATION_DATE));
            default:
                throw new RuntimeException("unsupported type " + type);
        }
    }
}
