package com.example.hedvig.resource;

import com.example.hedvig.dto.Report;
import com.example.hedvig.dto.ReportType;
import com.example.hedvig.model.Event;
import com.example.hedvig.model.EventType;
import com.example.hedvig.service.ReportService;
import com.example.hedvig.utils.EventConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportResource {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> create(@RequestParam(defaultValue = "SIMPLE") ReportType reportType,
                                         @RequestParam(defaultValue = "2020") Integer year,
                                         @RequestBody String data) throws IOException {
        List<Event> events = EventConvertor.convert(data);
        return ResponseEntity.ok(reportService.calculate(events, getAvailableTypes(reportType), year));
    }

    private List<EventType> getAvailableTypes(ReportType type) {
        switch (type) {
            case FULL: return Arrays.asList(EventType.values());
            case SIMPLE:
            default: return Arrays.asList(EventType.CONTRACT_CREATED, EventType.CONTRACT_TERMINATED);
        }
    }

}
