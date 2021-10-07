package com.example.hedvig.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;

@Data
@NoArgsConstructor
public class ReportRecord {

    private Month month;

    private Integer numberOfContracts;

    /**
     * Actual gross written premium (AGWP): The accumulated premium that should have been
     * paid in every month.
     */
    private Long agwp = 0L;

    /**
     * Expected gross written premium (EGWP): The expected sum of all premiums for the year.
     */
    private Long egwp = 0L;

    public void increaseNumberOfContract() {
        numberOfContracts++;
    }

    public void addToAGWP(Integer delta) {
        this.agwp += delta;
    }

    public void addToEGWP(Integer delta) {
        this.egwp += delta;
    }
}
