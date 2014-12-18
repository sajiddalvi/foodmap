package com.tekdi.foodmap;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity;

import java.io.IOException;
import java.util.List;

public class ServeActivity extends ActionBarActivity {

    private ProgressBar mActivityIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serve);
        mActivityIndicator =
                (ProgressBar) findViewById(R.id.address_progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serve, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        ServeFoodEntity server = new ServeFoodEntity();
        server.setAddress(address);
        server.setCuisine(cuisine);
        server.setName(name);
        server.setPhone(phone);

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

    public void onMenuAddButtonClick(View v) {
        Intent intent = new Intent(this, AddMenuActivity.class);
        startActivity(intent);
    }

    public void onMenuListButtonClick(View v) {
        String serverId = Prefs.getServeIdPref(this);
        Intent intent = new Intent(this, ListMenuActivity.class);
        intent.putExtra("serverId", Long.parseLong(serverId));
        startActivity(intent);
    }
}


