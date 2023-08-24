package cli.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ActiveRulesTest {

    private static final String VALID_RULE_FOLDER_PATH = "src/main/java/cli/rule/rules" ;
    private ActiveRules activeRules;

    @Test
    @DisplayName("Test that checks if the url is valid")
    void getRuleObjects(){
        this.activeRules = new ActiveRules();

        File file = new File(VALID_RULE_FOLDER_PATH);

        List<IRestRule> ruleObjects = activeRules.getAllRuleObjects();

        assertEquals(file.list().length, ruleObjects.size());
    }
}
