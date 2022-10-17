package rest.studentproject.utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {
    private static final String TEST_PROPS = "{test=test}";
    private static final String TEST_PROPS_ELE = "test";
    private String curPathConfig = "";
    private static final String VALID_PATH_TEST_CONFIG = "src/test/java/rest/studentproject/utility/testConfig.properties";
    private Config config;
    private File file;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @BeforeEach
    public void createNewConfig() {
        this.config = new Config();
        try {
            Field path = Class.forName(this.config.getClass().getName()).getDeclaredField("configFilePath");
            path.setAccessible(true);
            path.set(this.config, curPathConfig);

            this.file = new File(curPathConfig);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void deleteConfig() {
        try {
            Files.deleteIfExists(
                    Paths.get(VALID_PATH_TEST_CONFIG));
        } catch (IOException e) {
            String message = String.format("Error on deleting the test config file: %s", e.getMessage());
            logger.severe(message);
        }
    }

    @Test
    @DisplayName("Test if config file is created")
    void testConfigFileCreation() {
        this.config.getConfig();
        Properties props = this.config.getConfig();
        assertEquals(null, props);

        // no config file --> will be created by default and should be empty
        this.curPathConfig = VALID_PATH_TEST_CONFIG;
        createNewConfig();
        props = this.config.getConfig();
        assertTrue(this.file.exists());
        assertTrue(props.isEmpty());

    }

    @Test
    @DisplayName("Test adding to config file")
    void addToConfig() {
        this.curPathConfig = VALID_PATH_TEST_CONFIG;
        createNewConfig();
        Map<String, String> map = Map.of(TEST_PROPS_ELE, TEST_PROPS_ELE);

        this.config.addToConfig(map);

        Properties props = this.config.getConfig();
        assertEquals(TEST_PROPS, props.toString());

        String testEle = (String) props.get(TEST_PROPS_ELE);
        assertEquals(TEST_PROPS_ELE, testEle);
    }
}
