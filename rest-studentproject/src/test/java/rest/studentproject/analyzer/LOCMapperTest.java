package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LOCMapperTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private OpenAPI openAPI;
    private LOCMapper mapper;

    @BeforeEach
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    void validMapping() {
        String jsonURL = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
        this.openAPI = new OpenAPIParser().readLocation(jsonURL, null, null).getOpenAPI();
        this.mapper = new LOCMapper(openAPI, jsonURL);
        this.mapper.mapOpenAPIKeysToLOC();
        assertEquals(38, this.mapper.getLOCOfPath("/quotes"));
        assertEquals(4, this.mapper.getOpenAPIKeyLOC().get("paths").size());
        assertEquals(58, this.mapper.getOpenAPIKeyLOC().get("paths").get("/symbols"));
        assertEquals(89, this.mapper.getOpenAPIKeyLOC().get("paths").get("/"));
        assertEquals(120, this.mapper.getOpenAPIKeyLOC().get("paths").get("/adults/{get_adult_ID}/names"));
    }

    @Test
    void fileNotFoundInput() {
        String fileNotFoundURL = "/asd/asd.json";
        this.openAPI = new OpenAPIParser().readLocation(fileNotFoundURL, null, null).getOpenAPI();
        this.mapper = new LOCMapper(openAPI, fileNotFoundURL);
        this.mapper.mapOpenAPIKeysToLOC();
        System.out.println(fileNotFoundURL);
        System.out.println(errContent.toString());
        assertEquals(errContent.toString().trim().startsWith("Issues appeared when trying to read the file! " + "Error " + "message: " + FilenameUtils.separatorsToSystem(fileNotFoundURL)), true);
    }

    @Test
    void wrongFileFormatInput() {
        String wrongFileFormat = "src/test/java/rest/studentproject/analyzer/res/openAPIWrongFormat.pdf";
        this.openAPI = new OpenAPIParser().readLocation(wrongFileFormat, null, null).getOpenAPI();
        this.mapper = new LOCMapper(openAPI, wrongFileFormat);
        this.mapper.mapOpenAPIKeysToLOC();
        assertTrue(errContent.toString().trim().endsWith("Wrong file format!".trim()));
    }
}