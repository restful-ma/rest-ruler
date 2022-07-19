package rest.studentproject.analyzer;

import io.swagger.v3.oas.models.OpenAPI;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class LOCMapper {
    public Map<String, Integer> keyLOCMap;
    public List<String> openAPIKeys;
    private String path = "";
    private OpenAPI openAPI = null;

    public LOCMapper(String path, OpenAPI openAPI) {
        this.path = path;
        this.openAPI = openAPI;

        getOpenAPIKeys();
        mapOpenAPIKeysToLOC();

    }

    private Array getOpenAPIKeys() {
        // Alle wichtigen keys erhalten. Beachte: OpenAPI 3.0 und 2.0 haben verschiedene keys
        return null;
    }

    private void mapOpenAPIKeysToLOC() {
        // Durchsuche file nach String keys aus getOpenAPIKeys()
    }

    public Map<String, Integer> getOpenAPIKeyLOC() {
        return this.keyLOCMap;
    }

    public int getLOCKey(String key) {
        return this.keyLOCMap.get(key);
    }
}
