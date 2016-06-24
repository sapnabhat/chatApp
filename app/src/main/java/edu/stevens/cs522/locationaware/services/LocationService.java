package edu.stevens.cs522.locationaware.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Sapna on 4/13/2016.
 */
public class LocationService extends Service {

    @Override
    public void onCreate(){
        super.onCreate();
        if(checkPendingMessages()){
            sendMessage();
        }
        else{
            stopSelf();
        }
    }

    boolean checkPendingMessages(){
        return false;
    }

    boolean sendMessage(){
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
