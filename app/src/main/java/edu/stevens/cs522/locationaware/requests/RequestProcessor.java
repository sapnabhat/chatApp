package edu.stevens.cs522.locationaware.requests;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;

import edu.stevens.cs522.locationaware.entities.ChatMessage;
import edu.stevens.cs522.locationaware.entities.Peer;
import edu.stevens.cs522.locationaware.providers.MessageProviderCloud;

/**
 * Created by Sapna on 4/13/2016.
 */
public class RequestProcessor {

    RestMethod restMethod = new RestMethod();

    // Get the Content Resolver.
    ContentResolver cr;

    public RequestProcessor(ContentResolver cr) {
        super();
        this.cr = cr;
    }

    public void perform(Register request, ResultReceiver resultReceiver) {
        Response response = restMethod.perform(request);
        Bundle bundle = new Bundle();

        ContentValues newValues = new ContentValues();
        newValues.put("name", request.username);
        newValues.put("_id", response.body);
        Uri uri = cr.insert(MessageProviderCloud.CONTENT_URI_PEER, newValues);
        if(uri!=null){
            Log.i("ReqProc:insert success",uri.toString());
        }

        bundle.putString("clientId", response.body);
        resultReceiver.send(1, bundle);
    };

    public void perform(Synchronize request) {
        Cursor cursor = cr.query(MessageProviderCloud.CONTENT_URI,null, "messageid=?",new String[]{ "0"},null);
        ArrayList<String> temp = new ArrayList<String>();
        ChatMessage cm1;
        if(cursor.moveToFirst()){
            do{
                cm1 = new ChatMessage(cursor);
                Log.d("to be uploaded",cm1.messageText);
                temp.add(cm1.messageText);
            }while(cursor.moveToNext());
        }

        request.message= temp;
        StreamingResponse response = restMethod.perform(request);
        //update list of peers
        cr.delete(MessageProviderCloud.CONTENT_URI_PEER, null, null);
        if(response.usersList!=null){
            for(String name : response.usersList){
                ContentValues values=new ContentValues();
                new Peer(name,1).writeToProvider(values);
                Log.d("inserted peer", cr.insert(MessageProviderCloud.CONTENT_URI_PEER,values).toString());
            }

            //add new message to database
            //delete all messageid =0 message
            int i = cr.delete(MessageProviderCloud.CONTENT_URI, "messageid=?",new String[]{ "0"});
            Log.d("Temp msg being deleted", String.valueOf(i));
            for(String[] msg:response.msgList){
                ChatMessage cm;
                long num = Long.parseLong(msg[4]);
                cm=new ChatMessage(0, msg[6], msg[5],num,Long.parseLong(msg[1]),msg[2],msg[3]);

                ContentValues values=new ContentValues();
                cm.writeToProvider(values);
                cr.insert(MessageProviderCloud.CONTENT_URI, values);
            }
        }
    };

    public void perform(Unregister request) {
    };
}
