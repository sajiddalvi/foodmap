package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class ServeActivity extends Activity {

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

        return super.onOptionsItemSelected(item);
    }

    public void onServerSetupButtonClick(View v) {
        Intent intent = new Intent(this, EditServerActivity.class);
        startActivity(intent);
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

    public void onOrderListButtonClick(View v) {
        Log.v("sajid","onOrderListButtonClick");

        String serverId = Prefs.getServeIdPref(this);
        Intent intent = new Intent(this, ListOrderActivity.class);
        intent.putExtra("serverId", serverId);
        startActivity(intent);
    }
}
