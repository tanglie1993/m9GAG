package com.example.tanglie1993.my9gag;

import android.graphics.Bitmap;
import android.view.WindowManager;
import android.widget.Adapter;

/**
 * Created by tanglie1993 on 2015/1/19.
 */
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

import uk.co.senab.photoview.PhotoViewAttacher;

public class FeedsAdapter extends BaseAdapter
{
    private Context context;

    private LayoutInflater layoutInflater;

    private List<Feed> list;

    private RequestQueue newRequestQueue;

    //构造方法，参数list传递的就是这一组数据的信息
    public FeedsAdapter(Context context, List<Feed> list)
    {
        this.context = context;

        layoutInflater = LayoutInflater.from(context);

        this.list = list;

        newRequestQueue = Volley.newRequestQueue(context);
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
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.feed_item_layout, null);
        }
        //得到条目中的子组件
        TextView tv1 = (TextView)convertView.findViewById(R.id.feedItemTextView);
        final ImageView iv = (ImageView)convertView.findViewById(R.id.feedItemImageView);

        //从list对象中为子组件赋值
        tv1.setText(list.get(position).caption);
        ImageRequest imageRequest = new ImageRequest(list.get(position).images.large,
                    new Response.Listener<Bitmap>()
                    {
                        @Override
                        public void onResponse(Bitmap response)
                        {
                            ViewGroup.LayoutParams para=iv.getLayoutParams();
                            WindowManager wm = (WindowManager) context.
                                    getSystemService(Context.WINDOW_SERVICE);
                            para.width = wm.getDefaultDisplay().getWidth();
                            iv.setLayoutParams(para);
                            iv.setImageBitmap(response);

                        }
                    }, 0, 0, Bitmap.Config.RGB_565, null);
        newRequestQueue.add(imageRequest);

        return convertView;
    }
}