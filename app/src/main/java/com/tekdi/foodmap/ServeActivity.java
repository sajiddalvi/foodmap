package com.tekdi.foodmap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class ServeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serve);
    }

    public void onServerSetupButtonClick(View v) {

        String Name = findViewById(R.id.server_name_text).toString();
        String Phone = findViewById(R.id.server_phone_text).toString();
        String Address = findViewById(R.id.server_phone_text).toString();
        String Cuisine = "";

        RadioGroup radioCuisineGroup = (RadioGroup) findViewById(R.id.cuisineRadioGroup);
        int selectedId = radioCuisineGroup.getCheckedRadioButtonId();
        RadioButton radioCuisineButton = (RadioButton) findViewById(selectedId);
        if (radioCuisineButton != null)
            Cuisine =  radioCuisineButton.getText().toString();

    }
}
