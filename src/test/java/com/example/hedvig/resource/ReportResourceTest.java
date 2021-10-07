package com.example.hedvig.resource;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.hedvig.dto.Report;
import com.example.hedvig.dto.ReportRecord;
import com.example.hedvig.dto.ReportType;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Month;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportResourceTest extends AbstractTestNGSpringContextTests {

    public final static String BIG_DATA_PATH = "/test-data.txt";
    public final static String ONE_CONTRACT_DATA_PATH = "/test-data-one-contract.txt";
    public final static String TWO_CONTRACTS_DATA_PATH = "/test-data-two-contracts.txt";

    @Autowired
    private ReportResource reportResource;

    @Test
    public void create_correctData_success() throws IOException {
        ResponseEntity<Report> reportResponseEntity = reportResource.create(ReportType.FULL, 2020, getTestData(BIG_DATA_PATH));

        assertThat(reportResponseEntity).isNotNull();
    }

    @Test
    public void create_simpleModeAndOneContract_success() throws IOException {
        ResponseEntity<Report> reportResponseEntity = reportResource.create(ReportType.SIMPLE, 2020, getTestData(ONE_CONTRACT_DATA_PATH));

        assertThat(reportResponseEntity).isNotNull();
        reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> reportRecord.getMonth().getValue() <= 4).forEach(reportRecord -> {
                            assertThat(reportRecord.getNumberOfContracts()).isEqualTo(1);
                            assertThat(reportRecord.getAgwp()).isEqualTo(100);
                            assertThat(reportRecord.getEgwp()).isEqualTo(1200);
                        });
        reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> reportRecord.getMonth().getValue() > 4).forEach(reportRecord -> {
                    assertThat(reportRecord.getNumberOfContracts()).isEqualTo(0);
                    assertThat(reportRecord.getAgwp()).isEqualTo(0);
                    assertThat(reportRecord.getEgwp()).isEqualTo(400);
                });
    }

    @Test
    public void create_fullModeAndOneContract_success() throws IOException {
        ResponseEntity<Report> reportResponseEntity = reportResource.create(ReportType.FULL, 2020, getTestData(ONE_CONTRACT_DATA_PATH));

        assertThat(reportResponseEntity).isNotNull();

        ReportRecord janRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.JANUARY.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(janRecord).isNotNull();
        assertThat(janRecord.getNumberOfContracts()).isEqualTo(1);
        assertThat(janRecord.getAgwp()).isEqualTo(100);
        assertThat(janRecord.getEgwp()).isEqualTo(1200);
        ReportRecord febRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.FEBRUARY.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(febRecord).isNotNull();
        assertThat(febRecord.getNumberOfContracts()).isEqualTo(1);
        assertThat(febRecord.getAgwp()).isEqualTo(90);
        assertThat(febRecord.getEgwp()).isEqualTo(1090);
        ReportRecord marRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.MARCH.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(marRecord).isNotNull();
        assertThat(marRecord.getNumberOfContracts()).isEqualTo(1);
        assertThat(marRecord.getAgwp()).isEqualTo(100);
        assertThat(marRecord.getEgwp()).isEqualTo(1190);
        ReportRecord aprRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.APRIL.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(aprRecord).isNotNull();
        assertThat(aprRecord.getNumberOfContracts()).isEqualTo(1);
        assertThat(aprRecord.getAgwp()).isEqualTo(110);
        assertThat(aprRecord.getEgwp()).isEqualTo(1280);
        reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> reportRecord.getMonth().getValue() > 4).forEach(reportRecord -> {
                    assertThat(reportRecord.getNumberOfContracts()).isEqualTo(0);
                    assertThat(reportRecord.getAgwp()).isEqualTo(0);
                    assertThat(reportRecord.getEgwp()).isEqualTo(400);
                });
    }

    @Test
    public void create_simpleModeAndTwoContract_success() throws IOException {
        ResponseEntity<Report> reportResponseEntity =
                reportResource.create(ReportType.SIMPLE, 2020, getTestData(TWO_CONTRACTS_DATA_PATH));

        assertThat(reportResponseEntity).isNotNull();
        reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.JANUARY.equals(reportRecord.getMonth()) ||
                        Month.FEBRUARY.equals(reportRecord.getMonth())).forEach(reportRecord -> {
                    assertThat(reportRecord.getNumberOfContracts()).isEqualTo(1);
                    assertThat(reportRecord.getAgwp()).isEqualTo(100);
                    assertThat(reportRecord.getEgwp()).isEqualTo(1200);
                });
        reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.MARCH.equals(reportRecord.getMonth()) ||
                        Month.APRIL.equals(reportRecord.getMonth())).forEach(reportRecord -> {
                    assertThat(reportRecord.getNumberOfContracts()).isEqualTo(2);
                    assertThat(reportRecord.getAgwp()).isEqualTo(300);
                    assertThat(reportRecord.getEgwp()).isEqualTo(3200);
                });
        reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.MAY.equals(reportRecord.getMonth()) ||
                        Month.JUNE.equals(reportRecord.getMonth())).forEach(reportRecord -> {
                    assertThat(reportRecord.getNumberOfContracts()).isEqualTo(1);
                    assertThat(reportRecord.getAgwp()).isEqualTo(200);
                    assertThat(reportRecord.getEgwp()).isEqualTo(2400);
                });
        reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> reportRecord.getMonth().getValue() > 6).forEach(reportRecord -> {
                    assertThat(reportRecord.getNumberOfContracts()).isEqualTo(0);
                    assertThat(reportRecord.getAgwp()).isEqualTo(0);
                    assertThat(reportRecord.getEgwp()).isEqualTo(1200);
                });
    }

    @Test
    public void create_fullModeAndTwoContract_success() throws IOException {
        ResponseEntity<Report> reportResponseEntity =
                reportResource.create(ReportType.FULL, 2020, getTestData(TWO_CONTRACTS_DATA_PATH));

        assertThat(reportResponseEntity).isNotNull();

        ReportRecord janRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.JANUARY.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(janRecord).isNotNull();
        assertThat(janRecord.getNumberOfContracts()).isEqualTo(1);
        assertThat(janRecord.getAgwp()).isEqualTo(100);
        assertThat(janRecord.getEgwp()).isEqualTo(1200);
        ReportRecord febRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.FEBRUARY.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(febRecord).isNotNull();
        assertThat(febRecord.getNumberOfContracts()).isEqualTo(1);
        assertThat(febRecord.getAgwp()).isEqualTo(90);
        assertThat(febRecord.getEgwp()).isEqualTo(1090);
        ReportRecord marRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.MARCH.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(marRecord).isNotNull();
        assertThat(marRecord.getNumberOfContracts()).isEqualTo(2);
        assertThat(marRecord.getAgwp()).isEqualTo(300);
        assertThat(marRecord.getEgwp()).isEqualTo(3190);
        ReportRecord aprRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.APRIL.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(aprRecord).isNotNull();
        assertThat(aprRecord.getNumberOfContracts()).isEqualTo(2);
        assertThat(aprRecord.getAgwp()).isEqualTo(260);
        assertThat(aprRecord.getEgwp()).isEqualTo(2830);
        ReportRecord mayRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.MAY.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(mayRecord).isNotNull();
        assertThat(mayRecord.getNumberOfContracts()).isEqualTo(1);
        assertThat(mayRecord.getAgwp()).isEqualTo(200);
        assertThat(mayRecord.getEgwp()).isEqualTo(2350);
        ReportRecord junRecord = reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> Month.JUNE.equals(reportRecord.getMonth())).findFirst().orElse(null);
        assertThat(junRecord).isNotNull();
        assertThat(junRecord.getNumberOfContracts()).isEqualTo(1);
        assertThat(junRecord.getAgwp()).isEqualTo(250);
        assertThat(junRecord.getEgwp()).isEqualTo(2700);
        reportResponseEntity.getBody().getRecords()
                .stream().filter(reportRecord -> reportRecord.getMonth().getValue() > 6).forEach(reportRecord -> {
                    assertThat(reportRecord.getNumberOfContracts()).isEqualTo(0);
                    assertThat(reportRecord.getAgwp()).isEqualTo(0);
                    assertThat(reportRecord.getEgwp()).isEqualTo(1200);
                });
    }


    private String getTestData(String fileName) {
        return new String(readDocument(fileName), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static byte[] readDocument(String fileName) {
        InputStream inputStream = ReportResourceTest.class.getResourceAsStream(fileName);
        return IOUtils.toByteArray(inputStream);
    }

}
