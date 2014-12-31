package com.tekdi.foodmap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsd017 on 12/14/14.
 */
public class ListMenuActivity extends ListActivity implements Serializable {

    private Long serverId;
    private String serverName;
    private ArrayList<MenuEntity> menuList = new ArrayList<MenuEntity>();
    private ArrayList<ParcelableOrder> orderList = new ArrayList<ParcelableOrder>();

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        menuList.clear();

        Intent intent = getIntent();
        serverId = intent.getLongExtra("serverId",0);
        serverName = intent.getStringExtra("serverName");


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
                Intent intent = new Intent(this, OrderActivity.class);
                intent.putParcelableArrayListExtra("com.tekdi.foodmap.ParcelableOrder", orderList);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        String myServerIdStr = Prefs.getServeIdPref(this);
        if (myServerIdStr.equals("")) {

            MenuEntity m = menuList.get(position);

            ParcelableOrder p = new ParcelableOrder();
            p.menuId = m.getId();
            p.finderDevRegId = Prefs.getDeviceRegIdPref(this);
            p.serverId = m.getServerId();
            p.serverName = serverName;
            p.orderState = 0;
            p.name = m.getName();
            p.description = m.getDescription();
            p.quantity = 1;
            p.price = m.getPrice();

            orderList.add(p);

            Log.v("sajid","ListMenuActivity:onClick severName ="+serverName);

            Intent intent = new Intent(this, OrderActivity.class);
            intent.putParcelableArrayListExtra("com.tekdi.foodmap.ParcelableOrder", orderList);
            startActivity(intent);
        } else {

            MenuEntity m = menuList.get(position);

            ParcelableMenu p = new ParcelableMenu();
            p.menuId = m.getId();
            p.serverId = m.getServerId();
            p.name = m.getName();
            p.description = m.getDescription();
            p.quantity = m.getQuantity();
            p.price = m.getPrice();

            Intent intent = new Intent(this, EditMenuActivity.class);
            intent.putExtra("com.tekdi.foodmap.ParcelableMenu", p);
            startActivity(intent);

        }

    }

    public void showMenu(List<MenuEntity> result) {

        menuList.clear();

        for (MenuEntity q : result) {
            menuList.add(q);
        }

        ListMenuRowAdapter adapter = new ListMenuRowAdapter(this, R.layout.list_menu_row,
                menuList);

        setListAdapter(adapter);

    }
}