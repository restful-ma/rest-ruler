package rest.studentproject.analyzer;

import io.swagger.v3.oas.models.OpenAPI;
import rest.studentproject.report.Report;
import rest.studentproject.rule.IRestRule;
import rest.studentproject.rule.Utility;
import rest.studentproject.rule.Violation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestAnalyzer {
    //Singleton
    private static final Report report = Report.getInstance();
    public static LOCMapper locMapper = null;
    public static Map<String, String> securitySchemas = null;

    public final OpenAPI openAPI;


    public RestAnalyzer(String path) {
        this.openAPI = Utility.getOpenAPI(path);
        locMapper = new LOCMapper(openAPI, path);
        locMapper.mapOpenAPIKeysToLOC();
//        List<Server> servers = swaggerParseResult.getOpenAPI().getServers();
        // Check if server is available
//        for (Server server : servers) {
//            System.out.println(server.getUrl());
//            System.out.println(server.getVariables());
//
//            System.out.println(pingURL(server.getUrl(), 2000));
//        }
    }

    /**
     * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in
     * the 200-399 range.
     *
     * @param url     The HTTP URL to be pinged.
     * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
     *                the total timeout is effectively two times the given timeout.
     * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
     * given timeout, otherwise <code>false</code>.
     */
    public static boolean pingURL(String url, int timeout) {
        url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    public List<Violation> runAnalyse(List<IRestRule> activeRules, boolean generateReport) {
        List<Violation> violations = new ArrayList<>();
        for (IRestRule rule : activeRules) {
            if (!rule.getIsActive()) continue;
            violations.addAll(rule.checkViolation(this.openAPI));
        }
        //generates Report
        if (generateReport) {
            report.generateReport(violations);
        }
        return violations;
    }
}
