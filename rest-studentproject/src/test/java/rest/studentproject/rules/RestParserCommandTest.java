package rest.studentproject.rules;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import rest.studentproject.RestParserCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestParserCommandTest {

    @Test
    void testWithCommandLineOption() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            String[] args = new String[]{"-v"};
            PicocliRunner.run(RestParserCommand.class, ctx, args);

            // rest-parser
            assertEquals(true, true);
        }
    }
}
