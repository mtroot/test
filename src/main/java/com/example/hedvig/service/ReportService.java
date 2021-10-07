package com.example.hedvig.service;

import com.example.hedvig.dto.Report;
import com.example.hedvig.dto.ReportRecord;
import com.example.hedvig.model.Contract;
import com.example.hedvig.model.Event;
import com.example.hedvig.model.EventType;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ReportService {

    public Report calculate(List<Event> events, List<EventType> availableType, Integer year) {
        Map<Month, List<Event>> monthToEventsMap = events.stream()
                .filter(event -> Objects.equals(year, event.getDate().getYear()))
                .filter(event -> availableType.contains(event.getType()))
                .collect(Collectors.groupingBy(event -> event.getDate().getMonth()));
        return new Report(process(monthToEventsMap));
    }

    private List<ReportRecord> process(Map<Month, List<Event>> monthToEventsMap) {
        Map<Long, Contract> idToContractMap = new HashMap<>();
        List<ReportRecord> records = new ArrayList<>();
        AtomicLong agwpSum = new AtomicLong(0);
        Arrays.stream(Month.values()).forEach(month -> {
                    ReportRecord record = new ReportRecord();
                    record.setEgwp(agwpSum.get());
                    record.setMonth(month);
                    record.setNumberOfContracts(idToContractMap.size());
                    records.add(process(idToContractMap, monthToEventsMap.get(month), record));
                    agwpSum.addAndGet(record.getAgwp());
                }
        );
        return records;
    }

    private ReportRecord process(Map<Long, Contract> idToContractMap, List<Event> events, ReportRecord record) {
        Set<Long> processedContractIds = new HashSet<>();
        if (events != null) {
            events.stream().sorted(Comparator.comparing(Event::getType))
                    .forEach(event -> {
                        process(idToContractMap, event, processedContractIds, record);
                    });
        }
        for (Map.Entry<Long, Contract> contractEntry : idToContractMap.entrySet()) {
            if (!processedContractIds.contains(contractEntry.getKey())) {
                record.addToAGWP(contractEntry.getValue().getPrice());
                int delta = (12 - (record.getMonth().getValue() - 1)) * contractEntry.getValue().getPrice();
                record.addToEGWP(delta);
            }
        }
        return record;
    }

    private void process(Map<Long, Contract> idToContractMap, Event event, Set<Long> processedContractIds, ReportRecord record) {
        boolean isProcessed = processedContractIds.contains(event.getContractId());
        switch (event.getType()) {
            case CONTRACT_CREATED: {
                Contract contract = new Contract(event.getContractId(), event.getValue());
                idToContractMap.put(contract.getId(), contract);
                record.increaseNumberOfContract();
                record.addToAGWP(contract.getPrice());
                int delta = (12 - (record.getMonth().getValue() - 1)) * contract.getPrice();
                record.addToEGWP(delta);
                processedContractIds.add(contract.getId());
                break;
            }
            case CONTRACT_TERMINATED: {
                Contract contract = idToContractMap.remove(event.getContractId());
                if (!isProcessed) {
                    record.addToAGWP(contract.getPrice());
                    int delta = (12 - (record.getMonth().getValue() - 1)) * contract.getPrice();
                    record.addToEGWP(delta);
                }
                break;
            }
            case PRICE_INCREASED: {
                Contract contract = idToContractMap.get(event.getContractId());
                contract.setPrice(contract.getPrice() + event.getValue());
                record.addToAGWP(isProcessed ? event.getValue() : contract.getPrice());
                int delta = (12 - (record.getMonth().getValue() - 1)) *
                        (isProcessed ? event.getValue() : contract.getPrice());
                record.addToEGWP(delta);
                processedContractIds.add(contract.getId());
                break;
            }
            case PRICE_DECREASED: {
                Contract contract = idToContractMap.get(event.getContractId());
                contract.setPrice(contract.getPrice() - event.getValue());
                record.addToAGWP(isProcessed ? -event.getValue() : contract.getPrice());
                int delta = (12 - (record.getMonth().getValue() - 1)) *
                        (isProcessed ? -event.getValue() : contract.getPrice());
                record.addToEGWP(delta);
                processedContractIds.add(contract.getId());
                break;
            }
        }
    }

}
