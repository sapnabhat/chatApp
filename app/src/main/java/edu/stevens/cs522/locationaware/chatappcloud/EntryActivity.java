package edu.stevens.cs522.locationaware.chatappcloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.util.UUID;

import edu.stevens.cs522.locationaware.R;
import edu.stevens.cs522.locationaware.databases.DBAdapter;
import edu.stevens.cs522.locationaware.helpers.ServiceHelper;
import edu.stevens.cs522.locationaware.requests.Register;

public class EntryActivity extends Activity {

    public static final String NAME = "name";
    public static final String PORT = "port";
    public static final String HOST = "host";
    public static final String PREFS_NAME = "chatappcloud";
    public GoogleApiClient googleApiClient;
    public LocationRequest locationRequest;
    public String latitude;
    public String longitude;

    public static boolean networkOn = false;
    AckReceiver receiver;
    String clientName, portNo, hostStr, uuid;
    int ipt;
    public static Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        receiver = new AckReceiver(new Handler());
        mContext = this.getApplicationContext();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        SharedPreferences prfs = getSharedPreferences(PREFS_NAME, 0);
        uuid = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = prfs.edit();
        editor.putString("clientUUID", uuid);
        //editor.putString("X-latitude",latitude);
        //editor.putString("X-longitude",longitude);
        editor.commit();
    }

    public class AckReceiver extends ResultReceiver {
        public AckReceiver(Handler handler) {
            super(handler);
        }

        protected void onReceiveResult(int resultCode, Bundle result) {
            switch (resultCode) {
                case 1:
                    String client = result.getString("clientId");
                    if (client != null) {
                        Log.d("OnReceiveResult id:", client);
                    }

                    Intent i = new Intent(mContext, ChatApp.class);
                    i.putExtra(NAME, clientName);
                    i.putExtra(PORT, portNo);
                    i.putExtra(HOST, hostStr);
                    i.putExtra("clientId", client);

                    DBAdapter db = new DBAdapter(EntryActivity.mContext);
                    db.open();
                    db.addPeer_2(Integer.parseInt(client), clientName);
                    startActivity(i);
                    break;
                case 0:
                    Toast.makeText(mContext, "Login Fail, Can't find server.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    };

    // this function will be called when the start_chat button is clicked
    public void start_chat(View view) {
        EditText username = (EditText) findViewById(R.id.name_field);
        EditText port = (EditText) findViewById(R.id.port_text);
        EditText host = (EditText) findViewById(R.id.dest_text);
        clientName = username.getText().toString();
        portNo = port.getText().toString();
        hostStr = host.getText().toString();

        if (username.getText().toString().matches("")
                || port.getText().toString().matches("")|| host.getText().toString().matches("")) {
            Toast.makeText(this, "Please fill every field",Toast.LENGTH_SHORT).show();
        }
        else {
            Register register = new Register(0, UUID.fromString(uuid),clientName, "http://" + hostStr + ":" + portNo);
            SharedPreferences prefs = this.getSharedPreferences("chatappcloud", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("clientName", register.username);
            editor.putString("portNo", portNo);
            editor.putString("hostStr", hostStr);
            editor.commit();
            ServiceHelper.getInstance(this).register(register, receiver);
        }

    }
}
