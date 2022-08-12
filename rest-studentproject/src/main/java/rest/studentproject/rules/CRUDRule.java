package rest.studentproject.rules;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.analyzer.LOCMapper;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rules.constants.RuleCategory;
import rest.studentproject.rules.constants.RuleSeverity;
import rest.studentproject.rules.constants.RuleSoftwareQualityAttribute;
import rest.studentproject.rules.constants.RuleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CRUDRule implements IRestRule {
    private static final String TITLE = "";
    private static final RuleCategory CATEGORY = RuleCategory.URIS;
    private static final RuleType TYPE = RuleType.STATIC;
    private static final RuleSeverity SEVERITY = RuleSeverity.ERROR;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTE = Arrays.asList(RuleSoftwareQualityAttribute.USABILITY, RuleSoftwareQualityAttribute.MAINTAINABILITY);
    private List<Violation> violationList = new ArrayList<>();
    private boolean isActive;
    private static LOCMapper locMapper;


    public void CRUDRule(boolean isActive) {
        this.isActive = isActive;
        locMapper = RestAnalyzer.locMapper;
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
     * @param openAPI
     * @return
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        return null;
    }
}
