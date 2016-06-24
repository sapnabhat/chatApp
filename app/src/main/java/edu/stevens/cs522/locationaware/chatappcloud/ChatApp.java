package edu.stevens.cs522.locationaware.chatappcloud;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import edu.stevens.cs522.locationaware.R;
import edu.stevens.cs522.locationaware.databases.DBAdapter;
import edu.stevens.cs522.locationaware.entities.ChatMessage;
import edu.stevens.cs522.locationaware.providers.MessageProviderCloud;
import edu.stevens.cs522.locationaware.services.AlarmReceiver;

public class ChatApp extends Activity implements LoaderManager.LoaderCallbacks<Cursor> ,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    final static public String TAG = ChatApp.class.getCanonicalName();
    public static final char SEPARATOR_CHAR = '|';
    private static final Pattern SEPARATOR = Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);


    public static String[] readStringArray(String in) {
        return SEPARATOR.split(in);
    }

    //Adapter for displaying received messages
    static CursorAdapter messageAdapter;

    ArrayList<String> messageList;
    ArrayAdapter<String> adapter;
    EditText msgTxt;
    Button send;

    public static Context chatAppCtxt;
    private String clientName;
    private String portNo;
    private String hostStr;
    private String uuidStr;
    public static String latitude;
    public static String longitute;
    private Date date;
    public static SharedPreferences prefs;
    public GoogleApiClient googleApiClient;
    public LocationRequest locationRequest;
    public Location location;
    public static final String CLIENT_PORT_KEY = "client_port";
    public static final int DEFAULT_CLIENT_PORT = 8080;

    private int clientPort;
    AlarmManager alarmManager;
    boolean mBound = false;
    public Geocoder coder;
    ListView msgList;
    ContentResolver cr;
    SimpleCursorAdapter msgAdapter;
    String clientId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent myIntent = getIntent();
        buildGoogleApiClient();
        createLocationRequest();
        chatAppCtxt = this;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        coder = new Geocoder(getApplicationContext());
        clientId = myIntent.getStringExtra("clientId");
        msgList = (ListView) findViewById(R.id.msgList);
        msgTxt = (EditText) findViewById(R.id.message_text);
        date = new Date();
        cr = getContentResolver();
        Cursor cursor = cr.query(MessageProviderCloud.CONTENT_URI, null, null, null, null);
        msgAdapter = new SimpleCursorAdapter(this,R.layout.list_layout, null, new String[] {
                "sender","latitude","longitude","text"}, new int[] { R.id.client_name_tv,
                R.id.client_lat , R.id.client_long,R.id.client_message});
        msgList.setAdapter(msgAdapter);
        msgAdapter.changeCursor(cursor);
        cursor.close();
        SharedPreferences prefs = this.getSharedPreferences("chatappcloud",
                Context.MODE_PRIVATE);

        uuidStr = prefs.getString("UUID",
                "00000000-a3e8-11e3-a5e2-0800201c9a66");
        clientName = prefs.getString("clientName", "myself");
        portNo = prefs.getString("portNo", "8080");
        hostStr = prefs.getString("hostStr", "localhost");
        Resources res = getResources();
        send = (Button) findViewById(R.id.send_button);
        send.setOnClickListener(sendListener);

        Long time = (long) (5 * 1000);
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = 	PendingIntent.getBroadcast(this, 12, intentAlarm,0);
        intentAlarm.putExtra("clientId", clientId);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // set the alarm for particular time
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(), time,sender);
        prefs = getSharedPreferences("chatappcloud",Context.MODE_PRIVATE);
    }

    private View.OnClickListener sendListener = new View.OnClickListener() {
        public void onClick(View v) {
            ContentValues cv = new ContentValues();
            double lat = (double)Double.parseDouble(latitude);
            double lon = (double)Double.parseDouble(longitute);
            String add="LA";
            try {
                Address loc = coder.getFromLocation(lat,lon,1).get(0);
                add = loc.getAddressLine(0) +" -- " +loc.getPostalCode();
                System.out.println("address: "+loc.getAddressLine(0));
            } catch (IOException e) {

            }
            String message ="Address: "+add+" \nMessage:"+msgTxt.getText().toString();
            ChatMessage cm = new ChatMessage(0, message, clientName, 0, new Date().getTime(),latitude,longitute);

            cm.writeToProvider(cv);
            try {
                getContentResolver().insert(MessageProviderCloud.CONTENT_URI, cv);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cursor cursor = cr.query(MessageProviderCloud.CONTENT_URI, null, null, null, null);
            msgAdapter.changeCursor(cursor);
        };


    };
    @Override
    public void onStop(){
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = 	PendingIntent.getBroadcast(this, 12, intentAlarm,0);
        try {
            alarmManager.cancel(sender);
        } catch (Exception e) {
            Log.e(TAG, "AlarmManager update was not cancelled. " + e.toString());
        }
        super.onStop();
    }
    @Override
    public void onDestroy(){

        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = 	PendingIntent.getBroadcast(this, 12, intentAlarm,0);
        try {
            alarmManager.cancel(sender);
        } catch (Exception e) {
            Log.e(TAG, "AlarmManager update was not cancelled. " + e.toString());
        }
        cr.delete(MessageProviderCloud.CONTENT_URI, null, null);
        cr.delete(MessageProviderCloud.CONTENT_URI_PEER, null, null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.show_peers):
                openPeersList();
                return true;
            case (R.id.show_settings):
                openSettings();
                return true;
        }
        return false;
    }

    private void openPeersList() {
        Intent i;
        i = new Intent(this, PeersActivity.class);
        startActivityForResult(i, 2);
    }

    private void openSettings() {
        Intent i;
        i = new Intent(this, EntryActivity.class);
        startActivityForResult(i, 2);
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

        return new CursorLoader(this, MessageProviderCloud.CONTENT_URI,
                MessageProviderCloud.MessageProjection, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.msgAdapter.swapCursor(cursor);

    }

    public void onLoaderReset(Loader<Cursor> loader) {
        this.msgAdapter.swapCursor(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        latitude = String.valueOf(location.getLatitude());
        longitute = String.valueOf(location.getLongitude());

        System.out.println("Adding LAT LONG in DATABASE");
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        dbAdapter.updatePeerLocation(clientName, latitude, longitute);

        try {
            Address loc = coder.getFromLocation((double)Double.parseDouble(latitude),(double)Double.parseDouble(longitute),1).get(0);
            tempadd = loc.getAddressLine(0) +" -- " +loc.getPostalCode();
            System.out.println("address: "+loc.getAddressLine(0));
            dbAdapter.updatePeerAddress(clientName, tempadd);
        }
        catch (IOException e) {

        }

        startLocationUpdates();
    }

    public void onConnectionSuspended(int i) {

    }
    String tempadd = "";
    public void onLocationChanged(Location location) {
        this.location = location;
        latitude = String.valueOf(this.location.getLatitude());
        longitute = String.valueOf(this.location.getLongitude());
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }

    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient,this.locationRequest,this);
    }

}
