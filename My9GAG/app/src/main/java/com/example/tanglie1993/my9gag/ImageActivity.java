package com.example.tanglie1993.my9gag;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import  uk.co.senab.photoview.PhotoViewAttacher;


public class ImageActivity extends ActionBarActivity {

    ImageView imageView;

    RequestQueue newRequestQueue;

    PhotoViewAttacher mAttacher;

    String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_test/";

    public static final Uri CONTENT_URI  = Uri.parse("content://com.example.tanglie1993.FeedsProvider");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        setImage();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_as_png) {
            BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
            Bitmap bm = bd.getBitmap();
            try{
                saveFile(bm, getIntent().getExtras().get("id")+".PNG");
            }catch(IOException e){
                e.printStackTrace();
            }
            return true;
        }else if(id == R.id.add_to_favorite){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Bundle bundle=getIntent().getExtras();
            Bitmap bmp=(Bitmap) bundle.get("largeImage");
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            ContentValues value=new ContentValues();
            value.put("ID", (String) bundle.get("id"));
            value.put("LARGE_IMAGE", os.toByteArray());
            value.put("CAPTION",(String) bundle.get("caption"));
            value.put("CATEGORY",(String) bundle.get("category"));
            String projection[]={"ID"};
            if(getContentResolver().query(CONTENT_URI, projection, "ID="+(String) bundle.get("id"), null, null).getCount()==0){
                getContentResolver().insert(CONTENT_URI, value);
                System.out.println("Insertion succeeded.");
            }
            else{
                System.out.println("Insertion failed.");
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bos.flush();
        bos.close();
    }

    private void setImage(){
        imageView= (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap((Bitmap) getIntent().getExtras().get("image"));

        LayoutParams para=imageView.getLayoutParams();
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        para.height = wm.getDefaultDisplay().getHeight();
        para.width = wm.getDefaultDisplay().getWidth();
        imageView.setLayoutParams(para);

        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openOptionsMenu();
                return false;
            }
        });

    }

}
