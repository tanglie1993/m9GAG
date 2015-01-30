package com.example.tanglie1993.my9gag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.WindowManager;

/**
 * Created by tanglie1993 on 2015/1/30.
 */
public class BitmapProcessor {
    public static Bitmap adjustBitmap(Bitmap loadedImage, Context context){
        WindowManager wm = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        float scaleWidth = ((float) screenWidth ) /loadedImage.getWidth();
        int picHeight=Math.round(500/scaleWidth);
        if(loadedImage.getHeight()<picHeight){
            picHeight=loadedImage.getHeight();
        }


        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap picNewRes = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), picHeight, matrix, true);
        return picNewRes;
    }
}
