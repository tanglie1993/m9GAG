package com.example.tanglie1993.my9gag;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FeedsAdapter extends BaseAdapter
{
    private Context context;

    private LayoutInflater layoutInflater;

    private List<DataItem> list;

    private RequestQueue newRequestQueue;

    private HashMap<String,Bitmap> imageCache;

    final WindowManager wm;

    //构造方法，参数list传递的就是这一组数据的信息
    public FeedsAdapter(Context context, List<DataItem> list)
    {
        this.context = context;

        layoutInflater = LayoutInflater.from(context);

        this.list = list;

        newRequestQueue = Volley.newRequestQueue(context);

        imageCache=new HashMap<String, Bitmap>();

        wm = (WindowManager) context.
            getSystemService(Context.WINDOW_SERVICE);

    }

    public void updateList(List<DataItem> list){
        this.list = list;
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
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.feed_item_layout, null);
        }
        //得到条目中的子组件
        TextView tv1 = (TextView)convertView.findViewById(R.id.feedItemTextView);
        final ImageView iv = (ImageView)convertView.findViewById(R.id.feedItemImageView);

        //从list对象中为子组件赋值
        tv1.setText(list.get(position).caption);


        Bitmap bmp=list.get(position).largeImage;
        //iv.setImageBitmap(bmp);

        System.out.println("xxx"+imageCache.keySet().size());

        if(imageCache.get(list.get(position).largeImageURL)!=null){
            Bitmap cachebmp = imageCache.get(list.get(position).largeImageURL);
            System.out.println("hit1");
            if(cachebmp!=null){
                iv.setImageBitmap(adjustBitmap(cachebmp));
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

        ImageLoader.getInstance().loadImage(list.get(position).largeImageURL, new ImageLoadingListener() {

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
                System.out.println("time:"+(System.currentTimeMillis()-time));

                System.out.println("size(before):"+loadedImage.getByteCount());

                loadedImage=comp(loadedImage);
                imageCache.put(imageUri, loadedImage);

                System.out.println("size(after):"+loadedImage.getByteCount());

                iv.setImageBitmap(adjustBitmap(loadedImage));


                System.out.println(imageCache.keySet().size());
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        return convertView;
    }

    private Bitmap comp(Bitmap image) {

        Matrix matrix = new Matrix();
        matrix.setScale(0.33f, 0.33f);
        Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        return result;
    }


    private Bitmap adjustBitmap(Bitmap loadedImage){
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