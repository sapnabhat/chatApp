package edu.stevens.cs522.locationaware.requests;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by Sapna on 4/13/2016.
 */
public class StreamingResponse {

    public HttpURLConnection connection;
    public Response response;
    public List<String[]> msgList;
    public List<String>  usersList;
    public int sequenceNum;

    public StreamingResponse(){
        connection=null;
    }
    public Response perform(Unregister ur){
        return null;
    }

}
