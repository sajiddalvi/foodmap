package com.tekdi.foodmap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import android.widget.Toast;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fsd017 on 1/11/15.
 */
public class ListOrderFinderActivity extends ListActivity {

    private static ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();
    private static ArrayList<OrderEntity> pendingOrderList = new ArrayList<OrderEntity>();

    private ListOrderRowAdapter adapter;
    public static final long DUMMY_TOTAL_MENU_ID = 999;
    public static final long DUMMY_NAME_MENU_ID = 998;

    private Menu orderActionBarMenu;
    private String action;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.list_order_view);

        registerForContextMenu(getListView());

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);

        setListAdapter(adapter);

        Intent intent = getIntent();
        action = intent.getStringExtra("action");

        if ((action!= null) && action.equals("new_order")) {
            setTitle("New Order");
            ParcelableFinderOrder p = (ParcelableFinderOrder)
                    intent.getParcelableExtra("com.tekdi.foodmap.ParcelableFinderOrder");
            processNewOrder(p);

        } else if ((pendingOrderList != null)&&(pendingOrderList.size() > 0)) {
            finishNewOrderWarning();
        } else if ( (action!= null) && action.equals("notification")) {
            setTitle("Order Update");
            processOrderReceived();
        } else {
            setTitle("Order Status");
            Log.v("sajid", "executing listfinderorder");
            getOrderListFromServer();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        orderActionBarMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (orderList.size() > 0) {
            int orderState = orderList.get(0).getOrderState();
            setupActionBarButtons(orderState);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuItem menuItem;
        switch(item.getItemId()) {
            case R.id.order_send:
               for (OrderEntity order : orderList) {
                    if ((order.getMenuId() != DUMMY_TOTAL_MENU_ID) &&
                        (order.getMenuId() != DUMMY_NAME_MENU_ID)) // dont't send total and name
                        new OrderEndpointAsyncTask(this).execute(new Pair<Context, OrderEntity>(this, order));
                }

                menuItem = orderActionBarMenu.findItem(R.id.order_add);
                menuItem.setVisible(false);
                menuItem = orderActionBarMenu.findItem(R.id.order_send);
                menuItem.setVisible(false);
                menuItem = orderActionBarMenu.findItem(R.id.order_clear);
                menuItem.setVisible(false);

                break;

            case R.id.order_add:
                if ((action!= null) && action.equals("new_order"))
                    finish();
                else {
                    // we've come to the ListOrder, not from ListMenu
                    // but somewhere else (since new_order is not set),
                    // so setup for showing ListMenuActivity
                    OrderEntity o = orderList.get(0);
                    Intent intent = new Intent(this, ListMenuActivity.class);
                    intent.putExtra("serverId", o.getServerId());
                    intent.putExtra("serverName", o.getServerName());
                    intent.putExtra("source","finder");
                    intent.putExtra("phone",o.getServerPhone());
                    intent.putExtra("address",o.getServerAddress());

                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.order_clear:
                orderList.clear();
                adapter.notifyDataSetChanged();

                menuItem = orderActionBarMenu.findItem(R.id.order_send);
                menuItem.setVisible(false);
                menuItem = orderActionBarMenu.findItem(R.id.order_clear);
                menuItem.setVisible(false);

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
                RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

                numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                linearLayout.setLayoutParams(params);
                linearLayout.addView(np,numPickerParams);
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
                OrderEntity o = orderList.get(info.position);

                if (!(o.getMenuId().equals(DUMMY_TOTAL_MENU_ID)) &&
                    !(o.getMenuId().equals(DUMMY_NAME_MENU_ID))) {
                    orderList.remove(info.position);

                    if (orderList.size() == 1) // only total left, clear the list
                        orderList.clear();
                    adapter.notifyDataSetChanged();
                }
                return true;

            case R.id.order_call:
                String phone = orderList.get(info.position).getServerPhone();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent);

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void processNewOrder(ParcelableFinderOrder p) {

        int index = findMenu(p.menuId);
        if (index < 0) { // index will be -1 if not found

            TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

            OrderEntity o = new OrderEntity();
            o.setServerId(p.serverId);
            o.setServerName(p.serverName);
            o.setServerAddress(p.serverAddress);
            o.setServerPhone(p.serverPhone);
            o.setFinderDevRegId(Prefs.getDeviceRegIdPref(this));
            o.setFinderPhone(tMgr.getLine1Number());
            o.setMenuId(p.menuId);
            o.setMenuName(p.menuName);
            o.setPrice(p.price);
            o.setQuantity(p.quantity);
            o.setOrderState(OrderState.ORDER_STATE_NEW);

            // if its the first order entry, add "total"
            if (pendingOrderList.size() == 0) {
                pendingOrderList.add(getDummyNameRow(o));
                pendingOrderList.add(o);
                pendingOrderList.add(getDummyTotalRow(o));
            } else {

                //check if this is an order for a new server
                boolean isSameSever = true;
                for (OrderEntity op : pendingOrderList) {
                    Log.v("sajid","so="+o.getServerId().toString()+",sop="+op.getServerId().toString());
                    if (!(o.getServerId().equals(op.getServerId()))) {
                        isSameSever = false;
                        break;
                    }
                }
                if (isSameSever)
                    // always add new order below name
                    pendingOrderList.add(1, o);
                else
                    finishNewOrderWarning();
            }

        } else {
            OrderEntity o = pendingOrderList.get(index);
            o.setQuantity(o.getQuantity() + 1);
            pendingOrderList.set(index, o);
        }
        orderList = pendingOrderList;

    }

    public int findMenu(Long menuId) {
        int index = 0;
        boolean found = false;

        for (OrderEntity order : orderList) {
            if (order.getMenuId().equals(menuId)) {
                found = true;
                break;
            }
            index ++;
        }
        if (found)
            return index;
        else
            return -1;
    }

    public void onPostExecute(String result) {
        String statusMessage;

        if (result.equals("new order success")) {
            statusMessage = "order sent";

            processOrderSent();
        }
        else
            statusMessage = "order addition failed";

        Toast.makeText(this, statusMessage, Toast.LENGTH_LONG).show();
    }

    public void onPostExecuteListOrder(List<OrderEntity> result) {
        showOrder(result);
    }

    private void processOrderSent() {
        setupActionBarButtons(OrderState.ORDER_STATE_SEND);
        setTitle("Orders");
        getOrderListFromServer();
        adapter.notifyDataSetChanged();
    }

    private void processOrderReceived() {

        getOrderListFromServer();
        setupActionBarButtons(OrderState.ORDER_STATE_RECEIVE);
        setTitle("Orders");
        adapter.notifyDataSetChanged();

    }

    private void setupActionBarButtons(int orderState) {
        MenuItem menuItem;
        switch (orderState) {
            case OrderState.ORDER_STATE_NEW :
                menuItem = orderActionBarMenu.findItem(R.id.order_add);
                menuItem.setVisible(true);
                menuItem = orderActionBarMenu.findItem(R.id.order_send);
                menuItem.setVisible(true);
                menuItem = orderActionBarMenu.findItem(R.id.order_clear);
                menuItem.setVisible(true);
                break;
            case OrderState.ORDER_STATE_SEND :
                menuItem = orderActionBarMenu.findItem(R.id.order_add);
                menuItem.setVisible(false);
                menuItem = orderActionBarMenu.findItem(R.id.order_send);
                menuItem.setVisible(false);
                menuItem = orderActionBarMenu.findItem(R.id.order_clear);
                menuItem.setVisible(false);
                menuItem = orderActionBarMenu.findItem(R.id.order_update);
                menuItem.setVisible(true);
                menuItem = orderActionBarMenu.findItem(R.id.order_cancel);
                menuItem.setVisible(true);
                break;
            case OrderState.ORDER_STATE_RECEIVE :
                break;
        }
    }

    public void showOrder(List<OrderEntity> result) {

        if (result == null) {
                Intent intent = new Intent(this, FindActivity.class);
                startActivity(intent);
                finish();
        }

        ArrayList<OrderEntity> remoteOrderList = new ArrayList<OrderEntity>();
        ArrayList<OrderEntity> serverOrderList = new ArrayList<OrderEntity>();

        serverOrderList.clear();
        remoteOrderList.clear();

        // Sort orders by finders
        Collections.sort(result, new ComparatorFinderOrderEntity());

        OrderEntity prev = result.get(0);
        for (OrderEntity o : result) {
            Log.v("sajid","o = "+o.getMenuName());
            if (!o.getServerId().equals(prev.getServerId())) {
                // if new finder found, copy finderList into newList
                remoteOrderList.addAll(serverOrderList);
                // add dummy total at the end
                remoteOrderList.add(getDummyTotalRow(prev));
                // clean up for new finder and add current order to new finderList
                serverOrderList.clear();
                prev = o;
            }
            if (serverOrderList.size() == 0) {
                serverOrderList.add(getDummyNameRow(prev));
                Log.v("sajid","adding name size="+serverOrderList.size());

            }
            serverOrderList.add(o);
        }

        // add the last finderList to newList
        remoteOrderList.addAll(serverOrderList);
        // add dummy total
        remoteOrderList.add(getDummyTotalRow(prev));

        orderList=remoteOrderList;

        for (OrderEntity ol : orderList) {
            Log.v("sajid",ol.getMenuName());
        }

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);
        setListAdapter(adapter);

        setTitle("Orders");

    }

    private void finishNewOrderWarning() {
        Toast.makeText(this, "Finish exiting new order first.", Toast.LENGTH_LONG).show();
        orderList = pendingOrderList;

        setTitle("New Order");
    }

    private OrderEntity getDummyTotalRow(OrderEntity base) {
        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("total");
        dummyEntity.setMenuId((long) DUMMY_TOTAL_MENU_ID);
        dummyEntity.setOrderState(base.getOrderState());
        dummyEntity.setFinderDevRegId(base.getFinderDevRegId());
        dummyEntity.setServerId(base.getServerId());
        dummyEntity.setMenuName("dummy total");

        return dummyEntity;
    }

    private OrderEntity getDummyNameRow(OrderEntity base) {
        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("name");
        dummyEntity.setMenuId((long) DUMMY_NAME_MENU_ID);
        dummyEntity.setQuantity(0);
        dummyEntity.setPrice((float)0);
        dummyEntity.setServerPhone(base.getServerPhone());
        dummyEntity.setServerName(base.getServerName());
        dummyEntity.setTimestamp(base.getTimestamp());
        dummyEntity.setMenuName("dummy name");

        return dummyEntity;
    }

    private void getOrderListFromServer() {
        pendingOrderList.clear();
        orderList.clear();
        ListFinderOrdersEndpointAsyncTask l = new ListFinderOrdersEndpointAsyncTask(this);
        l.setFinderDevRegId(Prefs.getDeviceRegIdPref(this));
        l.execute();
    }
}
