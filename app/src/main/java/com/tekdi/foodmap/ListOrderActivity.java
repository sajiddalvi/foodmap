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

    private Long serverId;
    private ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();
    //private ArrayList<ParcelableOrder> orderList = new ArrayList<ParcelableOrder>();

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Log.v("sajid","ListOrderActivity onCreate");

        Intent intent = getIntent();

        String intentSeverId = intent.getStringExtra("serverId");
        Long orderServerId = Long.parseLong(intentSeverId);
        serverId = Long.parseLong(Prefs.getServeIdPref(this));

        if (serverId.equals(orderServerId)) {
            Log.v("sajid", "executing server listorder for "+serverId.toString());
            ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
            l.setServerId(serverId);
            l.execute();
        } else {
            Log.v("sajid","executing finder listorder");

            Long orderId = Long.parseLong(intent.getStringExtra("orderId"));
            Long menuId = Long.parseLong(intent.getStringExtra("menuId"));
            String finder = intent.getStringExtra("finder");
            Integer state = Integer.parseInt(intent.getStringExtra("state"));


            Log.v("sajid","ListOrderActivity:orderId="+orderId+","
                       + "orderServerId="+orderServerId+","
                       + "menuId="+menuId+","
                       + "finder="+finder+","
                       + "state="+state
                        );


            int limit = 10;
            List<OrderEntity> ol = new ArrayList<OrderEntity>(limit);

            OrderEntity order = new OrderEntity();
            order.setId(orderId);
            order.setServerId(orderServerId);
            order.setMenuId(menuId);
            order.setFinderDevRegId(finder);
            order.setOrderState(state);
            ol.add(order);

            showOrder(ol);
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
                Log.v("sajid","Refresh List Order");
                if (serverId != 0) {
                    Log.v("sajid","refreshing listorder");
                    ListOrdersEndpointAsyncTask task = new ListOrdersEndpointAsyncTask(this);
                    task.setServerId(serverId);
                    task.execute();
                }

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Log.v("sajid","confirming order");

        OrderEntity selectedOrder = orderList.get(position);

        new ConfirmOrderEndpointAsyncTask().execute(new Pair<Context, OrderEntity>(this, selectedOrder));


        /*
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();

        MenuEntity m = menuList.get(position);

        ParcelableOrder p = new ParcelableOrder();
        p.menuId = m.getId();
        p.finderDevRegId = Prefs.getDeviceRegIdPref(this);
        p.serverId = m.getServerId();
        p.orderState = 0;
        p.name = m.getName();
        p.description = m.getDescription();
        p.quantity = 1;
        p.price = m.getPrice();

        orderList.add(p);

        Intent intent = new Intent(this, OrderActivity.class);
        intent.putParcelableArrayListExtra("com.tekdi.foodmap.ParcelableOrder", orderList);
        startActivity(intent);
*/

/*
            String serverId = Prefs.getServeIdPref(this);
            Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("serverId", Long.parseLong(serverId));
            intent.putExtra("serverId", Long.parseLong(serverId));
            startActivity(intent);
*/

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