package com.danliden.mm.rest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

public class HTTPResponse {
    private final String statusCodeId = "StatusCode";
    private final JSONObject responseAsJson;

    public HTTPResponse() {
        responseAsJson = new JSONObject();
    }

    public HTTPResponse setStatusCode(int statusCode) {
        responseAsJson.put(statusCodeId, statusCode);

        return this;
    }

    public HTTPResponse append(String key, Object data) {
        responseAsJson.put(key, data);

        return this;
    }

    public String toString() {
        try {
            return responseAsJson.toString();
        } catch (NullPointerException ignored) {
        }

        return "";
    }

    public int StatusCode() {
        try {
            return responseAsJson.getInt(statusCodeId);
        } catch (JSONException ignored) {
        }

        return HttpStatus.NOT_FOUND.value();
    }

}
