package edu.stevens.cs522.locationaware.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.locationaware.entities.ChatMessage;
import edu.stevens.cs522.locationaware.entities.Peer;
import edu.stevens.cs522.locationaware.providers.MessageProviderCloud;

/**
 * Created by Sapna on 4/13/2016.
 */
public class DBAdapter {

    private static final String DATABASE_NAME = "ChatCloud.s3db";
    private static final String DATABASE_MESSAGE_TABLE = "message";
    private static final String DATABASE_PEER_TABLE = "peer";
    private static final String DATABASE_PEER_TABLE_2 = "peer_2";
    private static final String DATABASE_MESSAGE_REGISTRATION_TABLE="register";
    public static final String TEXT = "text";
    public static final String ID = "_id";
    public static final String SENDER = "sender";
    public static final String DATE = "date";
    public static final String MESSAGEID = "messageid";
    public static final String SENDERID = "senderid";
    public static final String SENDERNAME = "name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String GEO_ADDR = "geo_addr";

    public static final String REGISTERID ="regid";
    public static final String DATABASE_CREATE = "create table "
            + DATABASE_MESSAGE_TABLE + " (" + ID + " integer primary key, "
            + TEXT + " text not null, " + SENDER + " text not null," + DATE
            + " integer not null," + MESSAGEID + " integer not null,"
            + SENDERID + " integer not null,"+LATITUDE+" text,"+LONGITUDE+" text" + ")";
    public static final String DATABASE_CREATE_PEER = "create table " +DATABASE_PEER_TABLE +" ("
            + ID + " integer primary key, " + SENDERNAME + " text not null, " + GEO_ADDR
            + " text DEFAULT 'ADDR', " + LATITUDE + " text DEFAULT 'LAT', " + LONGITUDE + " text DEFAULT 'LNG')";


    public static final String DATABASE_CREATE_PEER_2 = "CREATE TABLE IF NOT EXISTS " +DATABASE_PEER_TABLE_2 +" ("
            + ID + " integer primary key, " + SENDERNAME + " text not null, " + GEO_ADDR
            + " text DEFAULT 'ADDR', " + LATITUDE + " text DEFAULT 'LAT', " + LONGITUDE + " text DEFAULT 'LNG')";

    public static final String DATABASE_CREATE_REGISTER = "create table "+DATABASE_MESSAGE_REGISTRATION_TABLE+" ("+ID
            +" integer primary key, " + REGISTERID +" integer not null, "+MESSAGEID+" integer not null )";

    public SQLiteDatabase db;
    // Context of the application using the database.
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            SQLiteDatabase sdb = this.getWritableDatabase();
            sdb.execSQL(DBAdapter.DATABASE_CREATE_PEER_2);
        }

        // Database version mismatch: version on disk needs to be upgraded to the current version.
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion,int _newVersion) {
            // Log the version upgrade.
            Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion+ " to " + _newVersion);
            // Upgrade: drop the old table and create a new one.
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MESSAGE_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS peer");
            _db.execSQL("DROP TABLE IF EXISTS "+DATABASE_MESSAGE_REGISTRATION_TABLE);

            onCreate(_db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBAdapter.DATABASE_CREATE);
            db.execSQL(DBAdapter.DATABASE_CREATE_PEER);
            db.execSQL(DBAdapter.DATABASE_CREATE_REGISTER);
        }
    }

    public DBAdapter(Context ctx) {
        this.context = ctx;
    }

    public DBAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context, DATABASE_NAME, null,MessageProviderCloud.DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();

        SQLiteDatabase mDataBase;
        mDataBase = dbHelper.getReadableDatabase();
        Cursor dbCursor = mDataBase.query(DATABASE_PEER_TABLE_2, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++){
            System.out.println(" -- " + columnNames[i]);
        }
        return this;
    }

    public Cursor getAllEntries() {
        return db.query(DATABASE_MESSAGE_TABLE,new String[] { ID, TEXT, SENDER,LATITUDE,LONGITUDE }, null, null, null,null, null);
    }

    public boolean deleteAll() {
        return db.delete(DATABASE_MESSAGE_TABLE, null, null) > 0;
    }

    public boolean addMessage(ChatMessage chatMessage) {
        ContentValues contentValues = new ContentValues();
        chatMessage.writeToProvider(contentValues);
        return db.insert(DATABASE_MESSAGE_TABLE, null, contentValues) > 0;
    }

    public boolean addPeer_2(int id, String name) {
        System.out.println("Inside Add_peer_2");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(SENDERNAME, name);
        return db.insert(DATABASE_PEER_TABLE_2, null, contentValues) > 0;
    }

    public void updatePeerLocation(String name, String lat, String lng){
        System.out.println("Inside update_Peer_Location 2");
        ContentValues newValues = new ContentValues();
        newValues.put(LATITUDE, lat);
        newValues.put(LONGITUDE, lng);
        db.update(DATABASE_PEER_TABLE_2, newValues, "name=?",  new String[]{name});
    }

    public void updatePeerAddress(String name, String addr){
        System.out.println("Inside update_Peer_Address_2");
        ContentValues newValues = new ContentValues();
        newValues.put(GEO_ADDR, addr);
        db.update(DATABASE_PEER_TABLE_2, newValues, "name=?",  new String[]{name});
    }

    public Cursor getAllPeer2() {
        System.out.println("Inside get_All_Peer_2");
        return db.query(DATABASE_PEER_TABLE_2, new String[] { ID, "name",LATITUDE, LONGITUDE, GEO_ADDR }, null, null, null, null, null);
    }

    public boolean addPeer(Peer peer) {
        ContentValues contentValues = new ContentValues();
        peer.writeToProvider(contentValues);
        db.delete(DATABASE_PEER_TABLE, "name ='" + peer.name + "' and "+ "peer._id ='" + peer.id + "'", null);
        return db.insert(DATABASE_PEER_TABLE, null, contentValues) > 0;
    }


    public boolean deletePeer(Peer peer) {
        ContentValues contentValues = new ContentValues();
        peer.writeToProvider(contentValues);
        return db.delete(DATABASE_PEER_TABLE, "name ='" + peer.name + "' and "+ "peer._id ='" + peer.id + "'", null) > 0;
    }

    public Cursor getAllPeer() {
        return db.query(DATABASE_PEER_TABLE, new String[] { ID, "name",LATITUDE, LONGITUDE, GEO_ADDR }, null, null, null, null, null);
    }

    public Cursor getMessageByPeer(String name) {
        String whereClause = "sender = ?";
        String[] whereArgs = new String[] { name };
        return db.query(DATABASE_MESSAGE_TABLE,new String[] { ID, TEXT, SENDER }, whereClause, whereArgs,null, null, null);
    }

    public String getNameById(long id){
        String whereClause = "peer._id = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        Cursor c =  db.query(DATABASE_PEER_TABLE,new String[] { ID, TEXT, SENDER }, whereClause, whereArgs,null, null, null);
        String name = null;
        if(c.moveToFirst()){
            name = c.getString(c.getColumnIndex("name"));
        }
        return name;
    }
}
