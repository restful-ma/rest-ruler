package rest.studentproject.rule;

import org.apache.commons.lang3.tuple.ImmutablePair;
import rest.studentproject.rule.constants.RequestType;
import rest.studentproject.rule.constants.SecuritySchema;

import java.net.http.HttpRequest;

public class Request {

    private final String path;
    private final String url;
    private String[] headers;
    private final RequestType requestType;

    private HttpRequest.BodyPublisher body;


    public Request(String path, String url, RequestType requestType) {
        this.path = path;
        this.url = url;
        this.requestType = requestType;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public HttpRequest.BodyPublisher getBody() {
        return body;
    }

    public void setBody(HttpRequest.BodyPublisher body) {
        this.body = body;
    }

//    public ImmutablePair<String, String> getSecurityHeader(SecuritySchema usedSecurity, String token) {
//        switch (usedSecurity) {
//            case BASIC:
//                return ImmutablePair.of("Authorization", "Bearer " + token);
//            case APIKEY:
//                return ImmutablePair.of("Authorization");
//            case BEARER:
//                return;
//        }
//    }
}
