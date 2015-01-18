package com.example.tanglie1993.my9gag;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import  uk.co.senab.photoview.PhotoViewAttacher;


public class ImageActivity extends ActionBarActivity {

    ImageView imageView;

    ListView listview;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        setAdapter();
        requestData();


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestData(){
        imageView= (ImageView) findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        RequestQueue newRequestQueue = Volley.newRequestQueue(ImageActivity.this);
        ImageRequest imageRequest = new ImageRequest((String) bundle.get("imageurl"),
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        imageView.setImageBitmap(response);

                        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
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