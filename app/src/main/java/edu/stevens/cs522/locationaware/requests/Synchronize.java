package edu.stevens.cs522.locationaware.requests;

import android.net.Uri;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import edu.stevens.cs522.locationaware.chatappcloud.ChatApp;

/**
 * Created by Sapna on 4/13/2016.
 */
public class Synchronize {

    public String userId;
    public ArrayList<String> message;
    public long id = 0;
    public UUID regid;
    public String addr;
    public double latitude=40.074;
    public double longitude=-74.32;

    public int describeContents() {
        return hashCode();
    }
    public Synchronize(){};
    public Synchronize(long seqnum, UUID registrationID, String username,
                       String addr, ArrayList<String> message) {
        this.id = seqnum;
        this.regid = registrationID;
        this.userId = username;
        this.addr = addr;
        this.message = message;
    }

    public Map<String, String> getRequestHeaders() {
        return null;
    }

    public Uri getRequestUri() {
        return Uri.parse(addr + "/chat/" + this.userId + "?regid="
                + this.regid.toString() + "&seqnum=" + String.valueOf(id)+"&latitude=40.32&longitude=-74.032");

    }

    public String getRequestEntity() throws IOException {
        JSONObject obj = new JSONObject();
        try {
            obj.put("chatroom", "_default");
            obj.put("timestamp", String.valueOf(new Date().getTime()));
            obj.put("text", this.message);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public StreamingResponse getResponse(HttpURLConnection conn, JsonReader rd) {
        StreamingResponse response = new StreamingResponse();
        List usersList = new ArrayList<String>();
        List msgList = new Vector<String[]>();
        List latts = new ArrayList<String>();
        List longs = new ArrayList<String>();

        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);
            conn.setRequestProperty("X-latitude",ChatApp.latitude );
            conn.setRequestProperty("X-longitude",ChatApp.longitute);

            conn.connect();
            JsonWriter wr;
            wr = new JsonWriter(new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8")));
            wr.beginArray();
            for (String msg : message) {
                wr.beginObject();
                wr.name("chatroom");
                wr.value("_default");
                wr.name("timestamp");
                wr.value(new Date().getTime());
                wr.name("text");
                wr.value(msg);
                wr.endObject();
            }
            wr.endArray();
            wr.flush();
            wr.close();
            JsonReader jrd = new JsonReader(new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")));
            System.out.println(jrd.toString());
            Log.d("JSON_READER",jrd.toString());
            JsonToken tk = null;

            jrd.beginObject();
            jrd.nextName();
            jrd.beginArray();
            while (jrd.hasNext()) {
                jrd.beginObject();
                jrd.nextName();
                String sender=jrd.nextString();
                jrd.nextName();
                Double x=jrd.nextDouble();
                jrd.nextName();
                Double y=jrd.nextDouble();
                usersList.add(sender);
                jrd.endObject();
            }
            jrd.endArray();

            jrd.nextName();
            jrd.beginArray();
            while (jrd.hasNext()) {
                jrd.beginObject();
                String[] tmp = new String[7];
                jrd.nextName();
                String chatroom = jrd.nextString();
                tmp[0]=chatroom;
                jrd.nextName();
                String timestamp = jrd.nextString();
                tmp[1]=timestamp;
                jrd.nextName();
                Double x = jrd.nextDouble();
                String s1=Double.toString(x);
                tmp[2]=s1;
                jrd.nextName();
                Double y = jrd.nextDouble();
                String s2=Double.toString(y);
                tmp[3]=s2;
                jrd.nextName();
                long seqnum = jrd.nextLong();
                String s3=Long.toString(seqnum);
                tmp[4]=s3;
                jrd.nextName();
                String sender = jrd.nextString();
                tmp[5]=sender;
                jrd.nextName();
                String text = jrd.nextString();
                tmp[6]=text;
                msgList.add(new String[] { tmp[0], tmp[1], tmp[2], tmp[3],tmp[4],tmp[5],tmp[6]});
                jrd.endObject();
            }
            Log.d("USER_LIST",usersList.toString());
            response.usersList = usersList;
            response.msgList = msgList;
            conn.disconnect();

        } catch (Exception e) {
            Log.e("Error on sync", e.toString());
        }
        return response;
    }

}
