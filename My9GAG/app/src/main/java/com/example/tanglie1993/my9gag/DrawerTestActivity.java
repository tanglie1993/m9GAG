package com.example.tanglie1993.my9gag;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DrawerTestActivity extends Activity
{

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    ListView contentListview;

    ArrayAdapter<String> contentAdapter;

    List largeImageList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_test);

        initDrawerListView();
        contentListview=(ListView) findViewById(R.id.testListView);
        requestData("hot/0");

    }

    private void initDrawerListView()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList= (ListView) findViewById(R.id.left_drawer);

       final String[] category = getResources().getStringArray(R.array.categories);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.array_list_view_layout, category));

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                // Highlight the selected item, update the title, and close the
                // drawer
                System.out.println("" + category[position]);
                requestData(category[position] + "/0");
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        mDrawerList.setFocusableInTouchMode(false);
    }

    private void requestData(String suburl){
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://infinigag-us.aws.af.cm/" + suburl,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                List<String> commentList=new ArrayList<String>();
                largeImageList=new ArrayList<String>();

                Gson mGson = new Gson();
                Feed.FeedRequestData frd = mGson.fromJson(response, Feed.FeedRequestData.class);
                for(Feed feed : frd.data){
                    commentList.add(feed.caption);
                    largeImageList.add(feed.images.large);
                    System.out.println(feed.caption);
                }

                String[] titles=new String[commentList.size()];
                for(int i=0;i<titles.length;i++){
                    titles[i]=commentList.get(i);
                }
                contentListview.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.array_list_view_layout, titles));
                setListeners();

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
        contentListview.setOnItemClickListener
                (
                        new AdapterView.OnItemClickListener()
                        {
                            public void onItemClick(AdapterView adapterView, View view,int arg2, long arg3)
                            {
                                int selectedPosition = arg2;
                                String imageurl = (String) largeImageList.get(selectedPosition);
                                Intent intent = new Intent(DrawerTestActivity.this, ImageActivity.class);
                                intent.putExtra("imageurl",imageurl);
                                System.out.println("selectedPosition:"+selectedPosition);
                                startActivity(intent);
                            }
                        }
                );
    }
}