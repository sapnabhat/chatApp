package edu.stevens.cs522.locationaware.requests;

import android.net.Uri;
import android.os.Parcel;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Sapna on 4/13/2016.
 */
public class Unregister extends Request {

    private Uri serverUri;
    public Unregister(long clientID, UUID registrationID, String lat,String lon,Uri serverUri) {
        super(clientID, registrationID,lat,lon);
        this.serverUri = serverUri;
    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        return null;
    }

    @Override
    public Uri getRequestUri() {
        return null;
    }

    @Override
    public String getRequestEntity() throws IOException {
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) {
        return null;
    }


}
