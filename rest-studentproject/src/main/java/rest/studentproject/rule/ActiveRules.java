package rest.studentproject.rule;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;


/**
 * All rule objects as well as the state of the rules (active or disabled) can be accessed here.
 */
public class ActiveRules {
    private static final String CONFIG_FILE_PATH = "src\\main" + "\\java\\rest\\studentproject\\docs\\config" +
            ".properties";
    private static final String PATH_TO_RULES = "rest.studentproject.rule.rules";
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String METHOD_NAME = "getTitle";

    /**
     * With the help of reflections, all rule objects are created from the package {@link #PATH_TO_RULES}. It is
     * sufficient to
     * implement only a new rule, which is then recognised by the method directly. The method gets the state of the
     * rule (active or disabled) from the config file from {@link #CONFIG_FILE_PATH}.
     *
     * @return the list of IRestRule objects of the implemented rules
     */
    public List<IRestRule> getAllRuleObjects() {
        List<IRestRule> rules = new ArrayList<>();

        Reflections reflections = new Reflections(PATH_TO_RULES, Scanners.SubTypes.filterResultsBy(s -> true));
        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);

        Properties prop = getConfig();

        for (Class<?> ruleClass : allClasses) {
            try {
                Constructor<?> ruleConstructor = ruleClass.getConstructor(boolean.class);
                Object classObj = ruleConstructor.newInstance(true);
                Method ruleMethod = ruleClass.getDeclaredMethod(METHOD_NAME);

                String ruleTitle = ruleMethod.invoke(classObj).toString().replace(" ", "");

                IRestRule classObject;

                // If there is no configuration file or the rule is not defined in the configuration file, the rule
                // object will be enabled by default in the analysis. Otherwise, the status of the rule is taken from
                // the config.
                if (prop == null || prop.getProperty(ruleTitle) == null) {
                    classObject = (IRestRule) ruleConstructor.newInstance(true);
                } else {
                    classObject =
                            (IRestRule) ruleConstructor.newInstance(Boolean.parseBoolean(Objects.requireNonNull(prop).getProperty(ruleTitle)));
                }
                rules.add(classObject);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                logger.severe("Exception when trying to read the config.property: " + e.getMessage());
            }
        }
        return rules;
    }

    /**
     * Sets the state of the rule (active or disabled) in the config file.
     *
     * @param ruleSet a map with the rule name (without spaces) and the state of the rule (active --> true or
     *                disabled --> false)
     */
    public void setActiveRules(Map<String, Boolean> ruleSet) {
        Properties prop = getConfig();

        if (prop == null) {
            logger.severe("Properties not found!");
            return;
        }

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE_PATH)) {
            for (Map.Entry<String, Boolean> rule : ruleSet.entrySet()) {
                prop.setProperty(rule.getKey(), rule.getValue().toString());
            }

            prop.store(out, null);
        } catch (IOException e) {
            logger.severe("Exception: " + e.getMessage());
        }
    }

    /**
     * Gets the config.properties from {@link #CONFIG_FILE_PATH}
     *
     * @return if there is a config returns the properties, else returns null
     */
    private Properties getConfig() {
        try (FileInputStream propsInput = new FileInputStream(CONFIG_FILE_PATH)) {
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
