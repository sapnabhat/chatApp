package edu.stevens.cs522.locationaware.requests;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Sapna on 4/13/2016.
 */
public class Register extends Request {
    //	public long clientID;
//	public UUID registrationID; // sanityCheck
    public String username;

    public int describeContents() {
        return hashCode();
    }

    public Register(long clientID, UUID registrationID, String username,String addr){
        this.id = clientID;
        this.regid = registrationID;
        this.username = username;
        this.addr = addr;
    }
    public Register(Parcel source){
        this.id = source.readLong();
        this.regid = UUID.fromString(source.readString());
        this.username = source.readString();
        this.addr = source.readString();
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.regid.toString());
        dest.writeString(this.username);
        dest.writeString(this.addr);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
    }
    public static final Parcelable.Creator<Register> CREATOR = new Parcelable.Creator<Register>() {
        public Register createFromParcel(Parcel source) {
            return new Register(source);
        }

        public Register[] newArray(int size) {
            return new Register[size];
        }
    };

    @Override
    public Map<String, String> getRequestHeaders() {
        return null;
    }

    @Override
    public Uri getRequestUri() {
        return Uri.parse(addr +"/chat?username="+this.username+"&regid=" + this.regid.toString());
    }

    @Override
    public String getRequestEntity() throws IOException {
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) {
        try {
            rd.beginObject();
            while(rd.peek()!= JsonToken.END_OBJECT){
                String name = rd.nextName();
                if(name.equals("id")){
                    this.id=rd.nextLong();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
