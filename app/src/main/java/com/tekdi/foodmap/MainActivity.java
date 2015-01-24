package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private LinearLayout caterLayout;
    private LinearLayout findLayout;
    private LinearLayout featuredLayout;
    private LinearLayout statusLayout;

    private static boolean mRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        caterLayout = (LinearLayout) findViewById(R.id.cater_food_layout);
        findLayout = (LinearLayout) findViewById(R.id.find_food_layout);
        featuredLayout = (LinearLayout) findViewById(R.id.featured_layout);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);

        caterLayout.setVisibility(View.INVISIBLE);
        findLayout.setVisibility(View.INVISIBLE);
        featuredLayout.setVisibility(View.INVISIBLE);


    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!mRegistered)
            new GcmRegistrationAsyncTask(this).execute(this);
        else {
            caterLayout.setVisibility(View.VISIBLE);
            findLayout.setVisibility(View.VISIBLE);
            featuredLayout.setVisibility(View.VISIBLE);
        }

    }

    public void onFindButtonClick(View v) {
        Intent intent = new Intent(this, FindActivity.class);
        startActivity(intent);
    }

    public void onOrderListButtonClick(View v) {
        if (Prefs.getServeIdPref(this).equals("")) {
            Toast.makeText(this, "Setup Catering Service first.", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, ListOrderServerActivity.class);
            startActivity(intent);
        }
    }

    public void onFinderOrderButtonClick(View v) {
        Intent intent = new Intent(this, ListOrderFinderActivity.class);
        startActivity(intent);
    }

    public void onServerSetupButtonClick(View v) {
        Intent intent = new Intent(this, EditServerActivity.class);
        startActivity(intent);
    }

    public void onMenuListButtonClick(View v) {

        String serverId = Prefs.getServeIdPref(this);

        if (serverId.equals("")) {
            Toast.makeText(this, "Setup Catering Service first.", Toast.LENGTH_LONG).show();
        } else {

            Intent intent = new Intent(this, ListMenuActivity.class);
            intent.putExtra("serverId", Long.parseLong(serverId));
            intent.putExtra("serverName", "lulus");
            intent.putExtra("source", "server");
            startActivity(intent);
        }
    }

    public void onBannerClick(View v) {
        Intent intent = new Intent(this, ListMenuActivity.class);
        intent.putExtra("serverId", Long.parseLong("6240701431939072"));
        intent.putExtra("serverName", "lulus");
        intent.putExtra("source","finder");
        intent.putExtra("phone","6305852134");
        intent.putExtra("address","1147 n eola road aurora il");

        startActivity(intent);
    }


    public void onPostExecute(String msg) {
        Log.v("sajid", "registration postExecute " + msg);

        if (msg.contains("Error")) {
            TextView statusView = (TextView) findViewById(R.id.status);
            statusView.setText(msg);
            statusLayout.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "registered", Toast.LENGTH_LONG).show();
            caterLayout.setVisibility(View.VISIBLE);
            findLayout.setVisibility(View.VISIBLE);
            featuredLayout.setVisibility(View.VISIBLE);
            mRegistered = true;
        }
    }
}

