package rest.studentproject.rule.separatorTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.studentproject.analyzer.RestAnalyzer;
import rest.studentproject.rule.rules.SeparatorRule;
import rest.studentproject.rule.Violation;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SeparatorRuleTest {
    SeparatorRule separatorRule;
    RestAnalyzer restAnalyzer;

    //relative path to test JSON file
    private static final String PATH = "/src/test/java/rest/studentproject/rule/separatorTests/separator_test.json";


    @BeforeEach
    void setUp() {
        restAnalyzer = new RestAnalyzer(Paths.get("").toAbsolutePath().toString() + PATH);
        separatorRule = new SeparatorRule(true);

    }


    /**
     * This Test checks if a JSON file can be correctly opened, read and have violations in the file detected
     */
    @Test
    @DisplayName("Separator Rule detects violations in an OPEN API JSON file")
    void checkViolation() {

        List<Violation> violationList = restAnalyzer.runAnalyse(List.of(this.separatorRule), false);

        assertFalse(violationList.isEmpty());
        assertEquals(6, violationList.size());

    }


    /**
     * This Test checks if a set of erroneous inputs can be detected by the SeparatorRule.checkSeparator Method.
     * indirectly also tests the private method SeparatorRule.countPossibleSeparators
     */
    @Test
    @DisplayName("Separator Rule detects different kinds of Separators used instead of a forward slash '/' ")
    void checkSeparator() {

        //generate faulty inputs
        Set<String> input = new HashSet<>();
        input.add(":v1:destination_definition_specifications:get");
        input.add("\\v1\\destination_definitions\\get");
        input.add(".v1.destination_definitions.list_latest");
        input.add(";v1;destinations;update");
        input.add("-v1-{destinations}-update");

        input.add("/v1={destination_definitions}=create");
        input.add("=v1={destination_definitions}=create");
        input.add("=v1=destination_definitions=create");

        input.add("/");

        //run Method under test
        runMethodUnderTest(input);

    }

    @Test
    @DisplayName("SeparatorRule identifies inputs with illegal Symbols such as '#' and '?' for paths")
    void checkWronglyUsedCharacters() {
        Set<String> input = new HashSet<>();

        input.add("/v1#{destination_definitions}#create");
        input.add("#v1#{destination_definitions}#create");


        input.add("/v1?{destination_definitions}?create");
        input.add("?v1?{destination_definitions}?create");

        //edge case: '?' not allowed
        input.add("/get=Pets?Location");

        input.add("#get=Pets?Location");

        //run Method under test
        runMethodUnderTest(input);
    }

    @Test
    @DisplayName("SeparatorRule identifies partly faulty input paths")
    void checkPartlyFaultyPaths() {
        Set<String> input = new HashSet<>();
        input.add("/v1:destination_definition_specifications:get");
        input.add("/v1:destination_definition_specifications:get:");

        input.add("/v1\\{destination_definitions}\\get");
        input.add("/v1\\{destination_definitions}\\get\\");

        input.add("/v1#{destination_definitions}#create");

        input.add(".v1.destination_definitions/list_latest");

        input.add(";v1/destinations/update");
        input.add(";v1/destinations/update/");

        input.add("/v1/{destinations}:update");
        input.add("\\v1/{destinations}\\update");
        input.add(";v1/destinations/update:");

        //run Method under test
        runMethodUnderTest(input);

    }

    private void runMethodUnderTest(Set<String> input) {
        //run method under test
        List<Violation> violationList = separatorRule.checkSeparator(input);

        assertFalse(violationList.isEmpty());

        //check if each faulty input is detected
        for (Violation v : violationList) {
            assertTrue(input.contains(v.getKeyViolation()));
        }

        //final check if all are found
        assertEquals(input.size(), violationList.size());
    }
}