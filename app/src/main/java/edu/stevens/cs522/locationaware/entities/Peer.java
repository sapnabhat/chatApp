package edu.stevens.cs522.locationaware.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.locationaware.contracts.Contract;

/**
 * Created by Sapna on 4/13/2016.
 */
public class Peer implements Parcelable {

    public long id;
    public String name;
    public Peer() {
    }
    public Peer(String n,long id) {
        this.id = id;
        this.name=n;

    }
    public Peer(Parcel in) {
        readFromParcel(in);
    }
    public Peer(Cursor c) {
        this.id = Contract.getId(c);
        this.name = Contract.getName(c);
    }
    public void readFromParcel(Parcel in) {
        this.id=in.readLong();
        this.name  = in.readString();
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }
    public static final Parcelable.Creator<Peer> CREATOR = new Parcelable.Creator<Peer>() {

        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        public Peer[] newArray(int size) {
            return new Peer[size];
        }

    };
    public int describeContents() {
        return 0;
    }
    public void writeToProvider(ContentValues values) {
        Contract.putId(values,id);
        Contract.putName(values, name);

    }


}
