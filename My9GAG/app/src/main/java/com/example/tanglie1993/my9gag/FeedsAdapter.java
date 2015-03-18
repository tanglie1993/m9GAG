package com.example.tanglie1993.my9gag;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Debug;
import android.view.WindowManager;
import android.widget.Adapter;

/**
 * Created by tanglie1993 on 2015/1/19.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FeedsAdapter extends BaseAdapter
{
    private Context context;

    private LayoutInflater layoutInflater;

    private List<DataItem> list;

    private RequestQueue newRequestQueue;

    private HashMap<String,Bitmap> imageCache;

    final WindowManager wm;

    private Bitmap green;

    long memory;

    //构造方法，参数list传递的就是这一组数据的信息
    public FeedsAdapter(Context context, List<DataItem> list)
    {
        this.context = context;

        layoutInflater = LayoutInflater.from(context);

        this.list = list;

        newRequestQueue = Volley.newRequestQueue(context);

        imageCache=new HashMap<String, Bitmap>();

        green = BitmapFactory.decodeResource(context.getResources(),R.drawable.green);

        wm = (WindowManager) context.
            getSystemService(Context.WINDOW_SERVICE);

    }

    public void updateList(List<DataItem> list){
        this.list = list;
        ImageLoader.getInstance().stop();
    }

    //得到总的数量
    public int getCount()
    {
        // TODO Auto-generated method stub
        return this.list!=null? this.list.size(): 0 ;
    }

    //根据ListView位置返回View
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return this.list.get(position);
    }

    //根据ListView位置得到List中的ID
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    //根据位置得到View对象
    public View getView(int position, View convertView, ViewGroup parent)
    {
        long time=System.currentTimeMillis();
        System.out.println("memory:"+memory);
        if(convertView!=null) {
            System.out.println("convertView:"+convertView.getId());
            ViewGroup layout=(ViewGroup) convertView;
            ImageView imv=(ImageView) layout.getChildAt(1);
            BitmapDrawable drawable=(BitmapDrawable) imv.getDrawable();
            Bitmap bmp=drawable.getBitmap();
            memory-=bmp.getByteCount();
            bmp.recycle();
            if(bmp.isRecycled()==true){
                System.out.println("recycle success");
            }else{
                System.out.println("recycle failed");
            }

        }
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.feed_item_layout, null);
        }
        //得到条目中的子组件
        final TextView tv1 = (TextView)convertView.findViewById(R.id.feedItemTextView);
        final ImageView iv = (ImageView)convertView.findViewById(R.id.feedItemImageView);
        final String caption = list.get(position).displayText;
        convertView.setTag(position);

        //从list对象中为子组件赋值
        tv1.setText(list.get(position).caption);

        if(imageCache.get(list.get(position).largeImageURL)!=null){
            Bitmap cachebmp = imageCache.get(list.get(position).largeImageURL);
            System.out.println("hit1");
            if(cachebmp!=null){
                Bitmap bmp=BitmapProcessor.adjustBitmap(cachebmp, context);
                iv.setImageBitmap(bmp);
                memory+=bmp.getByteCount();

                System.out.println("setImageBitmap from cache");
                System.out.println("hit2,time:"+(System.currentTimeMillis()-time));


                return convertView;
            }
            else{
                System.out.println("miss2"+(System.currentTimeMillis()-time));
            }

        }
        else{
            System.out.println("miss1");
        }

        Cursor c=context.getContentResolver().query(FeedsProvider.FAVORITES_URI, FeedsProvider.FAVORITES_COLUMN, "IMAGE_URL='" +list.get(position).largeImageURL+"'", null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            byte[] b = c.getBlob(c.getColumnIndexOrThrow("IMAGE"));

            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, null);
            iv.setImageBitmap(BitmapProcessor.adjustBitmap(bitmap, context));
            tv1.setText(caption);
            return convertView;
        }

        Bitmap bmp=CacheManager.getFromContentProvider(list.get(position).id, context);
        if(bmp!=null){
            iv.setImageBitmap(bmp);
            return convertView;
        }

        final int selectedPosition=position;
        final View finalConvertView = convertView;

        ImageLoader.getInstance().loadImage(list.get(position).largeImageURL,null, new DisplayImageOptions.Builder().cacheOnDisk(true).build(), new ImageLoadingListener() {

            long time=0;
            final int initialPosition = selectedPosition;
            final View thisView = finalConvertView;
            final TextView thisTextView = (TextView) thisView.findViewById(R.id.feedItemTextView);
            final ImageView thisImageView = (ImageView) thisView.findViewById(R.id.feedItemImageView);

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                time=System.currentTimeMillis();
                iv.setImageBitmap(BitmapProcessor.adjustBitmap(green, context));
                tv1.setText("0%");

            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                System.out.println("failed:"+failReason.toString());

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                System.out.println("time:"+(System.currentTimeMillis()-time));
                System.out.println("imageUri:"+imageUri);
                if(loadedImage==null){
                    System.out.println("loadedImage==null");
                }
                else{
                    System.out.println("loadedImage!=null");
                }


                Bitmap compressed=BitmapProcessor.comp(loadedImage);
                imageCache.put(imageUri, compressed);
                CacheManager.insertIntoContentProvider(list.get(selectedPosition).id, loadedImage, context);

                loadedImage.recycle();
                list.get(initialPosition).displayText = list.get(initialPosition).caption;
                if((Integer) thisView.getTag() == initialPosition){
                    thisTextView.setText(list.get(initialPosition).caption);
                    thisImageView.setImageBitmap(BitmapProcessor.adjustBitmap(compressed, context));

                }

                System.out.println("size(after):"+loadedImage.getByteCount());

                System.out.println("setImageBitmap from ImageLoadingListener");
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                System.out.println("LoadingCancelled");
            }
        },  new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                tv1.setText(""+Math.round((float)current*100/(float) total)+"%");
            }
        });
        return convertView;
    }



}