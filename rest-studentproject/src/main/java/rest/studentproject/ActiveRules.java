package rest.studentproject;

import rest.studentproject.rules.*;

import java.util.Arrays;
import java.util.List;

/**
 * All rules that are implemented need to be added here in this class. If the rule should be analyzed the rule object needs as parameter true.
 */
public class ActiveRules {

    /**
     * Add all rule objects here that are implemented
     *
     * @return the list of implemented rules
     */
    public List<IRestRule> getActiveRules() {
        return Arrays.asList(new UnderscoreRule(true), new LowercaseRule(true), new HyphensRule(true), new SeparatorRule(true), new GetResourceRule(true));
    }
}
