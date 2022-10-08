package rest.studentproject.rule.rules;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Violation;
import rest.studentproject.rule.constants.*;
import rest.studentproject.utility.Output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TunnelingRule implements IRestRule {

    private static final String TITLE = "GET and POST must not be used to tunnel other request methods";
    private static final RuleCategory RULE_CATEGORY = RuleCategory.HTTP;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.CRITICAL;
    private static final List<RuleType> RULE_TYPE = List.of(RuleType.STATIC);
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(
            RuleSoftwareQualityAttribute.MAINTAINABILITY, RuleSoftwareQualityAttribute.COMPATIBILITY,
            RuleSoftwareQualityAttribute.FUNCTIONAL_SUITABILITY, RuleSoftwareQualityAttribute.USABILITY);
    private boolean isActive;

    public TunnelingRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public RuleCategory getCategory() {
        return RULE_CATEGORY;
    }

    @Override
    public RuleSeverity getSeverityType() {
        return RULE_SEVERITY;
    }

    @Override
    public List<RuleType> getRuleType() {
        return RULE_TYPE;
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

    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();
        Paths paths = openAPI.getPaths();

        // check CRUD violations for further violations
        CRUDRule crudRule = new CRUDRule(true);

        List<Violation> crudViolations = crudRule.checkViolation(openAPI);

        int curViolation = 1;
        int totalViolations = crudViolations.size();
        for (Violation crudViolation : crudViolations) {
            Output.progressPercentage(curViolation, totalViolations);
            curViolation++;

            Violation tunnelingViolation = checkCRUDForTunneling(crudViolation, paths);

            if (tunnelingViolation != null)
                violations.add(tunnelingViolation);

        }

        return violations;
    }

    /**
     * checks CRUD violations if there is also a potential Tunneling violation
     *
     * @param violation CRUD violation
     * @param paths     representation of all paths
     * @return
     */
    private Violation checkCRUDForTunneling(Violation violation, Paths paths) {

        String requestType;

        // iterate through all the path representations
        for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
            String path = entry.getKey();
            PathItem item = entry.getValue();

            // find the path where a CRUD violation was found
            if (!path.equals(violation.getKeyViolation()))
                continue;
            requestType = getRequestType(item);

            // key word search in path to see if request type is found in the path
            if (requestType != null && checkPathforrequesttype(path, requestType))
                return new Violation(this, violation.getLineViolation(), ImprovementSuggestion.TUNNELING,
                        violation.getKeyViolation(), ErrorMessage.TUNNELING);

        }
        return null;
    }

    /**
     * retrieves the request type for a given request
     *
     * @param item a request representation
     * @return
     */
    private String getRequestType(PathItem item) {

        if (item.getGet() != null)
            return "get";
        if (item.getPost() != null)
            return "post";
        if (item.getDelete() != null)
            return "delete";
        if (item.getPut() != null)
            return "put";
        if (item.getPatch() != null)
            return "patch";
        if (item.getOptions() != null)
            return "options";
        if (item.getHead() != null)
            return "head";
        if (item.getTrace() != null)
            return "trace";

        // invalid request
        return null;

    }

    /**
     * checks if a http operation can be found in the path and then check if it
     * matches the operation type for the request
     *
     * @param path path of the request
     * @param type request operation type
     * @return
     */
    private boolean checkPathforrequesttype(String path, String type) {
        String[] requestypes = { "get", "post", "delete", "put", "patch", "options", "head", "trace" };

        for (String requestype : requestypes) {

            if (path.contains(requestype) && !requestype.equals(type))
                return true;

        }
        return false;
    }
}
