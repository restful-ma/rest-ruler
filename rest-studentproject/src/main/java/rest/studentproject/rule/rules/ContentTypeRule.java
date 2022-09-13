package rest.studentproject.rule.rules;

import io.swagger.models.Response;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.RuleCategory;
import rest.studentproject.rule.constants.RuleSeverity;
import rest.studentproject.rule.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rule.constants.RuleType;

import java.nio.file.Path;
import java.util.*;

public class ContentTypeRule implements IRestRule {
    private static final String TITLE = "Content-Type must be used";
    private static final RuleCategory CATEGORY = RuleCategory.META;
    private static final RuleType TYPE = RuleType.STATIC;
    private static final RuleSeverity SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE =
            Arrays.asList(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY);
    private final List<Violation> violationList = new ArrayList<>();

    private boolean isActive;

    public ContentTypeRule(boolean isActive) {

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
    public RuleType getRuleType() {
        return TYPE;
    }

    @Override
    public List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute() {
        return SOFTWARE_QUALITY_ATTRIBUTE;
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
     * @param openAPI structured Object containing a representation of a OpenAPI specification
     * @return List of Violations of the executing Rule
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        Paths paths = openAPI.getPaths();

        for (PathItem path : paths.values()) {
            checkResponseContentType(path);
        }


        return null;
    }

    private void checkResponseContentType(PathItem path) {
        Operation getOp = path.getGet();
//        Operation deleteOp = path.getDelete();
//        Operation postOp = path.getPost();
//        Operation putOp = path.getPut();
//        Operation patchOp = path.getPatch();

        if (getOp != null) {
            System.out.println("path");
            System.out.println(path);
            ApiResponses responses = getOp.getResponses();
            System.out.println("responses");
            System.out.println(responses);
            for (ApiResponse response : responses.values()) {
                System.out.println("Inside response");
                Set<Map.Entry<String, MediaType>> mediaTypes = response.getContent().entrySet();
                for (Map.Entry<String, MediaType> mediaType : mediaTypes) {
                    System.out.println("MediaType");
                    System.out.println(mediaType.getKey());
                    System.out.println(mediaType.getValue());
                }
            }
        }


        //        System.out.println(path.);
    }

    private void checkRequestContentType() {

    }
}
