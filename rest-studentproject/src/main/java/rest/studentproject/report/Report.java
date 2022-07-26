package rest.studentproject.report;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import rest.studentproject.rules.IRestRule;
import rest.studentproject.rules.Violation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * this class handles all functionality concerning a report generation.
 */
public class Report {

    private static Report instance;

    public static Report getInstance() {
        if (instance != null) {
            return instance;
        } else {
            return new Report();
        }
    }

    public String descriptionReport;

    public void generateReport(List<Violation> violationList) {


        violationList.sort(Violation.getComparator());
        writeMarkdownReport(violationList);


    }

    private void writeMarkdownReport(List<Violation> violationList) {
        String currentKey = "";

        StringBuilder sb = new StringBuilder();
        sb.append(new Heading("REST API Specification Report", 1)).append("\n");
        Table.Builder tableBuilder = new Table.Builder();

        //Table heading
        tableBuilder.addRow("Line", "Line No.", "Rule", "Category",
                "Severity", "Rule Type", "Software Quality Attributes", "Violation", "Improvement Suggestion");

        for (Violation v : violationList) {
            IRestRule rule = v.getRule();
            tableBuilder.addRow(v.getKeyViolation(), v.getLineViolation(),
                    rule.getTitle(), rule.getCategory(), rule.getSeverityType(),
                    rule.getRuleType(), rule.getRuleSoftwareQualityAttribute().toString(), v.getErrorMessage(), v.getImprovementSuggestion());
        }

        sb.append(tableBuilder.build());

        //print to console
        System.out.println(sb);
        try {
            //get current time
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            //write file
            String filename = "Report_" + now + ".md";
            FileWriter fileWriter = new FileWriter(filename);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(sb);

            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {

        return "";
    }
}
