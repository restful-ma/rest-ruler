package rest.studentproject.rules;

import rest.studentproject.rules.constants.RequestType;

import java.net.http.HttpRequest;

public class Request {

    private String path;
    private String url;
    private String[] headers;
    private RequestType requestType;

    private HttpRequest.BodyPublisher body;


    public Request(String path, String url, RequestType requestType){
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
}
