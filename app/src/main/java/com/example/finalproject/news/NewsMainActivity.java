package com.example.finalproject.news;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import java.net.URL;

//News API first activity
public class NewsMainActivity extends AppCompatActivity {


    String BASE_URL = "https://newsapi.org/v2/everything?apiKey=28051384fe9e4681893b96918c6c327d&q=";
    ListView listNews;
    Button searchButton;
    SearchView text;
    ProgressBar loader;
    String toSearch;
    Context thisApp;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_URL = "url";
    public static final String KEY_URLTOIMAGE = "urlToImage";
    public static final String KEY_SAVED ="saveToDatabase";
    public static final String COL_ID ="Id" ;
    public static final String KEY_POS = "pos";
    static ArrayList<Article> newsList = new ArrayList<Article>();
    MyNetworkRequest newsWorkerThread;
    static NewsAdapter adapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        FloatingActionButton fab = findViewById(R.id.newsHelp);
        fab.setOnClickListener(click -> {

                Intent nextActivity = new Intent(this, EmptyActivity.class);

                startActivity(nextActivity);

        });

        listNews = findViewById(R.id.listView);

        text = findViewById(R.id.editText);
        loader = findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);
        //Configure adaptor for listNews.
        adapter = new NewsAdapter(NewsMainActivity.this, newsList);
        listNews.setAdapter(adapter);
        //Need to check the database for any saved news.
        MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();

        this.getData();
        //If there is any need to populate it to the list window.
        newsWorkerThread = new MyNetworkRequest();

        String lastURLQuery =   PreferenceManager.getDefaultSharedPreferences(this).getString("LastURL", "");

        if (!lastURLQuery.isEmpty()){
            // here is the request to fetch and show data from Last searched URL automatically when activity loaded
            newsList.clear();
            new MyNetworkRequest().execute(lastURLQuery);
           // text.setText(lastURLQuery);
        }


        text.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                text.clearFocus();

                if (!(query.equals(""))) {
                    //Need to get all the news from the news link.

                    System.out.println("Value of text = " + query);
                    toSearch = BASE_URL + query;
                    try {
                        new MyNetworkRequest().execute();
                    } catch (Exception e) {
                        return true;
                    }

                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (!(query.equals(""))) {
                    newsWorkerThread.cancel(true);
                }
                return false;
            }
        });

        listNews.setOnItemClickListener((lv, vw, pos, id) -> {
            Bundle dataToPass = new Bundle();
            dataToPass.putString(KEY_TITLE, newsList.get(pos).getTitle());
            dataToPass.putString(KEY_DESCRIPTION,newsList.get(pos).getDescription() );

            dataToPass.putString(KEY_URL,newsList.get(pos).getUrl() );
            dataToPass.putString(KEY_URLTOIMAGE,newsList.get(pos).getUrlToImage() );
            dataToPass.putBoolean(KEY_SAVED,newsList.get(pos).IsSaved());
            dataToPass.putLong(COL_ID,newsList.get(pos).getId());
            dataToPass.putInt(KEY_POS,pos);
            Intent nextActivity = new Intent(this, NewsDetailsActivity.class);
            nextActivity.putExtras(dataToPass); //send data to next activity

            startActivity(nextActivity);


        });
    }
    //Updating the data in database
    public static void updateList(int pos, Long value)
    {
        newsList.get(pos).setId(value);
        if(value >0) {
            newsList.get(pos).setSaved(true);
        }else{

            newsList.get(pos).setSaved(false);

            newsList.remove(pos);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id =item.getItemId();
        if(id == R.id.action_settings)
        {
            return true;
        }

        if(item.getItemId() == R.id.newsMenu)
        {
            Intent intent =new Intent(this,NewsDetailsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void getData()
    {

// set data from db
        String [] columns = { MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.KEY_TITLE, MyDatabaseOpenHelper.KEY_DESCRIPTION, MyDatabaseOpenHelper.KEY_URL,
                MyDatabaseOpenHelper.KEY_URLTOIMAGE};
        Cursor result = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null,null,null,null,null,null);

        //Get column indexes
        int idConIndex = result.getColumnIndex(MyDatabaseOpenHelper.COL_ID);
        int titleIndex = result.getColumnIndex(MyDatabaseOpenHelper.KEY_TITLE);
        int descIndex = result.getColumnIndex(MyDatabaseOpenHelper.KEY_DESCRIPTION);
        int urlIndex = result.getColumnIndex(MyDatabaseOpenHelper.KEY_URL);
        int imageIndex = result.getColumnIndex(MyDatabaseOpenHelper.KEY_URLTOIMAGE);
        while (result.moveToNext())
        {
            long id = result.getLong(idConIndex);
            String title = result.getString(titleIndex);
            String desc = result.getString(descIndex);
            String url = result.getString(urlIndex);
            String imgUrl = result.getString(imageIndex);

            newsList.add(new Article(id,title,desc,url,imgUrl,true));

        }

    }

    class MyNetworkRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(String... strings) {
            String ret = null;
            String queryURL = "https://newsapi.org/v2/everything?apiKey=28051384fe9e4681893b96918c6c327d&q=";
            newsList.clear();
            //Connect to the server
            try {
                URL url = new URL(toSearch);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                //Set up the JSON object parser:
                // json is UTF-8 by default

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("articles");

                // Toast.makeText(News.this, jsonArray.toString(), Toast.LENGTH_SHORT).show();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject sourceObj = (JSONObject) jsonArray.getJSONObject(i).get("source");
                    String sourceName = sourceObj.getString("name");

                    //Create Article object and add to the list.
                    String title = jsonArray.getJSONObject(i).getString(KEY_TITLE);
                    String desc = jsonArray.getJSONObject(i).getString(KEY_DESCRIPTION);
                    String urlTo = jsonArray.getJSONObject(i).getString(KEY_URL);
                    String urlImage = jsonArray.getJSONObject(i).getString(KEY_URLTOIMAGE);

                    Article ac = new Article((long) 0, title, desc, urlTo, urlImage, false);
                    newsList.add(ac);

                }


                ret = "News Fetched successfully";
            } catch (JSONException jse) {
                ret = "JSON Object parse error";
            } catch (MalformedURLException mfe) {
                ret = "Malformed URL exception";
            } catch (IOException ioe) {
                ret = "IO Exception. Is the Wifi connected?";
            }
            //What is returned here will be passed as a parameter to onPostExecute:
            return ret;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(NewsMainActivity.this, s, Toast.LENGTH_SHORT).show();
            adapter = new NewsAdapter(NewsMainActivity.this, newsList);
            listNews.setAdapter(adapter);
            loader.setVisibility(View.GONE);


        }
    }


}