package com.tekdi.foodmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

import java.io.ByteArrayOutputStream;

/**
 * Created by fsd017 on 12/14/14.
 */
public class AddMenuActivity extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private Bitmap mThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_menu);

        mImageView = (ImageView) findViewById(R.id.menu_picture_view);
        mThumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_camera);
    }

    public void onMenuAddButtonClick(View v) {
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
        menu.setName(name);
        menu.setDescription(description);
        menu.setQuantity(Integer.parseInt(quantity));
        menu.setPrice(Float.parseFloat(price));
        if (serverId != null) {
            menu.setServerId(Long.parseLong(serverId));
        }

        menu.setThumbnail(BitMapToString(mThumbnail));

        new MenuEntityEndpointAsyncTask(this).execute(new Pair<Context, MenuEntity>(this, menu));

    }

    public void doneAddingMenu() {
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

    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}
