package com.tekdi.foodmap;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.ListView;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListOrderActivity extends ListActivity {

    private Boolean iAmServer = Boolean.FALSE;
    private Long myServerId;
    private String finderDevRegId = "";
    private ListOrderRowAdapter adapter;
    private ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();
    private ArrayList<OrderEntity> newOrderList = new ArrayList<OrderEntity>();
    private int numItemsInOrder;
    private int numItemsConfirmed;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.list_order_view);

        registerForContextMenu(getListView());

        orderList.clear();
        numItemsInOrder = 0;
        numItemsConfirmed = 0;

        Intent intent = getIntent();
        String intentServerIdStr = intent.getStringExtra("serverId");
        Integer notificationId = intent.getIntExtra("notificationId", 0);

        if (notificationId == 861) {
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(861);
        }

        Long intentServerId =new Long(0);

        if ((intentServerIdStr != null)&&(!(intentServerIdStr.equals(""))))
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
    public void onResume() {
        super.onResume();
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
                refreshOrder();
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.order_edit:
                return true;
            case R.id.menu_delete:
                orderList.remove(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.v("sajid","onlistitemclicked");

        if (iAmServer) {
             if (newOrderList.get(position).getFinderDevRegId().equals("total")) {
                numItemsConfirmed = 0;
                Log.v("sajid","clicked total");
                String finderDevRegId = newOrderList.get(position-1).getFinderDevRegId();
                Log.v("sajid","confirming for "+truncateStr(finderDevRegId));
                for (OrderEntity o : orderList) {
                    if (o.getFinderDevRegId().equals(finderDevRegId)) {
                        Log.v("sajid","confirming item "+o.getMenuName());
                        new ConfirmOrderEndpointAsyncTask(this).execute(new Pair<Context, OrderEntity>(this, o));
                    }
                    numItemsInOrder ++;
                }
            }
        }
    }

    public void refreshOrder() {

        numItemsConfirmed ++;
        Log.v("sajid","refresh order tot="+numItemsInOrder+",cnf="+numItemsConfirmed);
        if (numItemsConfirmed == numItemsInOrder) {
            orderList.clear();
            if (iAmServer) {
                Log.v("sajid", "refreshing server listorder for " + myServerId);
                ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
                l.setServerId(myServerId);
                l.execute();
            } else {
                Log.v("sajid", "refreshing finder listorder");
                if (!(finderDevRegId.equals(""))) {
                    ListFinderOrdersEndpointAsyncTask l =
                            new ListFinderOrdersEndpointAsyncTask(this);
                    l.setFinderDevRegId(finderDevRegId);
                    l.execute();
                }
            }
        }
    }

    public void showOrder(List<OrderEntity> result) {

        Log.v("sajid","showing order list");

        orderList.clear();

        for (OrderEntity q : result) {
            orderList.add(q);
        }

        Collections.sort(orderList, new ComparatorOrderEntity());

        ArrayList<OrderEntity> finderOrderList = new ArrayList<OrderEntity>();

        OrderEntity prev = orderList.get(0);

        finderOrderList.clear();
        newOrderList.clear();

        for (OrderEntity o : orderList) {
            if (! o.getFinderDevRegId().equals(prev.getFinderDevRegId())) {

                finderOrderList = removeDups(finderOrderList);

                newOrderList.addAll(finderOrderList);

                OrderEntity dummyEntity = new OrderEntity();
                dummyEntity.setFinderDevRegId("total");
                newOrderList.add(dummyEntity);

                finderOrderList.clear();
                finderOrderList.add(o);
                prev = o;
            } else {
                finderOrderList.add(o);
            }
        }

        finderOrderList = removeDups(finderOrderList);

        newOrderList.addAll(finderOrderList);

        OrderEntity dummyEntity = new OrderEntity();
        dummyEntity.setFinderDevRegId("total");
        newOrderList.add(dummyEntity);

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                newOrderList);

        adapter.setIAmServer(iAmServer);

        setListAdapter(adapter);

    }

    private String truncateStr(String str) {
        return str.substring(str.length() - 5);
    }

    private ArrayList<OrderEntity> removeDups(ArrayList<OrderEntity> finderOrderList) {
        int size = finderOrderList.size();
        for (int i = 0; i < size; i++) {
            OrderEntity oe = finderOrderList.get(i);
            for (int j = i + 1; j < size; j++) {
                OrderEntity oe2 = finderOrderList.get(j);
                if (oe.getMenuId().equals(oe2.getMenuId())) {
                    oe.setQuantity(oe.getQuantity() + oe2.getQuantity());
                    finderOrderList.remove(j);
                    size--;
                    j--;
                }
            }
        }

        return finderOrderList;
    }

}