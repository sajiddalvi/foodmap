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

    public ListOrderRowAdapter(Context context, int layoutResourceId,
                              ArrayList<OrderEntity> orderEntities) {
        super(context, layoutResourceId, orderEntities);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = orderEntities;
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
        holder.orderId = (TextView) row.findViewById(R.id.list_order_row_order_id);
        holder.name = (TextView) row.findViewById(R.id.list_order_row_name);
        holder.who = (TextView) row.findViewById(R.id.list_order_row_who);

        holder.price = (TextView) row
                .findViewById(R.id.list_order_row_price);
        holder.quantity = (TextView) row
                .findViewById(R.id.list_order_row_quantity);
        holder.state = (TextView) row
                .findViewById(R.id.list_order_row_state);

        row.setTag(holder);

        OrderEntity order = data.get(position);

        String orderIdStr = "";
        String truncatedOrderIdStr = "";

        if (!(order.getId() == null)) {
            orderIdStr = order.getId().toString();
            truncatedOrderIdStr = orderIdStr.substring(orderIdStr.length() - 5);
        }

        String finderStr = order.getFinderDevRegId();
        String truncatedFinderStr = finderStr.substring(finderStr.length()-5);

        holder.orderId.setText(truncatedOrderIdStr);
        holder.name.setText(order.getMenuName());
        holder.quantity.setText(order.getQuantity().toString());
        String currency_symbol = "$";
        holder.price.setText(currency_symbol + order.getPrice().toString());

        if (iAmServer)
            holder.who.setText(truncatedFinderStr);
        else
            holder.who.setText(order.getServerName());

        if (order.getOrderState() == 0)
            holder.state.setText("new");
        else if (order.getOrderState() == 1)
            holder.state.setText("cnf");

        return row;
    }

    static class MyListHolder {
        TextView orderId;
        TextView name;
        TextView who;
        TextView quantity;
        TextView price;
        TextView state;
    }

}