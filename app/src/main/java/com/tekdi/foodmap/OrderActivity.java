package com.tekdi.foodmap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;

/**
 * Created by fsd017 on 12/19/14.
 */
public class OrderActivity extends ListActivity {
    private ArrayList<ParcelableOrder>orders;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        try{

            Intent i = this.getIntent();

            orders = i.getParcelableArrayListExtra("com.tekdi.foodmap.ParcelableOrder");

            Log.v("sajid","in order activity size = "+orders.size());

            for(int index = 0; index < orders.size(); index++){
                Log.v("sajid","index="+index);
                Log.v("sajid","menuId="+orders.get(index).menuId);
            }

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
                o.setOrderState(0);
                o.setMenuName(orders.get(0).name);
                o.setPrice(orders.get(0).price);
                o.setQuantity(orders.get(0).quantity);

                new OrderEndpointAsyncTask().execute(new Pair<Context, OrderEntity>(this, o));

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void showOrder() {
        ArrayList<String> values = new ArrayList<String>();


        for (ParcelableOrder o : orders) {
            values.add(o.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

    }
}
