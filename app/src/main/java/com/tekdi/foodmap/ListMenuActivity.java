package com.tekdi.foodmap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
    private String source;
    private Boolean amIServer;

    private ArrayList<MenuEntity> menuList = new ArrayList<MenuEntity>();
    private ArrayList<ParcelableOrder> orderList = new ArrayList<ParcelableOrder>();
    ListMenuRowAdapter adapter = null;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        menuList.clear();

        Intent intent = getIntent();
        serverId = intent.getLongExtra("serverId",0);
        serverName = intent.getStringExtra("serverName");
        source = intent.getStringExtra("source");

        if (source.equals("server"))
            amIServer = true;
        else
            amIServer = false;

        registerForContextMenu(getListView());

        setContentView(R.layout.list_menu_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (serverId != 0) {
            Log.v("sajid","executing listmenu");
            ListMenuEndpointAsyncTask l = new ListMenuEndpointAsyncTask(this);
            l.setServerId(serverId);
            l.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (amIServer) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (amIServer) {
            switch (item.getItemId()) {
                case R.id.action_new:
                    Intent intent = new Intent(this, AddMenuActivity.class);
                    startActivity(intent);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        if (amIServer == false) {

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
            p.thumbnail = m.getThumbnail();

            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("com.tekdi.foodmap.ParcelableOrder", p);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (amIServer) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) {
                case R.id.menu_edit:

                    MenuEntity m = menuList.get(info.position);

                    ParcelableMenu p = new ParcelableMenu();
                    p.menuId = m.getId();
                    p.serverId = m.getServerId();
                    p.name = m.getName();
                    p.description = m.getDescription();
                    p.quantity = m.getQuantity();
                    p.price = m.getPrice();
                    p.thumbnail = m.getThumbnail();

                    Intent intent = new Intent(this, EditMenuActivity.class);
                    intent.putExtra("com.tekdi.foodmap.ParcelableMenu", p);
                    startActivity(intent);

                    return true;
                case R.id.menu_delete:
                    MenuEntity menu = menuList.get(info.position);
                    new MenuEntityDeleteEndpointAsyncTask(this).execute(new Pair<Context, MenuEntity>(this, menu));

                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
    }

    public void showMenu(List<MenuEntity> result) {

        menuList.clear();

        for (MenuEntity q : result) {
            menuList.add(q);
        }

        adapter = new ListMenuRowAdapter(this, R.layout.list_menu_row,
                menuList);

        setListAdapter(adapter);
    }

    public void doneDeletingMenu() {
        if (serverId != 0) {
            Log.v("sajid","executing listmenu after delete");
            ListMenuEndpointAsyncTask l = new ListMenuEndpointAsyncTask(this);
            l.setServerId(serverId);
            l.execute();
        }

    }

}