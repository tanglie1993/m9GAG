package com.example.tanglie1993.my9gag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import  uk.co.senab.photoview.PhotoViewAttacher;


public class ImageActivity extends ActionBarActivity {

    TextView tv;

    ImageView imageView;

    ListView listview;

    ArrayAdapter<String> adapter;

    RequestQueue newRequestQueue;

    PhotoViewAttacher mAttacher;

    String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_test/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        newRequestQueue = Volley.newRequestQueue(ImageActivity.this);
        setAdapter();
        requestData();
        tv=(TextView)findViewById(R.id.testTextView);
        registerForContextMenu(tv);


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
        if (id == R.id.action_settings) {
            BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
            Bitmap bm = bd.getBitmap();
            try{
                saveFile(bm, (String) getIntent().getExtras().get("id")+".JPEG");
            }catch(IOException e){
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                   ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_image_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveImage:
                BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
                Bitmap bm = bd.getBitmap();
                try{
                    saveFile(bm, (String) getIntent().getExtras().get("id"));
                }catch(IOException e){
                    e.printStackTrace();
                }

                return true;
            default:
                return false;
        }
    }

    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    private void requestData(){
        imageView= (ImageView) findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();

        ImageRequest imageRequest = new ImageRequest((String) bundle.get("imageurl"),
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        imageView.setImageBitmap(response);
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
                }, 0, 0, Bitmap.Config.RGB_565, null);
        newRequestQueue.add(imageRequest);

    }

    private void setAdapter(){
        listview = new ListView(this);
        listview.findViewById(R.id.left_drawer);
        adapter = new ArrayAdapter<String>(this,R.layout.array_list_view_layout);
        adapter.add("hot");
        adapter.add("new");
        adapter.add("trend");
    }
}
