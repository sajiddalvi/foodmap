package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

/**
 * Created by fsd017 on 12/14/14.
 */
public class AddMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_menu);
    }

    public void onMenuAddButtonClick(View v) {
        EditText editText = (EditText) findViewById(R.id.menu_name_text);
        String name = editText.getText().toString();

        editText = (EditText) findViewById(R.id.menu_description_text);
        String description = editText.getText().toString();

        editText = (EditText) findViewById(R.id.menu_quantity_text);
        String quantity = editText.getText().toString();

        editText = (EditText) findViewById(R.id.menu_price_text);
        String price = editText.getText().toString();

        String serverId = Prefs.getServeIdPref(this);

        MenuEntity menu = new MenuEntity();
        menu.setName(name);
        menu.setDescription(description);
        menu.setQuantity(Integer.parseInt(quantity));
        menu.setPrice(Float.parseFloat(price));
        if (serverId != null) {
            menu.setServerId(Long.parseLong(serverId));
        }

        new MenuEntityEndpointAsyncTask(this).execute(new Pair<Context, MenuEntity>(this, menu));

    }

    public void doneAddingMenu() {
        finish();
    }

}
