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
import android.widget.ListView;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListOrderServerActivity extends ListActivity {
    private static ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();
    private ListOrderRowAdapter adapter;
    public static final long DUMMY_TOTAL_MENU_ID = 999;
    private Menu orderActionBarMenu;
    private String action;
    private ArrayList<OrderEntity> newOrderList = new ArrayList<OrderEntity>();
    private String finderPhone = "";
    private int numItemsInOrder;
    private int numItemsConfirmed;



    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.list_order_view);

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

        ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
        l.setServerId(Long.parseLong(Prefs.getServeIdPref(this)));
        l.execute();

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);

        setListAdapter(adapter);

        registerForContextMenu(getListView());

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

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
            if (newOrderList.get(position).getFinderDevRegId().equals("total")) {
                numItemsConfirmed = 0;
                String finderDevRegId = newOrderList.get(position-1).getFinderDevRegId();
                for (OrderEntity o : orderList) {
                    if (o.getFinderDevRegId().equals(finderDevRegId)) {
                        new ConfirmOrderEndpointAsyncTask(this).execute(new Pair<Context, OrderEntity>(this, o));
                    }
                    numItemsInOrder ++;
                }
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

    public void callPressed(View v) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + finderPhone));
        startActivity(intent);
    }

    public void showOrder(List<OrderEntity> result) {

        orderList.clear();

        for (OrderEntity q : result) {
            orderList.add(q);
        }

        // Sort orders by finders
        Collections.sort(orderList, new ComparatorOrderEntity());

        ArrayList<OrderEntity> finderOrderList = new ArrayList<OrderEntity>();

        OrderEntity prev = orderList.get(0);

        finderPhone = prev.getFinderPhone();
        finderOrderList.clear();
        newOrderList.clear();

        for (OrderEntity o : orderList) {
            if (! o.getFinderDevRegId().equals(prev.getFinderDevRegId())) {
                // if new finder found, copy finderList into newList
                newOrderList.addAll(finderOrderList);
                // add dummy total at the end
                OrderEntity dummyEntity = new OrderEntity();
                dummyEntity.setFinderDevRegId("total");
                dummyEntity.setMenuId((long) DUMMY_TOTAL_MENU_ID);
                dummyEntity.setOrderState(prev.getOrderState());
                dummyEntity.setFinderDevRegId(prev.getFinderDevRegId());
                newOrderList.add(dummyEntity);
                // clean up for new finder and add current order to new finderList
                finderOrderList.clear();
                finderOrderList.add(o);
                prev = o;
            } else {
                // as long as finder has not changed, keep adding to finderList
                finderOrderList.add(o);
            }
        }

        // add the last finderList to newList
        newOrderList.addAll(finderOrderList);
        // add dummy total
        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("total");
        dummyEntity.setMenuId((long) DUMMY_TOTAL_MENU_ID);
        dummyEntity.setOrderState(prev.getOrderState());
        dummyEntity.setFinderDevRegId(prev.getFinderDevRegId());

        newOrderList.add(dummyEntity);

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                newOrderList);

        adapter.setIAmServer(true);

        setListAdapter(adapter);

        setTitle("Pending Orders");
    }

}
