package rest.studentproject.analyzer;

import io.swagger.v3.oas.models.OpenAPI;

import java.io.*;
import java.net.MalformedURLException;
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
     * Calls the mapper to map all keys from the parsed OpenAPI object to the line of code from the original json/yaml file.
     *
     * @param openAPI  The parsed OpenAPI object.
     * @param filePath Path to the file that will be parsed and checked against the rules.
     * @throws MalformedURLException If the file is not found.
     */
    public LOCMapper(OpenAPI openAPI, String filePath) {
        this.openAPI = openAPI;
        this.filePath = filePath;
    }

    /**
     * Reads the file and goes through every line.
     * <p>
     * Goes to all lines and checks if the line contains a path.
     *
     * @throws MalformedURLException
     */
    public void mapOpenAPIKeysToLOC() throws MalformedURLException {
        boolean isURL = this.filePath.startsWith("http");
        if (!this.filePath.endsWith("json") && !this.filePath.endsWith("yaml")) {
            System.err.println("Wrong file format!");
            return;
        }

        try (BufferedReader br = new BufferedReader(isURL ? new InputStreamReader(new URL(this.filePath).openStream()) : new FileReader(this.filePath))) {
            String line;
            int currentLine = 0;

            while ((line = br.readLine()) != null) {
                currentLine++;
                mapPaths(line, currentLine);
            }
            this.keyLOCMap.put("paths", this.pathMap);

        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            String pathWithQuotes = "\"" + keyPath + "\"";
            // To search for the path in the yaml file
            String pathWithColon = keyPath + ":";

            if (!line.contains(pathWithQuotes) && !line.contains(pathWithColon)) continue;

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
        if (this.pathMap.get(keyPath) == null){
            return 0;
        }

        return this.pathMap.get(keyPath) ;
    }

}
