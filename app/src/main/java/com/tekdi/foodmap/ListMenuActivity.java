package com.tekdi.foodmap;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsd017 on 12/14/14.
 */
public class ListMenuActivity extends ListActivity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        new ListMenuEndpointAsyncTask(this).execute();

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }

    public void showMenu(List<MenuEntity> result) {
        ArrayList<String> values = new ArrayList<String>();;

        for (MenuEntity q : result) {
            values.add(q.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

    }
}