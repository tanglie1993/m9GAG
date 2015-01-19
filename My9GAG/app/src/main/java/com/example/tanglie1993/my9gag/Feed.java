package com.example.tanglie1993.my9gag;

import android.database.Cursor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by storm on 14-3-25.
 */
public class Feed {
    private static final HashMap<String, Feed> CACHE = new HashMap<String, Feed>();

    public String id;
    public String caption;
    public String link;
    public Image images;
    public Vote votes;

    public class Image {
        public String small;
        public String normal;
        public String large;
    }

    private class Vote {
        public int count;
    }

    public static class FeedRequestData {
        public ArrayList<Feed> data;
        public Paging paging;

        public String getPage() {
            return paging.next;
        }

    }


    private class Paging {
        public String next;
    }
}
