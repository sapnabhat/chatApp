package edu.stevens.cs522.locationaware.contracts;

import android.content.ContentValues;
import android.database.Cursor;

import edu.stevens.cs522.locationaware.databases.DBAdapter;

/**
 * Created by Sapna on 4/13/2016.
 */
public class Contract {

    public static final String TEXT = "text";
    public static final String ID = "_id";
    public static final String SENDER = "sender";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String PORT = "port";
    public static String getText(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(TEXT));
    }
    public static void putText(ContentValues values, String id) {
        values.put(TEXT, id);
    }
    public static int getId(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(ID));
    }
    public static void putId(ContentValues values, long id2) { values.putNull(ID);
    }
    public static String getSender(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
    }
    public static void putSender(ContentValues values, String title) { values.put(SENDER, title);
    }
    public static String getName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(NAME));
    }
    public static void putName(ContentValues values, String title) { values.put(NAME, title);
    }
    public static void putLatitude(ContentValues values, String title) { values.put(LATITUDE, title);
    }
    public static void putLongitude(ContentValues values, String title) { values.put(LONGITUDE, title);
    }

    public static String getLatitude(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.LATITUDE));
    }
    public static String getLongitude(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.LONGITUDE));
    }

    public static String getAddress(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS));
    }
    public static void putAddress(ContentValues values, String title) { values.put(ADDRESS, title);
    }
    public static String getPort(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(PORT));
    }

    public static void putPort(ContentValues values, int title) { values.put(PORT, title);
    }

    public static int getDate(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(DBAdapter.DATE));
    }
    public static void putDate(ContentValues values, long id2) { values.put(DBAdapter.DATE,id2);
    }

    public static int getMessageId(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(DBAdapter.MESSAGEID));
    }
    public static void putMessageID(ContentValues values, long id2) { values.put(DBAdapter.MESSAGEID,id2);
    }

    public static int getSenderId(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(DBAdapter.SENDERID));
    }
    public static void putSenderId(ContentValues values, long id2) { values.put(DBAdapter.SENDERID,id2);
        values.put(DBAdapter.SENDERID,id2);
    }

}
