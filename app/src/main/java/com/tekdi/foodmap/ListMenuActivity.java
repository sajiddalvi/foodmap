package com.tekdi.foodmap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;
import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsd017 on 12/14/14.
 */
public class ListMenuActivity extends ListActivity {

    private Long serverId;
    private ArrayList<MenuEntity> menuList = new ArrayList<MenuEntity>();

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Intent intent = getIntent();

        String fName = intent.getStringExtra("fname");

        serverId = intent.getLongExtra("serverId",0);

        if (serverId != 0) {
            Log.v("sajid","executing listmenu");
            ListMenuEndpointAsyncTask l = new ListMenuEndpointAsyncTask(this);
            l.setServerId(serverId);
            l.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.order_menu:
                Log.v("sajid","Start Order");

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();

        MenuEntity m = menuList.get(position);

        OrderEntity o = new OrderEntity();

        o.setMenuId(m.getId());
        o.setServerId(m.getServerId());
        o.setFinderDevRegId(Prefs.getDeviceRegIdPref(this));
        o.setOrderState(0); // order initiated




/*
            String serverId = Prefs.getServeIdPref(this);
            Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("serverId", Long.parseLong(serverId));
            intent.putExtra("serverId", Long.parseLong(serverId));
            startActivity(intent);
*/

    }

    public void showMenu(List<MenuEntity> result) {
        ArrayList<String> values = new ArrayList<String>();;


        for (MenuEntity q : result) {
            menuList.add(q);
            values.add(q.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

    }
}