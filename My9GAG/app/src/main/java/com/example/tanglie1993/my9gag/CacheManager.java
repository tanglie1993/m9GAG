package com.example.tanglie1993.my9gag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by tanglie1993 on 2015/2/2.
 */
public class CacheManager {
    Context context;
    public CacheManager(Context context){
        this.context=context;
    }

    public static void insertIntoContentProvider(String ID, Bitmap bmp, Context context){
        if(context.getContentResolver().query(FeedsProvider.CACHE_URI, FeedsProvider.CACHE_COLUMN, "ID='"+ID+"'", null, null).getCount()>0){
            return;
        }
        ContentValues values = new ContentValues();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BitmapProcessor.comp(bmp).compress(Bitmap.CompressFormat.PNG, 100, os);
        values.put("IMAGE",os.toByteArray());
        values.put("ID",ID);
        context.getContentResolver().insert(FeedsProvider.CACHE_URI, values);
        Cursor c = context.getContentResolver().query(FeedsProvider.CACHE_URI, FeedsProvider.CACHE_COLUMN, null, null, "INSERT_TIME ASC");
        if(c.getCount()>=100){
            c.moveToFirst();
            String deleteId=c.getString(c.getColumnIndexOrThrow("ID"));
            context.getContentResolver().delete(FeedsProvider.CACHE_URI, "ID='"+deleteId+"'", null);
        }

    }

    public static Bitmap getFromContentProvider(String ID, Context context){
        Cursor c = context.getContentResolver().query(FeedsProvider.CACHE_URI, FeedsProvider.CACHE_COLUMN, "ID='"+ID+"'", null, null);
        if(c.getCount()==0){
            return null;
        }
        else{
            c.moveToFirst();
            byte[] b = c.getBlob(c.getColumnIndexOrThrow("IMAGE"));
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, null);
            return BitmapProcessor.adjustBitmap(bitmap, context);
        }

    }
}
