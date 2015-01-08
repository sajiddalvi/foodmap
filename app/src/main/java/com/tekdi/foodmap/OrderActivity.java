package com.tekdi.foodmap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;

/**
 * Created by fsd017 on 12/19/14.
 */
public class OrderActivity extends ListActivity {
    private ArrayList<ParcelableOrder>orders;
    private static ArrayList<OrderEntity>orderList = new ArrayList<OrderEntity>();
    private ListOrderRowAdapter adapter;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        try{
            Intent i = this.getIntent();

            ParcelableOrder p = (ParcelableOrder) i.getParcelableExtra("com.tekdi.foodmap.ParcelableOrder");

            // if menu item already exists, bump up quantity
            int index = 0;
            boolean foundEntry = false;
            for (OrderEntity order : orderList) {
                if (order.getMenuId().equals(p.menuId)) {
                    order.setQuantity(order.getQuantity() + 1);
                    orderList.set(index,order);
                    foundEntry = true;
                    break;
                }
                index ++;
            }

            if (!foundEntry) {
                OrderEntity o = new OrderEntity();
                o.setMenuId(p.menuId);
                o.setServerId(p.serverId);
                o.setFinderDevRegId(p.finderDevRegId);
                o.setServerName(p.serverName);
                o.setOrderState(0);
                o.setMenuName(p.name);
                o.setPrice(p.price);
                o.setQuantity(p.quantity);

                // if its the first order entry, add "total"
                if (orderList.size() == 0) {
                    orderList.add(o);
                    OrderEntity dummyEntity = new OrderEntity();
                    dummyEntity.setFinderDevRegId("total");
                    dummyEntity.setMenuId((long)999);
                    orderList.add(dummyEntity);
                } else {
                    // always add new order to the top, so "total" stays last
                    orderList.add(0,o);
                }
            }

            showOrder();

        } catch(Exception e){
            Log.v("sajid","OrderActivity:"+e.getMessage());
            e.printStackTrace();
        }

        registerForContextMenu(getListView());

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

                for (OrderEntity order : orderList) {
                    new OrderEndpointAsyncTask().execute(new Pair<Context, OrderEntity>(this, order));
                }

                orderList.clear();

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_order, menu);

        Log.v("sajid","create context menu for order");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.order_edit:

                RelativeLayout linearLayout=new RelativeLayout(this);
                NumberPicker np = (NumberPicker) new NumberPicker(this);
                np.setMaxValue(99);
                np.setMinValue(0);
                np.setValue(orderList.get(info.position).getQuantity());
                np.setWrapSelectorWheel(false);
                np.setClickable(false);
                np.setEnabled(true);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50,50);
                RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

                numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                linearLayout.setLayoutParams(params);
                linearLayout.addView(np,numPicerParams);
                linearLayout.isClickable();

                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        orderList.get(info.position).setQuantity(newVal);
                    }
                });


                AlertDialog.Builder alertBw;
                alertBw=new AlertDialog.Builder(this);
                alertBw.setTitle("Select Order Quantity");
                alertBw.setView(linearLayout);
                alertBw.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (orderList.get(info.position).getQuantity() == 0) {
                            orderList.remove(info.position);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

                AlertDialog alertDw=alertBw.create();
                alertDw.show();


                return true;
            case R.id.order_delete:
                OrderEntity removed = orderList.remove(info.position);
                Log.v("sajid","removed "+removed.getMenuName());
                adapter.notifyDataSetChanged();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void showOrder() {

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);

        setListAdapter(adapter);

    }
}
