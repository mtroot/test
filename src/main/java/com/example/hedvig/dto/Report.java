package com.example.hedvig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Report {

    private List<ReportRecord> records;

}
