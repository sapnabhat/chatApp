package edu.stevens.cs522.locationaware.requests;

import java.util.List;
import java.util.Map;

/**
 * Created by Sapna on 4/13/2016.
 */
public class Response {

    public int status;
    public  Map<String, List<String>> headers;
    public String body;

    public Response(){

    }
    public Response(int status,  Map<String, List<String>> headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }
    public boolean isValid(){
        if(this.status ==201){
            return true;
        }else{
            return false;
        }
    };

}
