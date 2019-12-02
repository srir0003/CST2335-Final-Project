package com.example.finalproject.news;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class MyDatabaseOpenHelper extends SQLiteOpenHelper
{
   public static final String DATABASE_NAME ="MyNewsDatabase";
   public static final int VERSION_NUM = 3;
   public static final String TABLE_NAME = "News";
   public static final String COL_ID ="Id" ;
    public static final String KEY_AUTHOR = "author";
    public  static final String KEY_TITLE = "title";
    public  static final String KEY_DESCRIPTION = "description";
    public  static final String KEY_URL = "url";
    public  static final String KEY_URLTOIMAGE = "urlToImage";
    public  static final String COL_NEWS_ID = "News_ID";
    private static MyDatabaseOpenHelper sInstance;
   // Context context;

   public MyDatabaseOpenHelper(Context context)
   {
       super(context,DATABASE_NAME,null,VERSION_NUM);
   }

    public static MyDatabaseOpenHelper getInstance(NewsDetailsActivity newsDetailsActivity) {
        if (sInstance == null) {
            Context context =null;
            sInstance = new MyDatabaseOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }




    @Override
    public void onCreate(SQLiteDatabase db)
    {
      db.execSQL("CREATE TABLE " + TABLE_NAME + "("
              + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
              + KEY_TITLE + " TEXT,"
              + KEY_DESCRIPTION + " TEXT,"
              + KEY_URL + " TEXT,"
              + KEY_URLTOIMAGE + " TEXT"
              +")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      onCreate(db);
    }

   public void onDowngrade(SQLiteDatabase db, int oldVersion,int newVersion)
   {
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
       onCreate(db);
   }

   public boolean addData(String title,String description,String url, String urlToImage)
   {
       SQLiteDatabase db = this.getWritableDatabase();
       ContentValues values = new ContentValues();

       values.put(KEY_TITLE,title);
       values.put(KEY_DESCRIPTION,description);
       values.put(KEY_URL,url);
       values.put(KEY_URLTOIMAGE,urlToImage);

       long result =db.insert(TABLE_NAME,null,values);

       if(result == 0)
       {
           return false;
       }else{
           return true;
       }
   }

    public void InsertNewsItem(Article article) {
    }

    public Article[] getAllSavedNews() {
       return getAllSavedNews() ;
    }
}
