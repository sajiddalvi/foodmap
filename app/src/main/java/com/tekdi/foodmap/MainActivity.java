package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private LinearLayout blockLayout;
    private LinearLayout featuredLayout;
    private LinearLayout statusLayout;
    private ContentLoadingProgressBar progressBar;
    private TextView statusView; 
    private ImageView mainLogoView;
    

    private static boolean mRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLogoView = (ImageView) findViewById(R.id.main_logo);
        blockLayout = (LinearLayout) findViewById(R.id.block_layout);
        featuredLayout = (LinearLayout) findViewById(R.id.featured_layout);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        statusView = (TextView) findViewById(R.id.status);
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.progress);

    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!mRegistered) {
            new GcmRegistrationAsyncTask(this).execute(this);
            progressBar.show();
        }
    }

    public void onFindButtonClick(View v) {
        Intent intent = new Intent(this, FindActivity.class);
        //Intent intent = new Intent(this, PaymentActivity.class);
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

        progressBar.hide();
        
        if (msg.contains("Error")) {
            statusLayout.setVisibility(View.VISIBLE);
            statusView.setText(msg);
        } else {            
            mRegistered = true;
            mainLogoView.setVisibility(View.GONE);
            blockLayout.setVisibility(View.VISIBLE);
            featuredLayout.setVisibility(View.VISIBLE);
        }
    }
}

