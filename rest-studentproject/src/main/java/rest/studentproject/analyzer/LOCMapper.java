package rest.studentproject.analyzer;

import io.swagger.v3.oas.models.OpenAPI;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LOCMapper {

    private final Map<String, Integer> pathMap = new HashMap<>();
    private final Map<String, Map<String, Integer>> keyLOCMap = new HashMap<>();
    private final OpenAPI openAPI;
    private final String filePath;

    public LOCMapper(OpenAPI openAPI, String filePath) throws MalformedURLException {

        this.openAPI = openAPI;
        this.filePath = filePath;

        mapOpenAPIKeysToLOC();

    }

    private void mapOpenAPIKeysToLOC() throws MalformedURLException {
        boolean isURL = false;
        isURL = this.filePath.startsWith("http");

        URL url = new URL(this.filePath);
        try (BufferedReader br = new BufferedReader(isURL ? new InputStreamReader(url.openStream()) : new FileReader(this.filePath))) {
            String line;
            int currentLine = 0;

            while ((line = br.readLine()) != null) {
                currentLine++;
                mapPaths(line, currentLine);
            }
            this.keyLOCMap.put("paths", this.pathMap);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mapPaths(String line, int currentLine) {
        if (!this.filePath.endsWith("json") && !this.filePath.endsWith("yaml")) System.out.println("Wrong file format");

        for (String keyPath : this.openAPI.getPaths().keySet()) {
            String pathWithQuotes = "\"" + keyPath + "\"";
            String pathWithColon = keyPath + ":";

            if (!line.contains(pathWithQuotes) && !line.contains(pathWithColon)) continue;

            this.pathMap.put(keyPath, currentLine);
        }
    }

    public Map<String, Map<String, Integer>> getOpenAPIKeyLOC() {
        return this.keyLOCMap;
    }

    public int getLOCOfPath(String keyPath) {
        return this.pathMap.get(keyPath);
    }

}
