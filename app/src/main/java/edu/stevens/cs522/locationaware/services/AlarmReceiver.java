package edu.stevens.cs522.locationaware.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import edu.stevens.cs522.locationaware.helpers.ServiceHelper;

/**
 * Created by Sapna on 4/13/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceHelper.getInstance(context).sync();
        Toast.makeText(context, "syncing message", Toast.LENGTH_LONG).show();
    }

}
