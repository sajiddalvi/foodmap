package com.tekdi.foodmap;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tekdi.foodmap.backend.menuEntityApi.model.MenuEntity;

/**
 * Created by fsd017 on 12/14/14.
 */
public class AddMenuActivity extends Activity {

    private ImageView mImageView;
    private Thumbnail mThumbnail;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CROP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_menu);

        mImageView = (ImageView) findViewById(R.id.menu_picture_view);
        mThumbnail = new Thumbnail(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
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
                onMenuAddButtonClick();
                break;
            case R.id.action_cancel:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    private void onMenuAddButtonClick() {
        EditText editText = (EditText) findViewById(R.id.menu_name_text);
        String name = editText.getText().toString();

        editText = (EditText) findViewById(R.id.menu_description_text);
        String description = editText.getText().toString();

        editText = (EditText) findViewById(R.id.menu_price_text);
        String price = editText.getText().toString();

        String serverId = Prefs.getServeIdPref(this);

        MenuEntity menu = new MenuEntity();
        menu.setName(name);
        menu.setDescription(description);
        menu.setPrice(Float.parseFloat(price));
        if (serverId != null) {
            menu.setServerId(Long.parseLong(serverId));
        }
        menu.setThumbnail(mThumbnail.getString());

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

        if  (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bm = (Bitmap) extras.get("data");
            mThumbnail.setBitmap(bm);

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                performCrop();
            } else if (requestCode == REQUEST_IMAGE_CROP) {
                mImageView.setImageBitmap(mThumbnail.getBitmap());
            }
        }
    }

    private void performCrop(){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(mThumbnail.getImageUri(), "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 100);
            cropIntent.putExtra("outputY", 100);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        }
        catch(ActivityNotFoundException error){
        }
    }


}
