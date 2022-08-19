package rest.studentproject;

import rest.studentproject.rules.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * All rules that are implemented need to be added here in this class. If the rule should be analyzed the rule object
 * needs as parameter true.
 */
public class ActiveRules {
    private static final String CONFIG_FILE_PATH = "src\\main" + "\\java\\rest\\studentproject\\docs\\config.properties";
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Add all rule objects here that are implemented
     *
     * @return the list of implemented rules
     */
    public List<IRestRule> getActiveRules() {
        Properties prop = getConfig();

        if (prop == null)
            return Arrays.asList(new UnderscoreRule(true), new CRUDRule(false), new LowercaseRule(true),
                    new HyphensRule(true), new SeparatorRule(true));

        return Arrays.asList(new UnderscoreRule(Boolean.parseBoolean(prop.getProperty("Underscores(_)" +
                "shouldnotbeusedinURI"))), new CRUDRule(Boolean.parseBoolean(prop.getProperty(
                        "CRUDfunctionnamesshouldnotbeusedinURIs"))),
                new LowercaseRule(Boolean.parseBoolean(prop.getProperty("LowercaselettersshouldbepreferredinURIpaths"))), new HyphensRule(Boolean.parseBoolean(prop.getProperty("Hyphens(-)shouldbeusedtoimprovethereadabilityofURIs"))), new SeparatorRule(Boolean.parseBoolean(prop.getProperty("Forwardslashseparator(/)mustbeusedtoindicateahierarchicalrelationship"))));
    }

    public void setActiveRules(Map<String, Boolean> ruleSet) {
        Properties prop = getConfig();

        if (prop == null || ruleSet == null) return;

        try {
            FileOutputStream out = new FileOutputStream(CONFIG_FILE_PATH);
            for (Map.Entry<String, Boolean> rule : ruleSet.entrySet()) {
                prop.setProperty(rule.getKey(), rule.getValue().toString());
            }
            prop.store(out, null);
            out.close();
        } catch (IOException e) {
            logger.severe("Exception" + e.getMessage());
        }
    }

    private Properties getConfig() {
        FileInputStream propsInput = null;
        try {
            propsInput = new FileInputStream(CONFIG_FILE_PATH);
            Properties prop = new Properties();
            prop.load(propsInput);
            return prop;
        } catch (IOException e) {
            logger.severe("Error when trying to read the config file: " + e.getMessage());
            logger.info("Instead using the default values --> all rules are active.");
            return null;
        }
    }
}
