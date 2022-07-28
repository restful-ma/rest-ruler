package rest.studentproject.report;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rest.studentproject.rules.UnderscoreRule;
import rest.studentproject.rules.Violation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void generateReport() {

        List<Violation> violationList = new ArrayList<>();

        //create dummy data
        for (int i = 0; i < 10; i++) {
            new Violation(new UnderscoreRule(true),i, "%i", "%i", "%i");
        }
        //execute report method
        Report report = Report.getInstance();
        report.generateReport(violationList);


        //search for file in output directory
        Path path = Path.of("out");

        try (var files = Files.list(path)) {
            assertTrue(files.count() >= 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}