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
        holder.who = (TextView) row.findViewById(R.id.list_order_row_who);

        holder.name = (TextView) row.findViewById(R.id.list_order_row_name);
        holder.border = (LinearLayout) row.findViewById(R.id.list_order_border);
        holder.price = (TextView) row
                .findViewById(R.id.list_order_row_price);
        holder.quantity = (TextView) row
                .findViewById(R.id.list_order_row_quantity);
        holder.state = (TextView) row
                .findViewById(R.id.list_order_row_state);
        holder.totalLabel = (TextView) row.findViewById(R.id.list_order_row_total_label);

        holder.total = (TextView) row.findViewById(R.id.list_order_row_total);
        holder.timestamp = (TextView) row.findViewById(R.id.list_order_row_timestamp);
        holder.confirm = (TextView) row.findViewById(R.id.list_order_row_confirm);

        row.setTag(holder);

        OrderEntity order = data.get(position);

        if (order.getFinderDevRegId().equals("total")){
            hide_all(holder);
            holder.totalLabel.setVisibility(View.VISIBLE);
            holder.total.setVisibility(View.VISIBLE);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            holder.total.setText(format.format(prevTotal));

            if (iAmServer) {
                holder.confirm.setVisibility(View.VISIBLE);
            }

            return row;
        }

        String whoStr = new String("who");

        if (iAmServer) {
            String finderStr = order.getFinderDevRegId();
            String truncatedFinderStr = finderStr.substring(finderStr.length() - 5);
            whoStr = truncatedFinderStr;
        } else {
            whoStr = order.getServerName();
        }


        if (position == 0) {
            prevFinderDevRegId = order.getFinderDevRegId();
            holder.who.setVisibility(View.VISIBLE);
            holder.who.setText(whoStr);
            holder.timestamp.setText(getTimeStamp(order));
            holder.timestamp.setVisibility(View.VISIBLE);

            prevTotal = order.getPrice() * order.getQuantity();
        }
        else {
            if (! prevFinderDevRegId.equals(order.getFinderDevRegId())) {
                prevFinderDevRegId = order.getFinderDevRegId();
                holder.who.setText(whoStr);
                holder.who.setVisibility(View.VISIBLE);
                holder.timestamp.setText(getTimeStamp(order));
                holder.timestamp.setVisibility(View.VISIBLE);
                holder.border.setVisibility(View.VISIBLE);
                prevTotal = order.getPrice() * order.getQuantity();
            } else {
                prevTotal += (order.getPrice() * order.getQuantity());
            }

        }

        holder.name.setText(order.getMenuName());
        holder.quantity.setText(order.getQuantity().toString());

        NumberFormat format = NumberFormat.getCurrencyInstance();
        holder.price.setText(format.format(order.getPrice()));

        if (order.getOrderState() == 0)
            holder.state.setText("new");
        else if (order.getOrderState() == 1)
            holder.state.setText("cnf");

        return row;
    }

    private void hide_all(MyListHolder row){
        row.name.setVisibility(View.GONE);
        row.who.setVisibility(View.GONE);
        row.timestamp.setVisibility(View.GONE);
        row.quantity.setVisibility(View.GONE);
        row.name.setVisibility(View.GONE);
        row.price.setVisibility(View.GONE);
        row.state.setVisibility(View.GONE);
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
        TextView name;
        TextView who;
        TextView timestamp;
        TextView quantity;
        TextView price;
        TextView state;
        LinearLayout border;
        TextView totalLabel;
        TextView total;
        TextView confirm;
    }

}