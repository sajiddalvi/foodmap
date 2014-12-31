package com.tekdi.foodmap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;

/**
 * Created by fsd017 on 12/19/14.
 */
public class OrderActivity extends ListActivity {
    private ArrayList<ParcelableOrder>orders;
    private ArrayList<OrderEntity>orderList = new ArrayList<OrderEntity>();

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        try{

            Intent i = this.getIntent();

            orders = i.getParcelableArrayListExtra("com.tekdi.foodmap.ParcelableOrder");

            OrderEntity o = new OrderEntity();
            o.setMenuId(orders.get(0).menuId);
            o.setServerId(orders.get(0).serverId);
            o.setFinderDevRegId(orders.get(0).finderDevRegId);
            o.setServerName(orders.get(0).serverName);
            o.setOrderState(0);
            o.setMenuName(orders.get(0).name);
            o.setPrice(orders.get(0).price);
            o.setQuantity(orders.get(0).quantity);

            orderList.add(o);

            showOrder();

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.confirm_menu:
                Log.v("sajid","Confirm Order");
                OrderEntity o = new OrderEntity();
                o.setMenuId(orders.get(0).menuId);
                o.setServerId(orders.get(0).serverId);
                o.setFinderDevRegId(orders.get(0).finderDevRegId);
                o.setServerName(orders.get(0).serverName);
                o.setOrderState(0);
                o.setMenuName(orders.get(0).name);
                o.setPrice(orders.get(0).price);
                o.setQuantity(orders.get(0).quantity);

                Log.v("sajid","OrderActivity:onConfirm serverName="+o.getServerName());

                new OrderEndpointAsyncTask().execute(new Pair<Context, OrderEntity>(this, o));

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void showOrder() {

        ListOrderRowAdapter adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);

        setListAdapter(adapter);

    }
}
