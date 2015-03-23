package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stripe.android.model.Token;

import java.util.ArrayList;

/**
 * Created by fsd017 on 3/15/15.
 */
public class ListCardRowAdapter extends ArrayAdapter<Token> {

    Context context;
    int layoutResourceId;
    ArrayList<Token> data = null;

    public ListCardRowAdapter(Context context, int layoutResourceId,
                              ArrayList<Token> cards) {
        super(context, layoutResourceId, cards);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = cards;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        LinearLayout row = (LinearLayout) inflater.inflate(layoutResourceId, parent, false);

        MyListHolder holder = null;
        holder = new MyListHolder();
        holder.last4 = (TextView) row.findViewById(R.id.list_card_row_last4);
        holder.tokenId = (TextView) row
                .findViewById(R.id.list_card_row_token_id);


        row.setTag(holder);

        Token myList = data.get(position);

        holder.last4.setText(myList.getCard().getLast4());
        holder.tokenId.setText(myList.getId());


        return row;
    }


    static class MyListHolder {
        TextView last4;
        TextView tokenId;
    }
    
}
