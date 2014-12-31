package com.tekdi.foodmap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;
import java.util.List;

public class ListOrderActivity extends ListActivity {

    private Boolean iAmServer = Boolean.FALSE;
    private Long myServerId;
    private String finderDevRegId = "";

    private ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Log.v("sajid","ListOrderActivity onCreate");

        Intent intent = getIntent();
        String intentServerIdStr = intent.getStringExtra("serverId");
        Long intentServerId =new Long(0);

        if (!(intentServerIdStr.equals("")))
            intentServerId = Long.parseLong(intentServerIdStr);

        String myServerIdStr = Prefs.getServeIdPref(this);
        if (myServerIdStr.equals(""))
            iAmServer = Boolean.FALSE;
        else {
            myServerId = Long.parseLong(myServerIdStr);
            if (myServerId.equals(intentServerId))
                iAmServer = Boolean.TRUE;
        }

        if (iAmServer) {
            Log.v("sajid", "executing server listorder for "+intentServerId.toString());
            ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
            l.setServerId(myServerId);
            l.execute();
        } else {
            Log.v("sajid","executing finder listorder");
            finderDevRegId = Prefs.getDeviceRegIdPref(this);
            if (!(finderDevRegId.equals(""))) {
                ListFinderOrdersEndpointAsyncTask l =
                        new ListFinderOrdersEndpointAsyncTask(this);
                l.setFinderDevRegId(finderDevRegId);
                l.execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.order_list_refresh:
                if (iAmServer) {
                    Log.v("sajid", "refreshing server listorder for "+myServerId);
                    ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
                    l.setServerId(myServerId);
                    l.execute();
                } else {
                    Log.v("sajid","refreshing finder listorder");
                    if (!(finderDevRegId.equals(""))) {
                        ListFinderOrdersEndpointAsyncTask l =
                                new ListFinderOrdersEndpointAsyncTask(this);
                        l.setFinderDevRegId(finderDevRegId);
                        l.execute();
                    }
                }

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (iAmServer) {
            Log.v("sajid", "confirming order");
            OrderEntity selectedOrder = orderList.get(position);
            new ConfirmOrderEndpointAsyncTask().execute(new Pair<Context, OrderEntity>(this, selectedOrder));
        }
    }

    public void showOrder(List<OrderEntity> result) {
        ArrayList<String> values = new ArrayList<String>();

        Log.v("sajid","showing order list");

        for (OrderEntity q : result) {
            orderList.add(q);
            values.add(q.getMenuId().toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

    }
}