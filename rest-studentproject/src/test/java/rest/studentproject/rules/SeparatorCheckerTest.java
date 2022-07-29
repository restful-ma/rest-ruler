package rest.studentproject.rules;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SeparatorCheckerTest {

    //relative path to test JSON file
    private static final String PATH = "/src/test/java/rest/studentproject/rules/inputs/separator_test.json";

    /**
     * This Test checks if a JSON file can be correctly opene, read and have violations in the file detected
     */
    @Test
    void checkViolation() {

        //retrieve current work dir
        Path currentRelativePath = Paths.get("");
        String root = currentRelativePath.toAbsolutePath().toString();

        //open JSON file
        SwaggerParseResult swaggerParseResult = new OpenAPIParser().readLocation(root + PATH, null, null);
        OpenAPI openAPI = swaggerParseResult.getOpenAPI();

        //execute method under test
        SeparatorChecker separatorChecker = new SeparatorChecker();
        List<Violation> violationList = separatorChecker.checkViolation(openAPI);

        assertFalse(violationList.isEmpty());
        assertTrue(violationList.size() == 6);
    }


    /**
     * This Test checks if a set of erroneous inputs can be detected by the SeparatorChecker.checkSeparator Method.
     * indirectly also tests the private method SeparatorChecker.countPossibleSeparators
     */
    @Test
    void checkSeparator() {

        //generate faulty inputs
        Set<String> input = new HashSet<>();
        input.add(":v1:destination_definition_specifications:get");
        input.add("\\v1\\destination_definitions\\get");
        input.add("#v1#destination_definitions#create");
        input.add("-v1-destination_definitions-list");
        input.add(".v1.destination_definitions.list_latest");
        input.add(";v1;destinations;update");

        //run method under test
        SeparatorChecker separatorChecker = new SeparatorChecker();
        List<Violation> violationList = separatorChecker.checkSeparator(input);

        assertFalse(violationList.isEmpty());

        //check if each faulty input is detected
        for (Violation v:violationList) {
            assertTrue(input.contains(v.getKeyViolation()));
        }

        //final check if all are found
        assertTrue(violationList.size() == input.size());




    }
}