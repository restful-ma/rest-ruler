package cli.rule.rules;

import cli.rule.IRestRule;
import cli.rule.Violation;
import cli.rule.constants.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Rule "MUST contain API meta information"
 */
public class MetaInfoRule implements IRestRule {
    private static final String TITLE = "OpenAPI specification must contain required meta information";
    private static final RuleIdentifier RULE_IDENTIFIER = RuleIdentifier.META_INFO;
    private static final RuleCategory RULE_CATEGORY = RuleCategory.META;
    private static final RuleSeverity RULE_SEVERITY = RuleSeverity.WARNING;
    private static final List<RuleSoftwareQualityAttribute> SOFTWARE_QUALITY_ATTRIBUTES = List.of(
            RuleSoftwareQualityAttribute.MAINTAINABILITY,
            RuleSoftwareQualityAttribute.USABILITY
    );
    private boolean isActive;

    public MetaInfoRule(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public RuleIdentifier getIdentifier() {
        return RULE_IDENTIFIER;
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
     * Checks if there is a violation against the "MUST contain API meta information" rule
     * To conform to the rule, multiple fields within the "info" field of the API must be provided
     * The rule ensures an API's meta information is adequately documented
     *
     * @param openAPI the definition that will be checked against the rule.
     * @return the list of violations.
     */
    @Override
    public List<Violation> checkViolation(OpenAPI openAPI) {
        List<Violation> violations = new ArrayList<>();

        Info info = openAPI.getInfo();
        if (info == null) {
            violations.add(new Violation(this, 0, "Add an 'info' object to the OpenAPI specification", 
                "info", ErrorMessage.METAINFO));
            return violations;
        }

        if (info.getTitle() == null || info.getTitle().trim().isEmpty()) {
            violations.add(new Violation(this, 0, "Add a title to the info object", 
                "info.title", ErrorMessage.METAINFO));
        }

        if (info.getVersion() == null || info.getVersion().trim().isEmpty()) {
            violations.add(new Violation(this, 0, "Add a version to the info object", 
                "info.version", ErrorMessage.METAINFO));
        }

        if (info.getDescription() == null || info.getDescription().trim().isEmpty()) {
            violations.add(new Violation(this, 0, "Add a description to the info object", 
                "info.description", ErrorMessage.METAINFO));
        }

        Contact contact = info.getContact();
        if (contact == null) {
            violations.add(new Violation(this, 0, "Add a contact object to the info object", 
                "info.contact", ErrorMessage.METAINFO));
        } else {
            if (contact.getName() == null || contact.getName().trim().isEmpty()) {
                violations.add(new Violation(this, 0, "Add a name to the contact object", 
                    "info.contact.name", ErrorMessage.METAINFO));
            }
            if (contact.getUrl() == null || contact.getUrl().trim().isEmpty()) {
                violations.add(new Violation(this, 0, "Add a URL to the contact object", 
                    "info.contact.url", ErrorMessage.METAINFO));
            }
            if (contact.getEmail() == null || contact.getEmail().trim().isEmpty()) {
                violations.add(new Violation(this, 0, "Add an email to the contact object", 
                    "info.contact.email", ErrorMessage.METAINFO));
            }
        }

        return violations;
    }
} 