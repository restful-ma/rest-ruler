package rest.studentproject.analyzer;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void validMapping() throws MalformedURLException {
        String jsonURL = "src/test/java/rest/studentproject/validopenapi/validOpenAPI.json";
        this.openAPI = new OpenAPIParser().readLocation(jsonURL, null, null).getOpenAPI();
        this.mapper = new LOCMapper(openAPI, jsonURL);
        this.mapper.mapOpenAPIKeysToLOC();
        assertEquals(38, this.mapper.getLOCOfPath("/quotes"));
        assertEquals(2, this.mapper.getOpenAPIKeyLOC().get("paths").size());
        assertEquals(58, this.mapper.getOpenAPIKeyLOC().get("paths").get("/symbols"));

    }

    @Test
    void fileNotFoundInput() throws MalformedURLException {
        String fileNotFoundURL = "/asd/asd.json";
        this.openAPI = new OpenAPIParser().readLocation(fileNotFoundURL, null, null).getOpenAPI();
        this.mapper = new LOCMapper(openAPI, fileNotFoundURL);
        this.mapper.mapOpenAPIKeysToLOC();
        assertEquals("File not found!".trim(), errContent.toString().trim());

    }

    @Test
    void wrongFileFormatInput() throws MalformedURLException {
        String wrongFileFormat = "src/test/java/rest/studentproject/analyzer/res/openAPIWrongFormat.pdf";
        this.openAPI = new OpenAPIParser().readLocation(wrongFileFormat, null, null).getOpenAPI();
        this.mapper = new LOCMapper(openAPI, wrongFileFormat);
        this.mapper.mapOpenAPIKeysToLOC();
        assertEquals("Wrong file format!".trim(), errContent.toString().trim());
    }


}