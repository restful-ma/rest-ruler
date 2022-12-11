package cli.rule;

import cli.rule.constants.RuleCategory;
import cli.rule.constants.RuleSeverity;
import cli.rule.constants.RuleSoftwareQualityAttribute;
import cli.rule.constants.RuleType;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

public interface IRestRule {

    String getTitle();

    RuleCategory getCategory();

    RuleSeverity getSeverityType();

    List<RuleType> getRuleType();

    List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute();

    boolean getIsActive();

    void setIsActive(boolean isActive);

    /**
     * Method used to check for any violations of the implemented rule
     *
     * @param openAPI structured Object containing a representation of a OpenAPI specification
     * @return List of Violations of the executing Rule
     */
    List<Violation> checkViolation(OpenAPI openAPI);
}
