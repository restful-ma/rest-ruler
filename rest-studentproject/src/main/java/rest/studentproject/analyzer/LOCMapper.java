package rest.studentproject.analyzer;

import io.swagger.v3.oas.models.OpenAPI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps the keys from the parsed OpenAPI object to the original json or yaml line of code.
 */
public class LOCMapper {

    private final Map<String, Integer> pathMap = new HashMap<>();
    private final Map<String, Map<String, Integer>> keyLOCMap = new HashMap<>();
    private final OpenAPI openAPI;
    private final String filePath;

    /**
     * Calls the mapper to map all keys from the parsed OpenAPI object to the line of code from the original
     * json/yaml file.
     *
     * @param openAPI  The parsed OpenAPI object.
     * @param filePath Path to the file that will be parsed and checked against the rules.
     */
    public LOCMapper(OpenAPI openAPI, String filePath) {
        this.openAPI = openAPI;
        this.filePath = filePath;
    }

    /**
     * Reads the file and goes through every line.
     * <p>
     * Goes to all lines and checks if the line contains a path.
     */
    public void mapOpenAPIKeysToLOC() {
        boolean isURL = this.filePath.startsWith("http");
        if (!isURL && !this.filePath.endsWith("json") && !this.filePath.endsWith("yaml")) {
            System.err.println("Wrong file format!");
            return;
        }

        try (BufferedReader br = new BufferedReader(isURL ?
                new InputStreamReader(new URL(this.filePath).openStream()) : new FileReader(this.filePath))) {
            String line;
            int currentLine = 0;

            while ((line = br.readLine()) != null) {
                currentLine++;
                mapPaths(line, currentLine);
            }
            this.keyLOCMap.put("paths", this.pathMap);
        } catch (IOException e) {
            System.err.println("Issues appeared when trying to read the file! Error message: " + e.getMessage());

        }
    }

    /**
     * Checks if the given line contains a path. if it does, the path is saved with the line of code in a map.
     *
     * @param line        the current line from the original json/yaml file
     * @param currentLine the current line of code from the original json/yaml file
     */
    private void mapPaths(String line, int currentLine) {
        for (String keyPath : this.openAPI.getPaths().keySet()) {
            // To search for the path in the json file
            String pathWithDoubleQuotes = "\"" + keyPath + "\"";
            String pathWithSingleQuotes = "'" + keyPath + "'";
            // To search for the path in the yaml file
            String pathWithColon = keyPath + ":";
            String pathWithColonAndSingleQuotes = keyPath + "':";
            String pathWithColonAndDoubleQuotes = keyPath + "\":";

            if (!line.contains(pathWithDoubleQuotes) && !line.contains(pathWithSingleQuotes) && !line.contains(pathWithColon) && !line.contains(pathWithColonAndSingleQuotes) && !line.contains(pathWithColonAndDoubleQuotes))
                continue;

            this.pathMap.put(keyPath, currentLine);
        }
    }

    /**
     * @return the whole map of the keys. Map of a map. The inside map is for example the path keys with the loc.
     */
    public Map<String, Map<String, Integer>> getOpenAPIKeyLOC() {
        return this.keyLOCMap;
    }

    /**
     * @param keyPath the path for that the line of code is needed.
     * @return the line of code from the given path.
     */
    public int getLOCOfPath(String keyPath) {

        //case: path doesnt exist
        if (this.pathMap.get(keyPath) == null) {
            return 0;
        }

        return this.pathMap.get(keyPath);
    }

}
