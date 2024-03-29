package cli.rule;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import cli.utility.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

/**
 * All rule objects as well as the state of the rules (active or disabled) can
 * be accessed here.
 */
public class ActiveRules {
    private static final String PATH_TO_RULES = "cli.rule.rules";
    private static final String METHOD_NAME = "getTitle";
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * With the help of reflections, all rule objects are created from the package
     * {@link #PATH_TO_RULES}. It is
     * sufficient to implement only a new rule, which is then recognised by the
     * method directly. The method gets the
     * state of the rule (active or disabled) from the config file.
     *
     * @return the list of IRestRule objects of the implemented rules
     */
    public List<IRestRule> getAllRuleObjects() {
        List<IRestRule> rules = new ArrayList<>();

        Reflections reflections = new Reflections(PATH_TO_RULES, Scanners.SubTypes.filterResultsBy(s -> true));
        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);

        Config config = new Config();
        Properties prop = config.getConfig();

        boolean propsNull = false;

        for (Class<?> ruleClass : allClasses) {
            // Classes with the $1, $2 holds the anonymous inner classes, we are not
            // interested on them.
            if (ruleClass.getName().contains("$"))
                continue;
            try {
                Constructor<?> ruleConstructor = ruleClass.getConstructor(boolean.class);
                Object classObj = ruleConstructor.newInstance(true);
                Method ruleMethod = ruleClass.getDeclaredMethod(METHOD_NAME);

                String ruleTitle = ruleMethod.invoke(classObj).toString().replace(" ", "");

                IRestRule classObject;

                // If there is no configuration file or the rule is not defined in the
                // configuration file, the rule object will be enabled by default in the
                // analysis. Otherwise, the status of the rule is taken from the config.
                if (prop == null || prop.getProperty(ruleTitle) == null) {
                    classObject = (IRestRule) ruleConstructor.newInstance(true);
                    propsNull = true;
                } else {
                    classObject = (IRestRule) ruleConstructor
                            .newInstance(Boolean.parseBoolean(Objects.requireNonNull(prop).getProperty(ruleTitle)));
                }
                rules.add(classObject);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException
                    | IllegalAccessException e) {
                System.out.println(e);
                logger.severe("Exception when trying to get the rule objects or reading the config: " + e.getMessage());
            }
        }

        if (propsNull) {
            config.addToConfig(addDefaultValuesToConfig(rules));

        }
        return rules;
    }

    private Map<String, String> addDefaultValuesToConfig(List<IRestRule> rules) {
        Map<String, String> ruleStatus = new HashMap<>();
        for (IRestRule rule : rules) {
            ruleStatus.put(rule.getTitle().replaceAll("\\s+", ""), "true");
        }
        return ruleStatus;
    }

}
