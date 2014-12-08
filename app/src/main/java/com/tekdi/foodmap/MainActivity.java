package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GcmRegistrationAsyncTask().execute(this);
    }

    public void onServeButtonClick(View v) {
        Intent intent = new Intent(this, ServeActivity.class);
        startActivity(intent);
    }

    public void onFindButtonClick(View v) {
        Intent intent = new Intent(this, FindActivity.class);
        startActivity(intent);
    }
}
