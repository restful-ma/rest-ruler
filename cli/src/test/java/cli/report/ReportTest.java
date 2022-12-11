package cli.report;

import cli.rule.Violation;
import cli.rule.rules.UnderscoreRule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {


    @Test
    void generateReport() {

        List<Violation> violationList = new ArrayList<>();

        //create dummy data
        for (int i = 0; i < 5; i++) {
            violationList.add(new Violation(new UnderscoreRule(true), i, "%i", "%i", "%i"));
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

    @Test
    void testLongTitleInput(){
        String veryLongTitle = "sdfmsdfmsdkfmsdkfmksdfmksdmfksdfmskdfmsddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddsdfmsdfmsdkfmsdkfmksdfmksdmfksdfmskdfmsdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd";
        String expectedTitle = "Report_" + veryLongTitle.substring(0,30) +".md";
        List<Violation> violationList = new ArrayList<>();

        //create dummy data
        for (int i = 0; i < 10; i++) {
            violationList.add(new Violation(new UnderscoreRule(true), i, "%i", "%i", "%i"));
        }
        //execute report method
        Report report = Report.getInstance();
        report.generateReport(violationList,veryLongTitle);

        //search for file in output directory
        Path path = Path.of("out");

        try (var files = Files.list(path)) {
            assertTrue(files.anyMatch(f -> f.getFileName().toString().endsWith(expectedTitle)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIllegalTitleInput(){
        String veryIllegalTitle = " thisis a%{}superilleagaltitle&&<>";
        String expectedTitle = "Report_thisis-asuperilleagaltitle.md";

        List<Violation> violationList = new ArrayList<>();

        //create dummy data
        for (int i = 0; i < 5; i++) {
            violationList.add(new Violation(new UnderscoreRule(true), i, "%i", "%i", "%i"));
        }
        //execute report method
        Report report = Report.getInstance();
        report.generateReport(violationList,veryIllegalTitle);

        //search for file in output directory
        Path path = Path.of("out");

        try (var files = Files.list(path)) {
            assertTrue(files.anyMatch(f -> f.getFileName().toString().endsWith(expectedTitle)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}