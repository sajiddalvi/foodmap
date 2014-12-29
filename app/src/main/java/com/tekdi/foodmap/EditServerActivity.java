package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity;

import java.io.IOException;
import java.util.List;

/**
 * Created by fsd017 on 12/27/14.
 */
public class EditServerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_server);
        if (Prefs.getServeIdPref(this) == "") {
            setTitle("Add Server");
        } else {
            EditText editText = (EditText) findViewById(R.id.server_name_text);
            editText.setText(Prefs.getSERVER_NAME_PREF(this));
            editText = (EditText) findViewById(R.id.server_phone_text);
            editText.setText(Prefs.getSERVER_PHONE_PREF(this));
            editText = (EditText) findViewById(R.id.server_address_text);
            editText.setText(Prefs.getSERVER_ADDRESS_PREF(this));
            RadioGroup radioCuisineGroup = (RadioGroup) findViewById(R.id.cuisineRadioGroup);
            String cuisine = Prefs.getSERVER_CUISINE_PREF(this);

            int i;
            for (i=0; i<radioCuisineGroup.getChildCount();i++ ) {
                RadioButton r = (RadioButton)radioCuisineGroup.getChildAt(i);
                Log.v("sajid",cuisine+","+r.getText().toString());
                if (cuisine.equals(r.getText().toString())) {
                    r.setChecked(true);
                    break;
                }
            }
        }
    }

    public void onServerSetupButtonClick(View v) {

        EditText editText = (EditText) findViewById(R.id.server_name_text);
        String name = editText.getText().toString();

        editText = (EditText) findViewById(R.id.server_phone_text);
        String phone = editText.getText().toString();

        editText = (EditText) findViewById(R.id.server_address_text);
        String address = editText.getText().toString();

        String cuisine = "";

        RadioGroup radioCuisineGroup = (RadioGroup) findViewById(R.id.cuisineRadioGroup);
        int selectedId = radioCuisineGroup.getCheckedRadioButtonId();
        RadioButton radioCuisineButton = (RadioButton) findViewById(selectedId);
        if (radioCuisineButton != null)
            cuisine = radioCuisineButton.getText().toString();

        Prefs.setSERVER_NAME_PREF(this, name);
        Prefs.setSERVER_PHONE_PREF(this, phone);
        Prefs.setSERVER_ADDRESS_PREF(this, address);
        Prefs.setSERVER_CUISINE_PREF(this, cuisine);

        ServeFoodEntity server = new ServeFoodEntity();
        server.setAddress(address);
        server.setCuisine(cuisine);
        server.setName(name);
        server.setPhone(phone);
        Log.v("sajid", "sajid getting reg id " + Prefs.getDeviceRegIdPref(this));
        server.setServerRegId(Prefs.getDeviceRegIdPref(this));

        Geocoder coder = new Geocoder(this);
        List<Address> addresses;
        Double latitude = 0.0;
        Double longitude = 0.0;

        if (coder.isPresent()) {
            try {
                addresses = coder.getFromLocationName(address, 5);
                Address location = addresses.get(0);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                server.setLatitude(latitude.toString());
                server.setLongitude(longitude.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        new ServeFoodEntityEndpointAsyncTask().execute(new Pair<Context, ServeFoodEntity>(this, server));
    }

}
