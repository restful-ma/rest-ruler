package rest.studentproject.report;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * this class handles all functionality concerning a report generation.
 */
public class Report {

    private static final String OUTPUT_DIR = "out";
    private static Report instance;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static Report getInstance() {
        if (instance != null) {
            return instance;
        } else {
            return new Report();
        }
    }

    public void generateReport(List<Violation> violationList) {
        violationList.sort(Violation.getComparator());
        writeMarkdownReport(violationList);
    }

    private void writeMarkdownReport(List<Violation> violationList) {
        StringBuilder sbMDReport = new StringBuilder();
        sbMDReport.append(new Heading("REST API Specification Report", 1)).append("\n");
        StringBuilder sbConsoleReport = new StringBuilder();
        sbConsoleReport.append(new Heading("REST API Specification Report", 1)).append("\n");

        // Table heading
        Table.Builder mdReport = new Table.Builder().addRow("Line No.", "Line", "Rule Violated", "Category",
                "Severity", "Rule Type", "Software Quality Attributes", "Improvement Suggestion");

        Table.Builder consoleReport = new Table.Builder().addRow("Line No.", "Line", "Rule Violated");

        for (Violation v : violationList) {
            IRestRule rule = v.getRule();
            mdReport.addRow(v.getLineViolation(), v.getKeyViolation(), rule.getTitle(), rule.getCategory(),
                    rule.getSeverityType(), rule.getRuleType().toString().replace("[", "").replace("]", ""),
                    rule.getRuleSoftwareQualityAttribute().toString().replace("[", "").replace("]", ""),
                    v.getImprovementSuggestion());
            consoleReport.addRow(v.getLineViolation(), v.getKeyViolation(), rule.getTitle());
        }

        sbConsoleReport.append(consoleReport.build());

        // print to console
        System.out.println(sbConsoleReport);

        try {
            // get current time
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();

            // create directory
            Path path = Path.of(OUTPUT_DIR);
            if (!Files.isDirectory(path)) {
                path = Files.createDirectory(path);
            }

            // write file
            String filename = "Report_" + dtf.format(now) + ".md";
            Path file = Files.createFile(path.resolve(filename));
            BufferedWriter bw = Files.newBufferedWriter(file);
            PrintWriter printWriter = new PrintWriter(bw);
            sbMDReport.append(mdReport.build());
            printWriter.print(sbMDReport);

            // notification
            System.out.println("----------------------------------------------");
            System.out.println("\nIn total " + violationList.size() + " rule violations were found");
            System.out.println("--> The detailed report can be found here: " + path.toAbsolutePath());

            printWriter.close();
            bw.close();
        } catch (IOException e) {
            logger.severe("Error on writing report: " + e.getMessage());
        }
    }
}
