package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity;

import java.io.IOException;
import java.util.List;

/**
 * Created by fsd017 on 12/27/14.
 */
public class EditServerActivity extends Activity implements OnItemSelectedListener {

    private String cuisineSelected = "";
    private Long serverId;
    private Boolean addingNewServer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_server);

        Spinner spinner = (Spinner) findViewById(R.id.cuisine_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cuisine_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        if (Prefs.getServeIdPref(this) == "") {
            setTitle("Add Server");
            addingNewServer = true;
        } else {
            EditText editText = (EditText) findViewById(R.id.server_name_text);
            editText.setText(Prefs.getSERVER_NAME_PREF(this));
            editText = (EditText) findViewById(R.id.server_phone_text);
            editText.setText(Prefs.getSERVER_PHONE_PREF(this));
            editText = (EditText) findViewById(R.id.server_address_text);
            editText.setText(Prefs.getSERVER_ADDRESS_PREF(this));
            String cuisine = Prefs.getSERVER_CUISINE_PREF(this);
            serverId = Long.parseLong(Prefs.getServeIdPref(this));
            spinner.setSelection(adapter.getPosition(cuisine));
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        cuisineSelected = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onServerSetupButtonClick(View v) {

        EditText editText = (EditText) findViewById(R.id.server_name_text);
        String name = editText.getText().toString();

        editText = (EditText) findViewById(R.id.server_phone_text);
        String phone = editText.getText().toString();

        editText = (EditText) findViewById(R.id.server_address_text);
        String address = editText.getText().toString();

        ServeFoodEntity server = new ServeFoodEntity();
        server.setId(serverId);
        server.setAddress(address);
        server.setCuisine(cuisineSelected);
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

        if (addingNewServer)
            new ServeFoodEntityEndpointAsyncTask(this).execute(new Pair<Context, ServeFoodEntity>(this, server));
        else
            new ServeFoodEntityEditEndpointAsyncTask(this).execute(new Pair<Context, ServeFoodEntity>(this, server));
    }

    public void doneEditingServer(ServeFoodEntity result) {
        if (result != null) {
            Prefs.setServerIdPref(this, result.getId().toString());
            Prefs.setSERVER_NAME_PREF(this, result.getName());
            Prefs.setSERVER_PHONE_PREF(this, result.getPhone());
            Prefs.setSERVER_ADDRESS_PREF(this, result.getAddress());
            Prefs.setSERVER_CUISINE_PREF(this, result.getCuisine());
        } else {
            Log.v("sajid","EditServerActivity : failed to setup server");
        }
        finish();
    }

}
