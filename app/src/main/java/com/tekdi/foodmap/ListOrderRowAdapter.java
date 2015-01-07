package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tekdi.foodmap.backend.orderEntityApi.model.OrderEntity;

import java.util.ArrayList;

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
        holder.total = (TextView) row.findViewById(R.id.list_order_row_total);

        row.setTag(holder);

        OrderEntity order = data.get(position);

        String orderIdStr = "";
        String truncatedOrderIdStr = "";

        if (!(order.getId() == null)) {
            orderIdStr = order.getId().toString();
            truncatedOrderIdStr = orderIdStr.substring(orderIdStr.length() - 5);
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
            prevTotal = order.getPrice();
        }
        else {
            if (! prevFinderDevRegId.equals(order.getFinderDevRegId())) {
                prevFinderDevRegId = order.getFinderDevRegId();
                holder.who.setVisibility(View.VISIBLE);
                holder.border.setVisibility(View.VISIBLE);
                holder.total.setVisibility(View.VISIBLE);
                holder.total.setText(prevTotal.toString());
                prevTotal = (float)0;
            } else {
                prevTotal += order.getPrice();
            }

        }

        holder.who.setText(whoStr);

        holder.name.setText(order.getMenuName());
        holder.quantity.setText(order.getQuantity().toString());
        String currency_symbol = "$";
        holder.price.setText(currency_symbol + order.getPrice().toString());

        if (order.getOrderState() == 0)
            holder.state.setText("new1");
        else if (order.getOrderState() == 1)
            holder.state.setText("cnf1");

        return row;
    }

    static class MyListHolder {
        TextView name;
        TextView who;
        TextView quantity;
        TextView price;
        TextView state;
        LinearLayout border;
        TextView total;
    }

}