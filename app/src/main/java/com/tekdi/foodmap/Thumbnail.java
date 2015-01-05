package com.tekdi.foodmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by fsd017 on 1/5/15.
 */
public class Thumbnail {

    private Bitmap mBitmap;
    private Context context;

    public Thumbnail(Context context) {
        this.context = context;
        this.mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_camera);
    }

    public Thumbnail(Bitmap bm) {
        this.mBitmap = bm;
    }

    public Thumbnail(String encodedString) {
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            this.mBitmap = bitmap;
        }catch(Exception e){
            e.getMessage();
            this.mBitmap = null;
        }
    }

    public void setBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            this.mBitmap = bitmap;
        } catch (Exception e) {
            e.getMessage();
            this.mBitmap = null;
        }
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public Bitmap getBitmap() { return this.mBitmap; }

    public String getString(){
        ByteArrayOutputStream ByteStream=new  ByteArrayOutputStream();
        this.mBitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Uri getImageUri() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        this.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.context.getContentResolver(), this.mBitmap, "Title", null);
        return Uri.parse(path);
    }
}
