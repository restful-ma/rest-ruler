package rest.studentproject.rules;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnderscoreRuleTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkViolation() {
        UnderscoreRule underscoreRule = new UnderscoreRule(true);
//        underscoreRule.checkViolation()

    }
}