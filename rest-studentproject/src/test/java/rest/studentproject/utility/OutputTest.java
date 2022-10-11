package rest.studentproject.utility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class OutputTest {
    private static final String EMPTY_PATH = "";
    private static final String NO_URL = "src/test/java";
    private static final String VALID_URL = "https://google.com";
    private static final String EXISTING_FILE_PATH = "src/test/java/rest/studentproject/utility/OutputTest.java";
    private static final String NON_EXISTING_FILE_PATH = "src/test/java/rest/studentproject/utility/OutputTest1.java";
    private static final String DIR_PATH = "src/test/java/rest/studentproject/utility";
    private Output output;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @BeforeEach
    public void setUp() {
        this.output = new Output();
    }

    @Test
    @DisplayName("Test that checks if the url is valid")
    void checkURL() {
        Method method;
        boolean validUR;
        try {
            method = this.output.getClass().getDeclaredMethod("checkURL", String.class);
            method.setAccessible(true);
            validUR = (boolean) method.invoke(this.output, NO_URL);
            assertFalse(validUR);

            validUR = (boolean) method.invoke(this.output, VALID_URL);
            assertTrue(validUR);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            String message = String.format("Error when invoking the checkURL() method: %s", e.getMessage());
            logger.severe(message);
        }
    }

    @Test
    @DisplayName("Test that checks if a file exists and that it is not a directory.")
    void checkFileLocation() {
        Method method;
        boolean validUR;
        try {
            method = this.output.getClass().getDeclaredMethod("checkFileLocation", String.class);
            method.setAccessible(true);
            validUR = (boolean) method.invoke(this.output, NON_EXISTING_FILE_PATH);
            assertFalse(validUR);

            validUR = (boolean) method.invoke(this.output, EXISTING_FILE_PATH);
            assertTrue(validUR);

            validUR = (boolean) method.invoke(this.output, NON_EXISTING_FILE_PATH);
            assertFalse(validUR);

            validUR = (boolean) method.invoke(this.output, DIR_PATH);
            assertFalse(validUR);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            String message = String.format("Error when invoking the checkFileLocation() method: %s", e.getMessage());
            logger.severe(message);
        }
    }

    @Test
    @DisplayName("Test that checks if the server is reachable.")
    void pingURL() {
        Method method;
        boolean output;
        try {
            method = this.output.getClass().getDeclaredMethod("pingURL", String.class);
            method.setAccessible(true);
            output = (boolean) method.invoke(this.output, VALID_URL);
            assertTrue(output);

            output = (boolean) method.invoke(this.output, NO_URL);
            assertFalse(output);

            output = (boolean) method.invoke(this.output, EMPTY_PATH);
            assertFalse(output);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            String message = String.format("Error when invoking the pingURL() method: %s", e.getMessage());
            logger.severe(message);
        }
    }
}
