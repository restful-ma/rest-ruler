package cli.rule.rules;

import cli.analyzer.RestAnalyzer;
import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import cli.utility.Output;

import io.swagger.v3.oas.models.OpenAPI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class FileExtensionRule implements IRestRule {
    private static final String PATH_TO_FILE_EXTENSIONS = "src/main/java/cli/docs/file_extensions.txt";
    private static final String TITLE = "File extensions should not be included in URIs";
    private static final RuleCategory CATEGORY = RuleCategory.URIS;
    private static final RuleSeverity SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List
            .of(RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private static final List<Violation> violationList = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private boolean isActive;

    public FileExtensionRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public RuleCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return SEVERITY;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return SOFTWARE_QUALITY_ATTRIBUTES;
    }

    @Override
    public boolean getIsActive() {
        return this.isActive;
    }

    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Method used to check for any violations of the implemented rule
     *
     * @param openAPI structured Object containing a representation of a OpenAPI
     *                specification
     * @return List of Violations of the executing Rule
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        Set<String> paths = new HashSet<>(openAPI.getPaths().keySet());

        int curPath = 1;
        int totalPaths = paths.size();
        for (String path : paths) {
            Output.progressPercentage(curPath, totalPaths);
            curPath++;
            String[] segments = path.split("/");
            for (String segment : segments){
                String segmentWithoutParameters = segment.replaceAll("\\{" + ".*" + "\\}", "").toUpperCase();

                // Reads file that contains about 838 file extensions
                try (BufferedReader br = new BufferedReader(new FileReader(PATH_TO_FILE_EXTENSIONS))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // Stops when one violation is found --> rest of extensions are not checked
                        if (segmentWithoutParameters.endsWith("." + line.toUpperCase())) {
                            violationList.add(new Violation(this, RestAnalyzer.locMapper.getLOCOfPath(path),
                                    "To indicate the format " + "of a message's entity body (" + line + ") rely on the "
                                            + "media type inside the Content-Type header.",
                                    path, ErrorMessage.FILE_EXTENSION));
                            break;
                        }
                    }
                } catch (IOException e) {
                    logger.severe("Error on trying to read the file extension file: " + e.getMessage());
                }
            }
        }
        return violationList;
    }
}
