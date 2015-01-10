package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

import java.text.NumberFormat;
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
        holder.description = (TextView) row
                .findViewById(R.id.list_menu_row_description);
        holder.thumbnail = (ImageView) row.findViewById(R.id.menu_picture_view);

        row.setTag(holder);

        MenuEntity myList = data.get(position);

        holder.name.setText(myList.getName());

        NumberFormat format = NumberFormat.getCurrencyInstance();
        holder.price.setText(format.format(myList.getPrice()));

        holder.description.setText(myList.getDescription());

        if (myList.getThumbnail() != null) {
            Thumbnail t = new Thumbnail(myList.getThumbnail());
            holder.thumbnail.setImageBitmap(t.getBitmap());
        }

        return row;
    }

    static class MyListHolder {
        TextView name;
        TextView price;
        TextView description;
        ImageView thumbnail;
    }

}
