package rest.studentproject.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {

    private String configFilePath = "src\\main" + "\\java\\rest\\studentproject\\docs\\config" +
            ".properties";
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean isConfigFileCreated = false;

    /**
     * Gets the config.properties from {@link #configFilePath}
     *
     * @return if there is a config returns the properties, else returns null
     */
    public Properties getConfig() {
        try (FileInputStream propsInput = new FileInputStream(configFilePath)) {
            Properties prop = new Properties();
            prop.load(propsInput);
            return prop;
        } catch (IOException e) {
            logger.info("Error when trying to read the config file: " + e.getMessage());
            createConfigFile();
            if (isConfigFileCreated) {
                return getConfig();
            }
            return null;
        }
    }

    /**
     * Creates a new config file at {@link #configFilePath}
     */
    private void createConfigFile() {
        if (!new File(configFilePath).exists()) {
            logger.info("Config file cannot be found. New file is created.");
            File file = new File(configFilePath);
            
            try {
                if (file.createNewFile()) {
                    logger.info("Config file created");
                    isConfigFileCreated = true;
                }

            } catch (IOException ex) {
                logger.info("Cannot create config file: " + ex.getMessage());
            }
        }
    }

    /**
     * Adds a key value pair to the config.properties {@link #configFilePath}.
     * <p>
     * Rules are saved here too (Key is error msg of rule without spaces; value is
     * boolean as string)
     *
     * @param keyValues key value pair that needs to be stored
     */
    public void addToConfig(Map<String, String> keyValues) {
        Properties prop = getConfig();

        if (prop == null) {
            logger.severe("Properties not found!");
            return;
        }

        try (FileOutputStream out = new FileOutputStream(configFilePath)) {
            for (Map.Entry<String, String> keyValue : keyValues.entrySet()) {
                prop.setProperty(keyValue.getKey(), keyValue.getValue());
            }

            prop.store(out, null);
        } catch (IOException e) {
            logger.severe("Exception: " + e.getMessage());
        }
    }

}
