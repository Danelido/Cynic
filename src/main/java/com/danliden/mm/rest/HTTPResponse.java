package com.danliden.mm.rest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

public class HTTPResponse {
    private String statusCodeId = "StatusCode";
    private JSONObject obj;

    public HTTPResponse(){
        obj = new JSONObject();
    }

    public HTTPResponse setStatusCode(int statusCode){
       obj.put(statusCodeId, statusCode);

       return this;
    }

    public HTTPResponse append(String key, Object data){
        obj.put(key,data);

        return this;
    }

    public String toString(){
        try{ return obj.toString();}
        catch(NullPointerException e){}

        return "";
    }

    public int StatusCode(){
        try{return obj.getInt(statusCodeId);}
        catch(JSONException e){}

        return HttpStatus.NOT_FOUND.value();
    }

}
