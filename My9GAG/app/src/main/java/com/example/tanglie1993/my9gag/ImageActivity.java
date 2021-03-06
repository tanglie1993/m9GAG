package com.example.tanglie1993.my9gag;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import  uk.co.senab.photoview.PhotoViewAttacher;


public class ImageActivity extends ActionBarActivity {

    ImageView imageView;

    Bitmap green;

    Bitmap displayedBitmap;

    ProgressDialog MyDialog;

    String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_test/";

    String[] projection={"ID","IMAGE_URL","CAPTION","CATEGORY"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        green = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.green);
        setDialog();
        setImage();

    }

    private void setDialog(){
        MyDialog = new ProgressDialog(this);
        MyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        MyDialog.setMessage("Loading, please wait...");
        MyDialog.setCancelable(true);
        MyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                MyDialog.hide();
                finish();
            }
        });
        MyDialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if((Integer) getIntent().getExtras().get("CATEGORY")==3){
            getMenuInflater().inflate(R.menu.menu_favorites, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_image, menu);
        }

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

            try{
                saveFile(displayedBitmap, getIntent().getExtras().getString("ID")+".PNG");
            }catch(IOException e){
                e.printStackTrace();
            }
            return true;
        }else if(id == R.id.add_to_favorite){
            ContentValues values=new ContentValues();
            values.put("ID",getIntent().getExtras().getString("ID"));
            values.put("IMAGE_URL",getIntent().getExtras().getString("IMAGE_URL"));
            values.put("CAPTION",getIntent().getExtras().getString("CAPTION"));
            values.put("CATEGORY",getIntent().getExtras().getInt("CATEGORY"));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BitmapProcessor.comp(displayedBitmap).compress(Bitmap.CompressFormat.PNG, 100, os);
            values.put("IMAGE",os.toByteArray());


            if(getContentResolver().query(FeedsProvider.FAVORITES_URI, projection, "ID='"+ getIntent().getExtras().getString("ID")+"'", null, null).getCount()==0){
                getContentResolver().insert(FeedsProvider.FAVORITES_URI, values);
                System.out.println("Insertion succeeded.");
                Toast.makeText(getApplicationContext(), "Added to favorite",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                System.out.println("Insertion failed.");
            }
        }else if(id == R.id.delete_from_favorite){
            getContentResolver().delete(FeedsProvider.FAVORITES_URI, "ID='"+ getIntent().getExtras().getString("ID")+"'",null);
            Toast.makeText(getApplicationContext(), "Deleted from favorite",
                    Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(), "Saved to "+ALBUM_PATH,
                Toast.LENGTH_SHORT).show();
    }


    private void setImage(){
        imageView= (ImageView) findViewById(R.id.imageView);

        ImageLoader.getInstance().loadImage(getIntent().getExtras().getString("IMAGE_URL"), null, new DisplayImageOptions.Builder().cacheOnDisk(true).build(), new ImageLoadingListener() {

            long time=0;
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                time=System.currentTimeMillis();

            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                System.out.println("failed:"+failReason.toString());

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                System.out.println("loadingtime:"+(System.currentTimeMillis()-time));

                MyDialog.dismiss();

                displayedBitmap=loadedImage;
                LayoutParams para=imageView.getLayoutParams();
                WindowManager wm = (WindowManager) getApplicationContext()
                        .getSystemService(Context.WINDOW_SERVICE);

                para.height = wm.getDefaultDisplay().getHeight();
                para.width = wm.getDefaultDisplay().getWidth();
                imageView.setLayoutParams(para);
                imageView.setImageBitmap(loadedImage);
                PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
                mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        openOptionsMenu();
                        return false;
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                System.out.println("LoadingCancelled");
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                System.out.println("current:" + (float)current/(float) total);
                MyDialog.setMessage(""+Math.round((float)current*100/(float) total)+"%");
            }
        });

    }







}
