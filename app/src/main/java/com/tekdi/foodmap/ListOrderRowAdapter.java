package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by fsd017 on 12/30/14.
 */


public class ListOrderRowAdapter extends ArrayAdapter<OrderEntity> {

    Context context;
    int layoutResourceId;
    ArrayList<OrderEntity> data = null;
    private Boolean iAmServer = Boolean.FALSE;
    private String prevFinderDevRegId = null;
    private Float prevTotal = new Float(0);

    public ListOrderRowAdapter(Context context, int layoutResourceId,
                              ArrayList<OrderEntity> orderEntities) {
        super(context, layoutResourceId, orderEntities);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = orderEntities;
        prevFinderDevRegId = null;
        prevTotal = (float)0.0;
    }

    public void setIAmServer(Boolean iAmServer) {
        this.iAmServer = iAmServer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        LinearLayout row = (LinearLayout) convertView;

        row = (LinearLayout) inflater.inflate(layoutResourceId, parent, false);

        MyListHolder holder = null;
        holder = new MyListHolder();
        holder.nameRowLayout = (LinearLayout) row.findViewById(R.id.list_order_row_name_layout);
        holder.who = (TextView) row.findViewById(R.id.list_order_row_who);
        holder.timestamp = (TextView) row.findViewById(R.id.list_order_row_timestamp);
        holder.phone = (TextView) row.findViewById(R.id.list_order_row_phone);
        holder.address = (TextView) row.findViewById(R.id.list_order_row_address);


        holder.name = (TextView) row.findViewById(R.id.list_order_row_name);
        holder.border = (LinearLayout) row.findViewById(R.id.list_order_border);
        holder.price = (TextView) row
                .findViewById(R.id.list_order_row_price);
        holder.quantity = (TextView) row
                .findViewById(R.id.list_order_row_quantity);
        holder.totalLabel = (TextView) row.findViewById(R.id.list_order_row_total_label);

        holder.total = (TextView) row.findViewById(R.id.list_order_row_total);
        holder.buttonLayout = (LinearLayout) row.findViewById(R.id.list_order_buttons_layout);
        holder.button1 = (TextView) row.findViewById(R.id.list_order_button1);
        holder.button2 = (TextView) row.findViewById(R.id.list_order_button2);
        holder.button3 = (TextView) row.findViewById(R.id.list_order_button3);

        holder.status = (TextView) row.findViewById(R.id.list_order_row_status);

        row.setTag(holder);

        OrderEntity order = data.get(position);

        if (order.getMenuId() == ListOrderFinderActivity.DUMMY_TOTAL_MENU_ID ) {
            hide_all(holder);
            holder.totalLabel.setVisibility(View.VISIBLE);
            holder.total.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.VISIBLE);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            holder.total.setText(format.format(prevTotal));
            holder.buttonLayout.setVisibility(View.VISIBLE);

            String orderStatus[];
            if (iAmServer)
                orderStatus = context.getResources().getStringArray(R.array.order_status_server);
            else
                orderStatus = context.getResources().getStringArray(R.array.order_status_finder);

            int orderState = order.getOrderState();
            holder.status.setText(orderStatus[orderState]);

            if (iAmServer) {
                switch (orderState) {
                    case OrderState.ORDER_STATE_NEW:
                        holder.button1.setVisibility(View.INVISIBLE);
                        holder.button2.setVisibility(View.INVISIBLE);
                        holder.button3.setVisibility(View.INVISIBLE);
                        break;
                    case OrderState.ORDER_STATE_SEND:
                        holder.button1.setText("Accept");
                        holder.button2.setVisibility(View.INVISIBLE);
                        holder.button3.setText("Cancel");
                        break;
                    case OrderState.ORDER_STATE_RECEIVE:
                        holder.button1.setText("Ready");
                        holder.button2.setVisibility(View.INVISIBLE);
                        holder.button3.setText("Cancel");
                        break;
                    case OrderState.ORDER_STATE_READY:
                        holder.button1.setText("Done");
                        holder.button2.setVisibility(View.INVISIBLE);
                        holder.button3.setText("Cancel");
                        break;
                }
                holder.button1.setTag(order.getFinderDevRegId());
                holder.button2.setTag(order.getFinderDevRegId());
                holder.button3.setTag(order.getFinderDevRegId());

            } else {
                switch (orderState) {
                    case OrderState.ORDER_STATE_NEW :
                        holder.button1.setText("Add");
                        holder.button2.setText("Clear");
                        holder.button3.setText("Send");
                        break;
                    case OrderState.ORDER_STATE_SEND :
                        holder.button1.setText("Update");
                        holder.button2.setVisibility(View.INVISIBLE);
                        holder.button3.setText("Cancel");
                        break;
                    case OrderState.ORDER_STATE_RECEIVE :
                        holder.button1.setText("Update");
                        holder.button2.setVisibility(View.INVISIBLE);
                        holder.button3.setText("Cancel");
                        break;
                    case OrderState.ORDER_STATE_READY :
                        holder.button1.setText("Update");
                        holder.button2.setText("Pay");
                        holder.button3.setText("Cancel");
                        break;
                    case OrderState.ORDER_STATE_CANCEL :
                        holder.button1.setVisibility(View.INVISIBLE);
                        holder.button2.setVisibility(View.INVISIBLE);
                        holder.button3.setText("Undo Cancel");
                        break;
                }

                holder.button1.setTag(order.getServerId());
                holder.button2.setTag(order.getServerId());
                holder.button3.setTag(order.getServerId());
            }

            holder.border.setVisibility(View.VISIBLE);

            return row;
        }

