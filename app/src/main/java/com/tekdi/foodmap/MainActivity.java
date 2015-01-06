package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GcmRegistrationAsyncTask().execute(this);

    }

    public void onServeButtonClick(View v) {

        if (Prefs.getServeIdPref(this) == "") {
            Intent intent = new Intent(this, EditServerActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ServeActivity.class);
            startActivity(intent);
        }
    }

    public void onFindButtonClick(View v) {
        Intent intent = new Intent(this, FindActivity.class);
        startActivity(intent);
    }

    public void onOrderListButtonClick(View v) {
        Log.v("sajid", "onOrderListButtonClick");
        String serverId = Prefs.getServeIdPref(this);
        Intent intent = new Intent(this, ListOrderActivity.class);
        intent.putExtra("serverId", serverId);
        startActivity(intent);
    }

    public void onServerSetupButtonClick(View v) {
        Intent intent = new Intent(this, EditServerActivity.class);
        startActivity(intent);
    }

    public void onFinderSetupButtonClick(View v) {
        Toast.makeText(this, "No Finder Setup", Toast.LENGTH_LONG).show();
    }

    public void onMenuListButtonClick(View v) {
        String serverId = Prefs.getServeIdPref(this);
        Intent intent = new Intent(this, ListMenuActivity.class);
        intent.putExtra("serverId", Long.parseLong(serverId));
        intent.putExtra("serverName", "lulus");
        intent.putExtra("source","server");
        startActivity(intent);
    }

    public void onBannerClick(View v) {
        Intent intent = new Intent(this, ListMenuActivity.class);
        intent.putExtra("serverId", Long.parseLong("6240701431939072"));
        intent.putExtra("serverName", "lulus");
        intent.putExtra("source","finder");
        startActivity(intent);
    }
}

