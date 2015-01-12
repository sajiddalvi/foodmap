package com.tekdi.foodmap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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

/**
 * Created by fsd017 on 1/11/15.
 */
public class ListOrderFinderActivity extends ListActivity {
    private static ArrayList<OrderEntity> orderList = new ArrayList<OrderEntity>();
    private ListOrderRowAdapter adapter;
    public static final long DUMMY_TOTAL_MENU_ID = 999;
    private Menu orderActionBarMenu;
    private String action;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.list_order_view);

        registerForContextMenu(getListView());

        Intent intent = getIntent();
        action = intent.getStringExtra("action");

        if ((action!= null) && action.equals("new_order")) {
            setTitle("New Order");
            ParcelableFinderOrder p = (ParcelableFinderOrder)
                    intent.getParcelableExtra("com.tekdi.foodmap.ParcelableFinderOrder");
            processNewOrder(p);
        } else if ( (action!= null) && action.equals("notification")) {
            processOrderReceived();
        } else if ((orderList == null) || (orderList.size() == 0)) {
            intent = new Intent(this, FindActivity.class);
            startActivity(intent);
            finish();
        }

        adapter = new ListOrderRowAdapter(this, R.layout.list_order_row,
                orderList);

        setListAdapter(adapter);
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
                    if (order.getMenuId() != DUMMY_TOTAL_MENU_ID)  // dont't send total
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
                orderList.remove(info.position);
                if (orderList.size() == 1) // only total left, clear the list
                    orderList.clear();
                adapter.notifyDataSetChanged();

                return true;

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
                orderList.add(o);
                OrderEntity dummyEntity = new OrderEntity();
                dummyEntity.setMenuId((long) DUMMY_TOTAL_MENU_ID);
                dummyEntity.setOrderState(o.getOrderState());

                orderList.add(dummyEntity);
            } else {
                // always add new order to the top, so "total" stays last
                orderList.add(0, o);
            }

        } else {
            OrderEntity o = orderList.get(index);
            o.setQuantity(o.getQuantity() + 1);
            orderList.set(index, o);
        }

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

    private void processOrderSent() {
        for (OrderEntity order : orderList) {
            order.setOrderState(OrderState.ORDER_STATE_SEND);
        }
        setupActionBarButtons(OrderState.ORDER_STATE_SEND);
        setTitle("Pending Order");
        adapter.notifyDataSetChanged();

    }

    private void processOrderReceived() {
        for (OrderEntity order : orderList) {
            order.setOrderState(OrderState.ORDER_STATE_RECEIVE);
        }
        setupActionBarButtons(OrderState.ORDER_STATE_RECEIVE);
        setTitle("Pending Order");
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

}
