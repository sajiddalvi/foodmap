package com.tekdi.foodmap;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    private String finderPhone = "";
    private String serverPhone = "";
    private ListOrderRowAdapter adapter;
    private ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();
    private ArrayList<OrderEntity> newOrderList = new ArrayList<OrderEntity>();
    private int numItemsInOrder;
    private int numItemsConfirmed;
    public static final long DUMMY_TOTAL_MENU_ID = 999;


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

        Long intentServerId = new Long(0);

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
            //Log.v("sajid", "executing server listorder for "+intentServerId.toString());
            //ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
            //l.setServerId(myServerId);
            //l.execute();
        } else {
            Log.v("sajid","executing finder listorder");
            finderDevRegId = Prefs.getDeviceRegIdPref(this);
           /* if (!(finderDevRegId.equals(""))) {
                ListFinderOrdersEndpointAsyncTask l =
                        new ListFinderOrdersEndpointAsyncTask(this);
                l.setFinderDevRegId(finderDevRegId);
                l.execute();
            }*/
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
                reSyncOrder();
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
        if (iAmServer) {
             if (newOrderList.get(position).getFinderDevRegId().equals("total")) {
                numItemsConfirmed = 0;
                String finderDevRegId = newOrderList.get(position-1).getFinderDevRegId();
                for (OrderEntity o : orderList) {
                    if (o.getFinderDevRegId().equals(finderDevRegId)) {
                        //new ConfirmOrderEndpointAsyncTask(this).execute(new Pair<Context, OrderEntity>(this, o));
                    }
                    numItemsInOrder ++;
                }
            }
        }
    }

    public void callPressed(View v) {
        String phone;
        if (iAmServer)
            phone = finderPhone;
        else
            phone = serverPhone;

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    public void refreshOrder() {

        numItemsConfirmed ++;
        Log.v("sajid","refresh order tot="+numItemsInOrder+",cnf="+numItemsConfirmed);
        if (numItemsConfirmed == numItemsInOrder) {
            orderList.clear();
            if (iAmServer) {
               // ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
               // l.setServerId(myServerId);
               // l.execute();
            } else {
                if (!(finderDevRegId.equals(""))) {
/*                    ListFinderOrdersEndpointAsyncTask l =
                            new ListFinderOrdersEndpointAsyncTask(this);
                    l.setFinderDevRegId(finderDevRegId);
                    l.execute();*/
                }
            }
        }
    }

    public void reSyncOrder() {
        orderList.clear();
        if (iAmServer) {
            //ListOrdersEndpointAsyncTask l = new ListOrdersEndpointAsyncTask(this);
            //l.setServerId(myServerId);
            //l.execute();
        } else {
            if (!(finderDevRegId.equals(""))) {
/*                ListFinderOrdersEndpointAsyncTask l =
                        new ListFinderOrdersEndpointAsyncTask(this);
                l.setFinderDevRegId(finderDevRegId);
                l.execute();*/
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

        finderPhone = prev.getFinderPhone();
        serverPhone = prev.getServerPhone();

        finderOrderList.clear();
        newOrderList.clear();

        for (OrderEntity o : orderList) {
            if (! o.getFinderDevRegId().equals(prev.getFinderDevRegId())) {

                finderOrderList = removeDups(finderOrderList);

                newOrderList.addAll(finderOrderList);

                OrderEntity dummyEntity = new OrderEntity();
                dummyEntity.setFinderDevRegId("total");
                dummyEntity.setMenuId((long) DUMMY_TOTAL_MENU_ID);
                dummyEntity.setOrderState(prev.getOrderState());


                Boolean showConfirm = checkIfConfirmed(finderOrderList);
                if (showConfirm)
                    dummyEntity.setFinderDevRegId("total confirm");

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
        dummyEntity.setMenuId((long) DUMMY_TOTAL_MENU_ID);
        dummyEntity.setOrderState(prev.getOrderState());


        Boolean showConfirm = checkIfConfirmed(finderOrderList);
        if (showConfirm)
            dummyEntity.setFinderDevRegId("total confirm");

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
                    if (oe.getOrderState().equals(oe2.getOrderState())) {
                        oe.setQuantity(oe.getQuantity() + oe2.getQuantity());
                        finderOrderList.remove(j);
                        size--;
                        j--;
                    }
                }
            }
        }

        return finderOrderList;
    }

    private Boolean checkIfConfirmed(ArrayList<OrderEntity> finderOrderList) {
        for (OrderEntity o: finderOrderList) {
            if (o.getOrderState() == 1) {
                return false;
            }
        }
        return true;
    }

}