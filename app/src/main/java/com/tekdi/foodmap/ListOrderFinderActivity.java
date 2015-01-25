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
import android.widget.TextView;
import android.widget.Toast;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListOrderFinderActivity extends ListActivity {

    private static ArrayList<OrderEntity> orderList = new ArrayList<>();

    private ListOrderRowAdapter adapter;
    public static final long DUMMY_TOTAL_MENU_ID = 999;
    public static final long DUMMY_NAME_MENU_ID = 998;

    private Menu orderActionBarMenu;
    private String action;

    private static boolean pendingOrder = false;

    private ArrayList<OrderEntity> displayOrderList = new ArrayList<>();
    private ArrayList<OrderEntity> serverOrderList = new ArrayList<>();
    private ArrayList<OrderEntity> packedServerOrderList = new ArrayList<>();

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.list_order_view);
        registerForContextMenu(getListView());

        Intent intent = getIntent();
        action = intent.getStringExtra("action");

        if ((action!= null) && action.equals("new_order")) {
            setTitle("New Order");

            ParcelableFinderOrder p =
                    intent.getParcelableExtra("com.tekdi.foodmap.ParcelableFinderOrder");

            // if we have a new order,
            // and there is already a pending order
            // check if new order is from the same server as pending order
            // if not give user a warning to first finish pending order

            // pending order will be cleared when the order is sent to the server
            // or if the order is cleared

            if (pendingOrder) {

                boolean isSameSever = true;
                for (OrderEntity op : orderList) {
                    if (op.getServerId() == null)
                        continue;
                    if (!(p.serverId.equals(op.getServerId()))) {
                        isSameSever = false;
                        break;
                    }
                }
                if (isSameSever) {
                    processNewOrder(p);
                } else {
                    finishNewOrderWarning();
                }
            } else {
                orderList.clear();
                pendingOrder = true;
                processNewOrder(p);
            }

        } else if ( (action!= null) && action.equals("notification")) {
            if (!pendingOrder) {
                setTitle("Order Update");
                processOrderReceived();
            }
        } else {
            if (!pendingOrder) {
                setTitle("Order Status");
                Log.v("sajid", "executing listfinderorder");
                getOrderListFromServer();
            }
        }

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);
        setListAdapter(adapter);

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
                NumberPicker np = new NumberPicker(this);
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
            if (orderList.size() == 0) {
                orderList.add(getDummyNameRow(o,OrderState.ORDER_STATE_NEW));
                orderList.add(o);
                orderList.add(getDummyTotalRow(o,OrderState.ORDER_STATE_NEW));
            } else {
                orderList.add(1, o);
            }

        } else {
            OrderEntity o = orderList.get(index);
            o.setQuantity(o.getQuantity() + 1);
            orderList.set(index, o);
        }

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);

        adapter.notifyDataSetChanged();

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
        setTitle("Orders");
        getOrderListFromServer();
        adapter.notifyDataSetChanged();
    }

    private void processOrderReceived() {
        setTitle("Orders");
        getOrderListFromServer();
        adapter.notifyDataSetChanged();
    }

    public void onListOrderButtonClick(View view) {
        long serverId = (long)view.getTag();
        TextView buttonView = (TextView) view;

        if (buttonView.getText().equals("Add")) {
            // if its a new order, finish OrderActivity which will take us back to ListMenu
                finish();
        }

        else if (buttonView.getText().equals("Clear")) {
            orderList.clear();
            pendingOrder = false;
            finish();
        }

        else if (buttonView.getText().equals("Send")) {
            for (OrderEntity order : orderList) {
                Log.v("sajid","view serverId = " + serverId);
                Log.v("sajid","order serverId = " + order.getServerId());

                if ((order.getServerId() == serverId) &&      // check for serverId
                    (order.getMenuId() != DUMMY_TOTAL_MENU_ID) &&  // don't send total
                    (order.getMenuId() != DUMMY_NAME_MENU_ID))     // don't send name
                        new OrderEndpointAsyncTask(this).execute(new Pair<Context, OrderEntity>(this, order));
            }
            pendingOrder = false;
            adapter.notifyDataSetChanged();
        }
    }

    private void addToDisplayList(OrderEntity prev) {

        int displayOrderState = 99;

        packedServerOrderList.clear();

        Collections.sort(serverOrderList, new ComparatorMenuOrderEntity());

        // check for dups and if they exist, then pack them
        OrderEntity prevPacked = null;
        for (OrderEntity so : serverOrderList) {
            if (so.getOrderState() < displayOrderState) {
                displayOrderState = so.getOrderState();
            }

            if (packedServerOrderList.size() == 0) {
                packedServerOrderList.add(so);
                prevPacked = so;
            } else {
                if (so.getMenuId().equals(prevPacked.getMenuId())) {
                    prevPacked.setQuantity(prevPacked.getQuantity()+so.getQuantity());
                } else {
                    packedServerOrderList.add(so);
                    prevPacked = so;
                }
            }
        }

        // if new finder found, copy finderList into newList
        displayOrderList.add(getDummyNameRow(packedServerOrderList.get(0), displayOrderState));
        displayOrderList.addAll(packedServerOrderList);
        // add dummy total at the end
        displayOrderList.add(getDummyTotalRow(prev,displayOrderState));
    }

    public void showOrder(List<OrderEntity> result) {

        if (result == null) {
            Toast.makeText(this, "No orders found. Lets look for food.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, FindActivity.class);
                startActivity(intent);
                finish();
                return;
        }

        serverOrderList.clear();
        displayOrderList.clear();

        // Sort orders by servers
        Collections.sort(result, new ComparatorFinderOrderEntity());

        OrderEntity prev = result.get(0);
        for (OrderEntity o : result) {
            if (!o.getServerId().equals(prev.getServerId())) {

                // new server found, add the previous server
                addToDisplayList(prev);

                // clean up for new finder and add current order to new finderList
                serverOrderList.clear();
                prev = o;
            }

            serverOrderList.add(o);
        }

        // add the server to the list
        addToDisplayList(prev);

        orderList=displayOrderList;

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);
        setListAdapter(adapter);

        setTitle("Orders");

    }

    private void finishNewOrderWarning() {
        Toast.makeText(this, "Finish exiting new order first.", Toast.LENGTH_LONG).show();
        setTitle("New Order");
    }

    private OrderEntity getDummyTotalRow(OrderEntity base, int orderState) {
        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("total");
        dummyEntity.setMenuName("zzz");
        dummyEntity.setMenuId(DUMMY_TOTAL_MENU_ID);
        dummyEntity.setOrderState(orderState);
        dummyEntity.setFinderDevRegId(base.getFinderDevRegId());
        dummyEntity.setServerId(base.getServerId());
        dummyEntity.setMenuName("dummy total");

        return dummyEntity;
    }

    private OrderEntity getDummyNameRow(OrderEntity base, int orderState) {
        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("name");
        dummyEntity.setServerId(base.getServerId());
        dummyEntity.setMenuName("aaa");
        dummyEntity.setMenuId(DUMMY_NAME_MENU_ID);
        dummyEntity.setQuantity(0);
        dummyEntity.setPrice((float) 0);
        dummyEntity.setOrderState(orderState);
        dummyEntity.setServerPhone(base.getServerPhone());
        dummyEntity.setServerName(base.getServerName());
        dummyEntity.setTimestamp(base.getTimestamp());
        dummyEntity.setMenuName("dummy name");

        return dummyEntity;
    }

    private void getOrderListFromServer() {
        orderList.clear();
        ListFinderOrdersEndpointAsyncTask l = new ListFinderOrdersEndpointAsyncTask(this);
        l.setFinderDevRegId(Prefs.getDeviceRegIdPref(this));
        l.execute();
    }
}
