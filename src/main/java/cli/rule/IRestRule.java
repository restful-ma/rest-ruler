package cli.rule;

import cli.rule.constants.RuleCategory;
import cli.rule.constants.RuleSeverity;
import cli.rule.constants.RuleIdentifier;
import cli.rule.constants.RuleSoftwareQualityAttribute;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

public interface IRestRule {

    String getTitle();

    RuleIdentifier getIdentifier();

    RuleCategory getCategory();

    RuleSeverity getSeverityType();

    List<RuleSoftwareQualityAttribute> getRuleSoftwareQualityAttribute();

    boolean getIsActive();

    void setIsActive(boolean isActive);
    
    /**
     * Method used to determine whether the rule will use an LLM-based approach during evaluation.
     * This method is only used by rules that actually have an LLM component, so not every rule needs to implement a body for this.
     *
     * @param enableLLM determines whether LLM usage is enabled or not
     */
    default void setEnableLLM(boolean enableLLM) {}

    /**
     * Method used to check for any violations of the implemented rule
     *
     * @param openAPI structured Object containing a representation of a OpenAPI specification
     * @return List of Violations of the executing Rule
     */
    List<Violation> checkViolation(OpenAPI openAPI);
}
