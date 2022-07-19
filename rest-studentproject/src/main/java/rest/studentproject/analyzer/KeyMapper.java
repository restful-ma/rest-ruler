package rest.studentproject.analyzer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;

public class KeyMapper {
    public OpenAPI openAPI = null;
    public Paths paths = null;
    public List<Server> serverURLs = new ArrayList<>();

    public KeyMapper(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    private void setPaths() {
        this.paths = openAPI.getPaths();
    }

    private void setServerURLs() {
        this.serverURLs = openAPI.getServers();
    }

    private void setComponents() {

    }
}
