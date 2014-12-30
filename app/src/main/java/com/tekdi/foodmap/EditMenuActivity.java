package com.tekdi.foodmap;

/**
 * Created by fsd017 on 12/29/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

public class EditMenuActivity extends Activity {
        private Long menuId;
        private Long serverId;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_menu);

            Log.v("sajid","EditMenuActivity onCreate");
            try {
                Intent i = this.getIntent();
                ParcelableMenu p = (ParcelableMenu) i.getParcelableExtra("com.tekdi.foodmap.ParcelableMenu");

                Log.v("sajid","Edit Menu =>"+p.name);

                EditText editText = (EditText) findViewById(R.id.menu_name_text);
                editText.setText(p.name);

                editText = (EditText) findViewById(R.id.menu_description_text);
                editText.setText(p.description);

                editText = (EditText) findViewById(R.id.menu_quantity_text);
                editText.setText(p.quantity.toString());
                editText = (EditText) findViewById(R.id.menu_price_text);
                editText.setText(p.price.toString());

                this.menuId = p.menuId;
                this.serverId = p.serverId;



            } catch(Exception e){
                Log.v("sajid","EditMenuActivity onCreate failed");
                e.printStackTrace();
            }

        }

        public void onMenuEditButtonClick(View v) {
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
            menu.setId(this.menuId);
            menu.setServerId(this.serverId);
            menu.setName(name);
            menu.setDescription(description);
            menu.setQuantity(Integer.parseInt(quantity));
            menu.setPrice(Float.parseFloat(price));
            if (serverId != null) {
                menu.setServerId(Long.parseLong(serverId));
            }

            new MenuEntityEditEndpointAsyncTask().execute(new Pair<Context, MenuEntity>(this, menu));

        }
    }

