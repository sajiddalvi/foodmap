package com.tekdi.foodmap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.stripe.model.Customer;
import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends ListActivity {

    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://manage.stripe.com/account/apikeys
     */
    public static final String PUBLISHABLE_KEY = "pk_test_npiJ9fcyu48YHPFqy9LaOxeM";

    List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();

    private Customer customer;

    private ArrayList<MenuEntity> menuList = new ArrayList<MenuEntity>();
    ListCardRowAdapter adapter = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        customer = Prefs.getCreditCard(this);
        
        if (customer == null) {
            Intent intent = new Intent(this, AddPaymentActivity.class);
            startActivity(intent);
        } else {

            // confirm payment activity
                
            Log.v("sajid","tokenId="+customer.getId());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_payment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_save:
                Intent intent = new Intent(this, AddPaymentActivity.class);
                startActivity(intent);
                break;
            case R.id.action_cancel:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}