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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListOrderServerActivity extends ListActivity {
    private static ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();
    private ListOrderRowAdapter adapter;
    private Menu orderActionBarMenu;
    private String action;
    private ArrayList<OrderEntity> newOrderList = new ArrayList<OrderEntity>();
    private String finderPhone = "";
    private int numItemsInOrder;
    private int numItemsConfirmed;

    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setContentView(R.layout.list_order_view);
        registerForContextMenu(getListView());
        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);
        setListAdapter(adapter);

        Intent intent = getIntent();
        action = intent.getStringExtra("action");

        if ( (action!= null) && action.equals("notification")) {
            Integer notificationId = intent.getIntExtra("notificationId", 0);

            if (notificationId == NotificationId.ORDER_UPDATE_NOTIFICATION_ID) {
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(NotificationId.ORDER_UPDATE_NOTIFICATION_ID);
            }
        }

        getOrderListFromServer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_server, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuItem menuItem;
        switch(item.getItemId()) {
            case R.id.order_list_refresh:
                ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
                l.setServerId(Long.parseLong(Prefs.getServeIdPref(this)));
                l.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
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
            case R.id.order_receive:
                numItemsInOrder = 0;
                numItemsConfirmed = 0;
                OrderEntity op = newOrderList.get(info.position);
                String finderDevRegId = op.getFinderDevRegId();
                for (OrderEntity o : orderList) {
                    if (o.getFinderDevRegId().equals(finderDevRegId)) {
                        new ConfirmOrderEndpointAsyncTask(this).execute(new Pair<Context, OrderEntity>(this, o));
                    }
                    numItemsInOrder ++;
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

    public void showOrder(List<OrderEntity> result) {

        if (result == null) {
            Toast.makeText(this, "No orders received.", Toast.LENGTH_LONG).show();
            finish();
        }

        ArrayList<OrderEntity> remoteOrderList = new ArrayList<OrderEntity>();
        ArrayList<OrderEntity> finderOrderList = new ArrayList<OrderEntity>();

        finderOrderList.clear();
        remoteOrderList.clear();

        // Sort orders by finders
        Collections.sort(result, new ComparatorOrderEntity());

        OrderEntity prev = result.get(0);
        for (OrderEntity o : result) {
            if (! o.getFinderDevRegId().equals(prev.getFinderDevRegId())) {
                // if new finder found, copy finderList into newList
                remoteOrderList.addAll(finderOrderList);
                // add dummy total at the end
                remoteOrderList.add(getDummyTotalRow(prev));
                // clean up for new finder and add current order to new finderList
                finderOrderList.clear();
                prev = o;
            }
            if (finderOrderList.size() == 0) {
                finderOrderList.add(getDummyNameRow(prev));
            }
            finderOrderList.add(o);
        }

        // add the last finderList to newList
        remoteOrderList.addAll(finderOrderList);
        // add dummy total
        remoteOrderList.add(getDummyTotalRow(prev));

        orderList=remoteOrderList;

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);
        adapter.setIAmServer(true);
        setListAdapter(adapter);

        setTitle("Orders");
    }

    private OrderEntity getDummyTotalRow(OrderEntity base) {
        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("total");
        dummyEntity.setMenuId((long) ListOrderFinderActivity.DUMMY_TOTAL_MENU_ID);
        dummyEntity.setOrderState(base.getOrderState());
        dummyEntity.setFinderDevRegId(base.getFinderDevRegId());
        dummyEntity.setServerId(base.getServerId());
        dummyEntity.setMenuName("dummy total");

        return dummyEntity;
    }

    private OrderEntity getDummyNameRow(OrderEntity base) {
        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("name");
        dummyEntity.setMenuId((long) ListOrderFinderActivity.DUMMY_NAME_MENU_ID);
        dummyEntity.setQuantity(0);
        dummyEntity.setPrice((float)0);
        dummyEntity.setFinderPhone(base.getFinderPhone());
        dummyEntity.setTimestamp(base.getTimestamp());
        dummyEntity.setMenuName("dummy name");

        return dummyEntity;
    }

    private void getOrderListFromServer() {
        orderList.clear();
        ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
        l.setServerId(Long.parseLong(Prefs.getServeIdPref(this)));
        l.execute();
    }

}
