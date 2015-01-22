package com.example.tanglie1993.my9gag;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DrawerTestActivity extends ActionBarActivity
{

    private DrawerLayout mDrawerLayout;



    private ListView mDrawerList;

    ListView contentListview;

    ArrayList[] feedsList;

    ArrayList[] nextList;

    String[] categoriesList;

    RequestQueue mQueue;

    int currentCategory;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_test);

        initDrawerListView();
        contentListview=(ListView) findViewById(R.id.testListView);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        categoriesList=getResources().getStringArray(R.array.categories);
        currentCategory=0;
        feedsList=new ArrayList[categoriesList.length];
        for(int i=0;i<categoriesList.length; i++){
            feedsList[i]=new ArrayList<Feed>();
        }
        nextList=new ArrayList[categoriesList.length];
        for(int i=0;i<categoriesList.length; i++){
            nextList[i]=new ArrayList<String>();
        }
        requestData(0);

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
                currentCategory=position;
                requestData(position);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        mDrawerList.setFocusableInTouchMode(false);
    }

    private void requestData(final int position){
        final int pos=position;

        String next="0";
        if(nextList[position].size()>0){
            next=(String) nextList[position].get(nextList[position].size()-1);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://infinigag-us.aws.af.cm/" + categoriesList[position] +"/" + next,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson mGson = new Gson();
                Feed.FeedRequestData frd = mGson.fromJson(response, Feed.FeedRequestData.class);
                String next=frd.getPage();

                for(Feed feed : frd.data){
                    nextList[position].add(next);
                    feedsList[position].add(feed);
                    System.out.println(feed.caption);
                }

                FeedsAdapter myAdapter=new FeedsAdapter(getApplicationContext(),feedsList[position]);
                AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(myAdapter);
                animationAdapter.setAbsListView(contentListview);
                contentListview.setAdapter(animationAdapter);
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
                        new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView adapterView, View view, int arg2, long arg3) {
                                int selectedPosition = arg2;
                                Feed feed = (Feed) feedsList[currentCategory].get(selectedPosition);
                                String imageurl = feed.images.large;
                                Intent intent = new Intent(DrawerTestActivity.this, ImageActivity.class);
                                intent.putExtra("imageurl", imageurl);
                                System.out.println("selectedPosition:" + selectedPosition);
                                startActivity(intent);
                            }
                        }
                );
        contentListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                //Check if the last view is visible
                if (++firstVisibleItem + visibleItemCount > totalItemCount) {
                    requestData(currentCategory);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                //TODO
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_loadmore) {
            requestData(currentCategory);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
