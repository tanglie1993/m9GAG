package com.example.tanglie1993.my9gag;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by tanglie1993 on 2015/1/28.
 */
public class HashBitmapCache
{
    /**
     * 内存图片软引用缓冲
     */
    private HashMap<String, SoftReference<Bitmap>> imageCache = null;

    public HashBitmapCache() {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    public Bitmap loadBitmap(final String imageURL)
    {
        //在内存缓存中，则返回Bitmap对象
        if(imageCache.containsKey(imageURL))
        {
            SoftReference<Bitmap> reference = imageCache.get(imageURL);
            Bitmap bitmap = reference.get();
            if(bitmap != null)
            {
                return bitmap;
            }
        }
        return null;
    }

}