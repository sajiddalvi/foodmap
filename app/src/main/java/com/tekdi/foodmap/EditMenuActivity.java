package com.tekdi.foodmap;

/**
 * Created by fsd017 on 12/29/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

import java.io.ByteArrayOutputStream;

public class EditMenuActivity extends Activity {
    private Long menuId;
    private Long serverId;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private Bitmap mThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);

        mImageView = (ImageView) findViewById(R.id.menu_picture_view);
        mThumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_camera);

        Log.v("sajid","EditMenuActivity onCreate");
        try {
            Intent i = this.getIntent();
            ParcelableMenu p = (ParcelableMenu) i.getParcelableExtra("com.tekdi.foodmap.ParcelableMenu");

            Log.v("sajid","Edit Menu =>"+p.name);

            EditText editText = (EditText) findViewById(R.id.menu_name_text);
            editText.setText(p.name);

            editText = (EditText) findViewById(R.id.menu_description_text);
            editText.setText(p.description);

            editText = (EditText) findViewById(R.id.menu_quantity_text);
            editText.setText(p.quantity.toString());
            editText = (EditText) findViewById(R.id.menu_price_text);
            editText.setText(p.price.toString());

            this.menuId = p.menuId;
            this.serverId = p.serverId;

            ImageView imageView = (ImageView) findViewById(R.id.menu_picture_view);

            if (p.thumbnail != null) {
                Bitmap bm = StringToBitMap(p.thumbnail);
                imageView.setImageBitmap(bm);
            }


        } catch(Exception e){
            Log.v("sajid","EditMenuActivity onCreate failed");
            e.printStackTrace();
        }

    }

    public void onMenuEditButtonClick(View v) {

        EditText editText = (EditText) findViewById(R.id.menu_name_text);
        String name = editText.getText().toString();

        editText = (EditText) findViewById(R.id.menu_description_text);
        String description = editText.getText().toString();

        editText = (EditText) findViewById(R.id.menu_quantity_text);
        String quantity = editText.getText().toString();

        editText = (EditText) findViewById(R.id.menu_price_text);
        String price = editText.getText().toString();

        String serverId = Prefs.getServeIdPref(this);

        MenuEntity menu = new MenuEntity();
        menu.setId(this.menuId);
        menu.setServerId(this.serverId);
        menu.setName(name);
        menu.setDescription(description);
        menu.setQuantity(Integer.parseInt(quantity));
        menu.setPrice(Float.parseFloat(price));
        if (serverId != null) {
            menu.setServerId(Long.parseLong(serverId));
        }

        menu.setThumbnail(BitMapToString(mThumbnail));

        new MenuEntityEditEndpointAsyncTask(this).execute(new Pair<Context, MenuEntity>(this, menu));

    }

    public void doneEditingMenu() {
            finish();
        }

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mThumbnail = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(mThumbnail);
        }
    }


    private Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}

