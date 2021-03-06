package com.example.tanglie1993.my9gag;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Debug;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DrawerTestActivity extends ActionBarActivity
{

    DrawerLayout mDrawerLayout;

    ListView mDrawerList;

    StaggeredGridView contentListview;

    ArrayList[] dataItemList;

    String[] categoriesList;

    RequestQueue mQueue;

    FeedsAdapter myAdapter;

    Bitmap GREEN;

    int currentCategory;

    ProgressDialog myDialog;

    boolean onScrollEnabled=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_test);
        //startActivity(new Intent(DrawerTestActivity.this, StaggeredViewActivity.class));

        initDrawerListView();
        initFeedsListView();
        setListeners();
        initImageLoader();
        requestData(0);
        setActionBar();

    }

    private void setDialog(){
        myDialog = new ProgressDialog(this);
        myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        myDialog.setMessage("Loading, please wait...");
        myDialog.setCancelable(true);
        myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                myDialog.hide();
                finish();
            }
        });
        myDialog.show();

    }

    private void setActionBar() {
        setTitle("9GAG");

    }

    private void initDrawerListView()
    {
        mQueue = Volley.newRequestQueue(getApplicationContext());

        categoriesList=getResources().getStringArray(R.array.categories);
        currentCategory=0;
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
                if(position==3){
                    openFavorites();
                    mDrawerLayout.closeDrawer(mDrawerList);
                    return;
                }
                if(position!=currentCategory){
                    switchList(position);
                    currentCategory=position;
                    if(dataItemList[position].size()==0){
                        System.out.println("requestData from drawerlist");
                        requestData(position);
                    }
                }
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        mDrawerList.setFocusableInTouchMode(false);
    }

    private void switchList(int position){
        myAdapter.updateList(dataItemList[position]);
        myAdapter.notifyDataSetChanged();
    }

    private void initFeedsListView(){


        contentListview=(StaggeredGridView) findViewById(R.id.grid_view);
        dataItemList=new ArrayList[categoriesList.length];
        for(int i=0;i<categoriesList.length; i++){
            dataItemList[i]=new ArrayList<DataItem>();
        }
        myAdapter=new FeedsAdapter(getApplicationContext(),dataItemList[currentCategory]);
        contentListview.setAdapter(myAdapter);
        GREEN= BitmapFactory.decodeResource(getResources(), R.drawable.green);

    }

    private void initImageLoader(){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new WeakMemoryCache())
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void requestData(final int position){
        System.out.println("1");
        final int pos=position;
        setDialog();

        String next="0";
        if(dataItemList[position].size()>0){
            DataItem item=(DataItem) dataItemList[position].get(dataItemList[position].size()-1);
            next=item.next;

        }
        System.out.println("2");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://infinigag-us.aws.af.cm/" + categoriesList[position] +"/" + next,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("4");
                myDialog.dismiss();
                Gson mGson = new Gson();
                Feed.FeedRequestData frd = mGson.fromJson(response, Feed.FeedRequestData.class);
                String next=frd.getPage();

                for(Feed feed : frd.data){
                    final DataItem item=new DataItem();
                    item.next=next;
                    item.caption=feed.caption;
                    item.category=position;
                    item.id=feed.id;
                    item.largeImageURL=feed.images.large;
                    item.displayText="0%";

                    dataItemList[position].add(item);
                    myAdapter.notifyDataSetChanged();


                    System.out.println(feed.caption);
                    onScrollEnabled=true;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        }) {};
        System.out.println("3");
        mQueue.add(stringRequest);
    }

    private void setListeners(){
        contentListview.setOnItemClickListener
                (
                        new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView adapterView, View view, int arg2, long arg3) {
                                int selectedPosition = arg2;
                                DataItem item = (DataItem) dataItemList[currentCategory].get(selectedPosition);
                                Intent intent = new Intent(DrawerTestActivity.this, ImageActivity.class);
                                intent.putExtra("CATEGORY",currentCategory);
                                intent.putExtra("CAPTION", item.caption);
                                intent.putExtra("ID", item.id);
                                intent.putExtra("IMAGE_URL",item.largeImageURL);
                                startActivity(intent);

                            }
                        }
                );
    }

    private void openFavorites(){
        currentCategory=3;
        Cursor c=getContentResolver().query(FeedsProvider.FAVORITES_URI, FeedsProvider.FAVORITES_COLUMN, null, null, null);
        dataItemList[currentCategory].clear();
        switchList(currentCategory);
        if(c.getCount()==0){
            return;
        }
        c.moveToFirst();
        do{
            DataItem item=new DataItem();
            item.id=c.getString(0);

            item.largeImageURL=c.getString(c.getColumnIndexOrThrow("IMAGE_URL"));
            item.caption=c.getString(c.getColumnIndexOrThrow("CAPTION"));
            item.category=c.getInt(c.getColumnIndexOrThrow("CATEGORY"));
            System.out.println("id:"+item.id+"URL:"+item.largeImageURL+"caption:"+item.caption);
            dataItemList[currentCategory].add(item);
        }while(c.moveToNext());
        myAdapter.notifyDataSetChanged();
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
            if(currentCategory == 3){
                 return false;
            }
            requestData(currentCategory);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
