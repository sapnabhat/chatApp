package edu.stevens.cs522.locationaware.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

import edu.stevens.cs522.locationaware.requests.Register;
import edu.stevens.cs522.locationaware.services.RequestService;

/**
 * Created by Sapna on 4/13/2016.
 */
public class ServiceHelper {

    // private static final ServiceHelper INSTANCE = new ServiceHelper();
    public static String host = "http://127.0.0.1:8080";
    int type;
    ResultReceiver resultReceiver;
    Register register;
    public Context ctx;
    private static Object lock = new Object();
    private static ServiceHelper instance;

    private ServiceHelper(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    public static ServiceHelper getInstance(Context ctx) {
        synchronized (lock) {
            if (instance == null) {
                instance = new ServiceHelper(ctx);
            }else{
                instance.ctx = ctx.getApplicationContext();
            }
        }

        return instance;
    }

    public void register(Register register, ResultReceiver receiver) {
        Intent i = new Intent(this.ctx, RequestService.class);
        //Intent i = new Intent(RequestService.REGISTER_ACTION);
        Log.d("register helper", this.ctx.getClass().toString());
        i.putExtra("register", register);
        i.putExtra("receiver", receiver);
        this.ctx.startService(i);
        // TODO Auto-generated method stub

    }

    public void sync() {
        Intent i = new Intent(ctx, RequestService.class);
        Log.d("Sync helper", this.ctx.getClass().toString());
        i.putExtra("type", 1);
        this.ctx.startService(i);

    }


    public void unregister() {
        // TODO Auto-generated method stub
        Intent i = new Intent(RequestService.UNREGISTER_ACTION);
        Log.d("unRegister helper", this.ctx.getClass().toString());
        i.putExtra("unregister", 1);
        this.ctx.startService(i);
    }

}
