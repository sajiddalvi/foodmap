package com.tekdi.foodmap;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListOrderServerActivity extends ListActivity {
    private static ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();
    private ListOrderRowAdapter adapter;
    private int numItemsInOrder;
    private int numItemsConfirmed;

    private ArrayList<OrderEntity> displayOrderList = new ArrayList<>();
    private ArrayList<OrderEntity> finderOrderList = new ArrayList<>();
    private ArrayList<OrderEntity> packedFinderOrderList = new ArrayList<>();

    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setContentView(R.layout.list_order_view);
        registerForContextMenu(getListView());
        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);
        setListAdapter(adapter);

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");

        if ( (action!= null) && action.equals("notification")) {
            Integer notificationId = intent.getIntExtra("notificationId", 0);

            if (notificationId == NotificationId.ORDER_UPDATE_NOTIFICATION_ID) {
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(NotificationId.ORDER_UPDATE_NOTIFICATION_ID);
            }
        }

        getOrderListFromServer();

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);
        adapter.setIAmServer(true);
        setListAdapter(adapter);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_order_server, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.order_accept:
                numItemsInOrder = 0;
                numItemsConfirmed = 0;
                OrderEntity op = orderList.get(info.position);
                String finderDevRegId = op.getFinderDevRegId();
                for (OrderEntity o : orderList) {
                    if (o.getFinderDevRegId().equals(finderDevRegId)) {
                        if ((o.getMenuId() != ListOrderFinderActivity.DUMMY_TOTAL_MENU_ID) &&
                                (o.getMenuId() != ListOrderFinderActivity.DUMMY_NAME_MENU_ID)) {
                            UpdateOrderStateServerEndpointAsyncTask task = new UpdateOrderStateServerEndpointAsyncTask(this);
                            task.setOrderState(OrderState.ORDER_STATE_RECEIVE);
                            task.execute(new Pair<Context, OrderEntity>(this, o));
                            numItemsInOrder ++;
                        }
                    }
                }

                return true;

            case R.id.order_ready:
                numItemsInOrder = 0;
                numItemsConfirmed = 0;
                op = orderList.get(info.position);
                finderDevRegId = op.getFinderDevRegId();
                for (OrderEntity o : orderList) {
                    if (o.getFinderDevRegId().equals(finderDevRegId)) {
                        if ((o.getMenuId() != ListOrderFinderActivity.DUMMY_TOTAL_MENU_ID) &&
                                (o.getMenuId() != ListOrderFinderActivity.DUMMY_NAME_MENU_ID)) {
                            UpdateOrderStateServerEndpointAsyncTask task = new UpdateOrderStateServerEndpointAsyncTask(this);
                            task.setOrderState(OrderState.ORDER_STATE_READY);
                            task.execute(new Pair<Context, OrderEntity>(this, o));                            numItemsInOrder ++;
                        }
                    }
                }

                return true;

            case R.id.order_cancel:
                return true;

            case R.id.order_call:
                String phone = orderList.get(info.position).getFinderPhone();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent);

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onListOrderButtonClick(View view) {
        String finderDevRegId = (String)view.getTag();
        TextView buttonView = (TextView) view;

        if (buttonView.getText().equals("Accept")) {

            numItemsInOrder = 0;
            numItemsConfirmed = 0;

            for (OrderEntity o : orderList) {
                if (o.getFinderDevRegId().equals(finderDevRegId)) {
                    if ((o.getMenuId() != ListOrderFinderActivity.DUMMY_TOTAL_MENU_ID) &&
                            (o.getMenuId() != ListOrderFinderActivity.DUMMY_NAME_MENU_ID)) {
                        UpdateOrderStateServerEndpointAsyncTask task = new UpdateOrderStateServerEndpointAsyncTask(this);
                        task.setOrderState(OrderState.ORDER_STATE_RECEIVE);
                        task.execute(new Pair<Context, OrderEntity>(this, o));
                        numItemsInOrder ++;
                    }
                }
            }

        }

        else if (buttonView.getText().equals("Cancel")) {
            orderList.clear();
            finish();
        }

        else if (buttonView.getText().equals("Ready")) {

            numItemsInOrder = 0;
            numItemsConfirmed = 0;

            for (OrderEntity o : orderList) {
                if (o.getFinderDevRegId().equals(finderDevRegId)) {
                    if ((o.getMenuId() != ListOrderFinderActivity.DUMMY_TOTAL_MENU_ID) &&
                            (o.getMenuId() != ListOrderFinderActivity.DUMMY_NAME_MENU_ID)) {
                        UpdateOrderStateServerEndpointAsyncTask task = new UpdateOrderStateServerEndpointAsyncTask(this);
                        task.setOrderState(OrderState.ORDER_STATE_READY);
                        task.execute(new Pair<Context, OrderEntity>(this, o));
                        numItemsInOrder ++;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void onPostExecute() {

        numItemsConfirmed ++;
        Log.v("sajid", "refresh order tot=" + numItemsInOrder + ",cnf=" + numItemsConfirmed);
        if (numItemsConfirmed == numItemsInOrder) {
            orderList.clear();

            ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
            l.setServerId(Long.parseLong(Prefs.getServeIdPref(this)));
            l.execute();

        }
    }

    private void addToDisplayList(OrderEntity prev) {

        int displayOrderState = 99;

        packedFinderOrderList.clear();

        Collections.sort(finderOrderList, new ComparatorMenuOrderEntity());

        // check for dups and if they exist, then pack them
        OrderEntity prevPacked = null;
        for (OrderEntity so : finderOrderList) {
            if (so.getOrderState() < displayOrderState) {
                displayOrderState = so.getOrderState();
            }

            if (packedFinderOrderList.size() == 0) {
                packedFinderOrderList.add(so);
                prevPacked = so;
            } else {
                if (so.getMenuId().equals(prevPacked.getMenuId())) {
                    prevPacked.setQuantity(prevPacked.getQuantity()+so.getQuantity());
                } else {
                    packedFinderOrderList.add(so);
                    prevPacked = so;
                }
            }
        }

        // if new finder found, copy finderList into newList
        displayOrderList.add(getDummyNameRow(packedFinderOrderList.get(0), displayOrderState));
        displayOrderList.addAll(packedFinderOrderList);
        // add dummy total at the end
        displayOrderList.add(getDummyTotalRow(prev,displayOrderState));
    }


    public void showOrder(List<OrderEntity> result) {

        if (result == null) {
            Toast.makeText(this, "No orders received.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        finderOrderList.clear();
        displayOrderList.clear();

        // Sort orders by servers
        Collections.sort(result, new ComparatorFinderOrderEntity());

        OrderEntity prev = result.get(0);
        for (OrderEntity o : result) {
            if (!o.getFinderDevRegId().equals(prev.getFinderDevRegId())) {

                // new server found, add the previous server
                addToDisplayList(prev);

                // clean up for new finder and add current order to new finderList
                finderOrderList.clear();
                prev = o;
            }

            finderOrderList.add(o);
        }

        // add the finder to the list
        addToDisplayList(prev);

        orderList=displayOrderList;

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);
        adapter.setIAmServer(true);

        setListAdapter(adapter);

        setTitle("Orders");

    }

    private OrderEntity getDummyTotalRow(OrderEntity base, int orderState) {
        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("total");
        dummyEntity.setMenuName("zzz");
        dummyEntity.setMenuId(ListOrderFinderActivity.DUMMY_TOTAL_MENU_ID);
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
        dummyEntity.setId(base.getId());
        dummyEntity.setMenuName("aaa");
        dummyEntity.setMenuId(ListOrderFinderActivity.DUMMY_NAME_MENU_ID);
        dummyEntity.setQuantity(0);
        dummyEntity.setPrice((float) 0);
        dummyEntity.setOrderState(orderState);
        dummyEntity.setFinderPhone(base.getFinderPhone());
        dummyEntity.setServerPhone(base.getServerPhone());
        dummyEntity.setServerAddress(base.getServerAddress());
        dummyEntity.setServerName(base.getServerName());
        dummyEntity.setTimestamp(base.getTimestamp());
        dummyEntity.setMenuName("dummy name");

        return dummyEntity;
    }

    private void getOrderListFromServer() {
        if (Prefs.getServeIdPref(this).equals("")) {
            Toast.makeText(this, "Setup Catering Service first.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            orderList.clear();
            ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
            l.setServerId(Long.parseLong(Prefs.getServeIdPref(this)));
            l.execute();
        }
    }

    private int getOrderState(String finder) {
        for (OrderEntity order : orderList) {
            if (order.getFinderDevRegId().equals(finder)) {
                return order.getOrderState();
            }
        }
        return OrderState.ORDER_STATE_NEW;
    }

}