        if (order.getMenuId() == ListOrderFinderActivity.DUMMY_NAME_MENU_ID ) {

            String whoStr;
            String phone;
            String address;

            if (iAmServer) {
                holder.who.setText(order.getId().toString());
                phone = order.getFinderPhone();
                holder.phone.setText("Phone: "+phone);
                holder.phone.setTag(phone);
                holder.address.setVisibility(View.GONE);
            } else {
                whoStr = order.getServerName();
                phone = order.getServerPhone();
                address = order.getServerAddress();
                holder.phone.setText("Phone: "+phone);
                holder.address.setText("Address: "+address);
                holder.phone.setTag(phone);
                holder.address.setTag(address);
                holder.who.setText(whoStr);
            }

            hide_all(holder);

            prevFinderDevRegId = order.getFinderDevRegId();


            holder.timestamp.setText(getTimeStamp(order));
            holder.nameRowLayout.setVisibility(View.VISIBLE);

            prevTotal = order.getPrice() * order.getQuantity();

            return row;
        }

        prevTotal += (order.getPrice() * order.getQuantity());

        holder.name.setText(order.getMenuName());
        holder.quantity.setText(order.getQuantity().toString());

        NumberFormat format = NumberFormat.getCurrencyInstance();
        holder.price.setText(format.format(order.getPrice()));

        return row;
    }

    private void hide_all(MyListHolder row){
        row.nameRowLayout.setVisibility(View.GONE);
        row.quantity.setVisibility(View.GONE);
        row.name.setVisibility(View.GONE);
        row.price.setVisibility(View.GONE);
        row.border.setVisibility(View.GONE);
        row.totalLabel.setVisibility(View.GONE);
        row.total.setVisibility(View.GONE);
    }

    private String getTimeStamp(OrderEntity order) {

        String localTime = "";
        if ((order.getTimestamp() != null) && (! order.getTimestamp().equals(""))) {
            Date d = new Date();
            String datestring = order.getTimestamp().toString();

            try {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");// spec for RFC3339 (with fractional seconds)
                s.setLenient(true);
                s.setTimeZone(TimeZone.getTimeZone("UTC"));
                d = s.parse(datestring);
            } catch (java.text.ParseException pe) {
                Log.e("sajid", pe.getMessage());
            }

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM, hh:mm a");
            sdf.setTimeZone(tz);
            localTime = sdf.format(d);
        }
        return localTime;
    }

    static class MyListHolder {
        LinearLayout nameRowLayout;
        TextView who;
        TextView timestamp;
        TextView phone;
        TextView address;
        TextView name;
        TextView quantity;
        TextView price;
        LinearLayout border;
        TextView totalLabel;
        TextView total;
        LinearLayout buttonLayout;
        TextView button1;
        TextView button2;
        TextView button3;
        TextView status;
    }

}