package com.example.tanglie1993.my9gag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    ListView listview;

    ArrayAdapter<String> adapter;

    AlphaInAnimationAdapter animationAdapter;

    List largeImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listview = new ListView(this);
        listview.findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this,R.layout.array_list_view_layout);
        largeImageList = new ArrayList<String>();
        listview.setAdapter(adapter);
        setContentView(listview);
        requestData();
        setListeners();

        Intent intent = new Intent(MainActivity.this, DrawerTestActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://infinigag-us.aws.af.cm/hot/0",  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson mGson = new Gson();
                Feed.FeedRequestData frd = mGson.fromJson(response, Feed.FeedRequestData.class);
                for(Feed feed : frd.data){
                    adapter.add(feed.caption);
                    largeImageList.add(feed.images.large);
                    System.out.println(feed.caption);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        }) {};
        mQueue.add(stringRequest);
    }

    private void setListeners(){
        listview.setOnItemClickListener
                (
                        new AdapterView.OnItemClickListener()
                        {
                            public void onItemClick(AdapterView adapterView, View view,int arg2, long arg3)
                            {
                                int selectedPosition = arg2;
                                String imageurl = (String) largeImageList.get(selectedPosition);
                                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                                intent.putExtra("imageurl",imageurl);
                                System.out.println("selectedPosition:"+selectedPosition);
                                startActivity(intent);
                            }
                        }
                );
    }
}
