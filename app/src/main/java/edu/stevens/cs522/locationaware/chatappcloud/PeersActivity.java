package edu.stevens.cs522.locationaware.chatappcloud;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.locationaware.R;
import edu.stevens.cs522.locationaware.databases.DBAdapter;

public class PeersActivity extends ListActivity {

    private SimpleCursorAdapter dbAdapter;
    private  DBAdapter chatDbAdapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peers);
        ListView myListView = (ListView) findViewById(android.R.id.list);
        registerForContextMenu(myListView);
        chatDbAdapter = new DBAdapter(this);
        chatDbAdapter.open();
        fillData();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Peer Detail");
        menu.add(0, v.getId(), 0, "Show Messages");
        menu.add(0, v.getId(), 0, "Cancel");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getTitle() =="Show Messages"){
            try{
                fillDataMsg(info.id);
            }
            catch(Exception e){
            }
            return true;
        }else{
            return super.onContextItemSelected(item);
        }

    }

    private void fillDataMsg(long id) {
        String name;
        name = chatDbAdapter.getNameById(id);
        Log.d("search peer", name);
        Cursor c = chatDbAdapter.getMessageByPeer(name);
        startManagingCursor(c);

        // For the list adapter that will exchange data with the list view, we need
        // to specify which layout object to bind to which data object.
        String[] from = new String[] {"text", "sender" };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2, android.R.id.text2, android.R.id.text2 };

        // Create a list adapter that encapsulates the results of a DB query
        dbAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_2, c, from, to);

        //Bind to the new adapter
        setListAdapter(dbAdapter);
        setSelection(0);
    }

    @SuppressWarnings("deprecation")
    private void fillData() {
        Cursor c = chatDbAdapter.getAllPeer2();
        startManagingCursor(c);

        String[] from = new String[] { "_id", "name", DBAdapter.LATITUDE, DBAdapter.LONGITUDE, DBAdapter.GEO_ADDR};
        int[] to = new int[] {R.id._peer_id, R.id._peer_name, R.id.lat, R.id.lng, R.id.add};
        dbAdapter = new SimpleCursorAdapter(this, R.layout.peer_list_layout, c, from, to);
        setListAdapter(dbAdapter);
        setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.peers, menu);
        return true;
    }

}
