package br.com.caronas.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ApiResponse<T> {
    private String method;
    private String endpoint;
    private int statusCode;
    private Map<String, String> requestHeaders = new HashMap<>();
    private String requestBody;
    private String responseBody;
    private boolean success;
    private T data;
    private ApiError error;
    private String timestamp;

    public ApiResponse(String method, String endpoint, int statusCode, boolean success) {
        this.method = method;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.success = success;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    public static <T> ApiResponse<T> ok(String method, String endpoint, int statusCode, T data, String reqBody, String respBody) {
        ApiResponse<T> resp = new ApiResponse<>(method, endpoint, statusCode, true);
        resp.setData(data);
        resp.setRequestBody(reqBody);
        resp.setResponseBody(respBody);
        return resp;
    }

    public static <T> ApiResponse<T> fail(String method, String endpoint, ApiError error, String reqBody) {
        ApiResponse<T> resp = new ApiResponse<>(method, endpoint, error.getStatusCode(), false);
        resp.setError(error);
        resp.setRequestBody(reqBody);
        resp.setResponseBody(error.toJson());
        return resp;
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void addHeader(String key, String value) {
        this.requestHeaders.put(key, value);
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
