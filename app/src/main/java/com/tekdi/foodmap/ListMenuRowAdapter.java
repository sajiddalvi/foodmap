package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

import java.util.ArrayList;

/**
 * Created by fsd017 on 12/29/14.
 */


public class ListMenuRowAdapter extends ArrayAdapter<MenuEntity> {

    Context context;
    int layoutResourceId;
    ArrayList<MenuEntity> data = null;

    public ListMenuRowAdapter(Context context, int layoutResourceId,
                              ArrayList<MenuEntity> menuEntities) {
        super(context, layoutResourceId, menuEntities);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = menuEntities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        //LinearLayout row = (LinearLayout) convertView;

        LinearLayout row = (LinearLayout) inflater.inflate(layoutResourceId, parent, false);

        MyListHolder holder = null;
        holder = new MyListHolder();
        holder.name = (TextView) row.findViewById(R.id.list_menu_row_name);
        holder.price = (TextView) row
                .findViewById(R.id.list_menu_row_price);
        holder.quantity = (TextView) row
                .findViewById(R.id.list_menu_row_quantity);
        holder.description = (TextView) row
                .findViewById(R.id.list_menu_row_description);
        holder.thumbnail = (ImageView) row.findViewById(R.id.menu_picture_view);

        row.setTag(holder);

        MenuEntity myList = data.get(position);

        holder.name.setText(myList.getName());
        holder.quantity.setText(myList.getQuantity().toString()+" left");
        String currency_symbol = "$";
        holder.price.setText(currency_symbol + myList.getPrice().toString());
        holder.description.setText(myList.getDescription());

        if (myList.getThumbnail() != null) {
            Bitmap bm = StringToBitMap(myList.getThumbnail());
            holder.thumbnail.setImageBitmap(bm);
        }

        return row;
    }

    static class MyListHolder {
        TextView name;
        TextView quantity;
        TextView price;
        TextView description;
        ImageView thumbnail;
    }

    private Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}
