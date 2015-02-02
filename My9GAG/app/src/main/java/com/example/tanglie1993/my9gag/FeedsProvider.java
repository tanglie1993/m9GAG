package com.example.tanglie1993.my9gag;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.google.gson.Gson;

public class FeedsProvider extends ContentProvider {

    public static final String FAVORITES = "favorites";

    public static final String CACHE = "cache";

    public static final String BUNDLE = "bundle";

    public static final String DATABASE_NAME = "FeedsProvider";

    public static final int DATABASE_VERSION=1;

    public static final Uri CONTENT_URI  = Uri.parse("content://com.example.tanglie1993.FeedsProvider");

    public static final Uri CACHE_URI  = Uri.parse("content://com.example.tanglie1993.FeedsProvider/cache");

    public static final Uri FAVORITES_URI  = Uri.parse("content://com.example.tanglie1993.FeedsProvider/favorites");

    public static final String[] FAVORITES_COLUMN={"ID","IMAGE_URL","IMAGE","CAPTION","CATEGORY"};

    public static final String[] CACHE_COLUMN={"ID","IMAGE","INSERT_TIME"};

    private DatabaseHelper dbHelper;

    private UriMatcher matcher;

    public FeedsProvider() {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI("com.example.tanglie1993.FeedsProvider",FAVORITES, 0);
        matcher.addURI("com.example.tanglie1993.FeedsProvider",CACHE, 1);
        matcher.addURI("com.example.tanglie1993.FeedsProvider",BUNDLE, 2);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //创建用于存储数据的表
            db.execSQL("Create table " + FAVORITES + " ( ID TEXT PRIMARY KEY, IMAGE_URL TEXT, IMAGE BLOB, CAPTION TEXT, CATEGORY INT);");
            db.execSQL("Create table " + CACHE + " ( ID TEXT PRIMARY KEY, IMAGE BLOB, INSERT_TIME TIMESTAMP);");
            db.execSQL("Create table " + BUNDLE + " ( IMAGE BLOB );");
        }



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + FAVORITES);
            db.execSQL("DROP TABLE IF EXISTS" + CACHE);
            db.execSQL("DROP TABLE IF EXISTS" + BUNDLE);
            onCreate(db);
        }


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (matcher.match(uri)) {
            case 0:
                db.delete(FAVORITES, selection, selectionArgs);
                return 0;
            case 1:
                db.delete(CACHE, selection, selectionArgs);
                return 0;
            case 2:
                db.delete(BUNDLE, selection, selectionArgs);
            default://不匹配
                return 1;
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId=0;
        switch (matcher.match(uri)) {
            case 0:
                rowId = db.insert(FAVORITES, null, values);
                break;
            case 1:
                rowId = db.insert(CACHE, null, values);
                break;
            case 2:
                rowId = db.insert(BUNDLE, null, values);
                break;
            default://不匹配
                break;
        }
        if(rowId > 0){
            Uri insertedUserUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(insertedUserUri, null);

            return insertedUserUri;
        }
        throw new SQLException("Failed to insert row into" + uri);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return (dbHelper == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (matcher.match(uri)) {
            case 0:
                qb.setTables(FAVORITES);
                break;
            case 1:
                qb.setTables(CACHE);
                break;
            case 2:
                qb.setTables(BUNDLE);
                break;
            default://不匹配
                break;
        }

        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.

        throw new UnsupportedOperationException("Not yet implemented");
    }


}
