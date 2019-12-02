package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Recipe extends AppCompatActivity {

    Button recipeBtn;
    Button saveBtn;
    Button favBtn;
    ProgressBar recipePB;
    ListView recipeLV;
    BaseAdapter recipeAdapter;
    ArrayList<String> recipeList = new ArrayList<>();
    ArrayList<String> favList = new ArrayList<>();
    ProgressDialog progressDialog;

    static String chickenUrl = "http://torunski.ca/FinalProjectChickenBreast.json";
    static String lasagnaUrl = "http://torunski.ca/FinalProjectLasagna.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeBtn = findViewById(R.id.recipeBtn);
        saveBtn = findViewById(R.id.saveBtn);
        favBtn = findViewById(R.id.favBtn);
        recipePB = findViewById(R.id.recipePB);
        recipeLV = findViewById(R.id.recipeLV);
        recipeLV.setAdapter(recipeAdapter = new MyListAdapter());

        final EditText searchText = findViewById(R.id.recipeET);
        final SharedPreferences prefs = getSharedPreferences("SearchRecipe", MODE_PRIVATE);
        String text = prefs.getString("search", "");
        searchText.setText(text);
        recipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("search", searchText.getText().toString());
                editor.commit();
                alert();
                recipeAdapter.notifyDataSetChanged();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favList.add(recipeList.toString());
                Snackbar snackbar = Snackbar.make(findViewById(R.id.recipeView), R.string.snackbar, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Recipe.this, RecipeFavourites.class);
                i.putStringArrayListExtra("Favourites", favList);
                startActivity(i);
            }
        });

    }

    public void alert() {
        View middle = getLayoutInflater().inflate(R.layout.recipe_alert, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setPositiveButton(R.string.chicken, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        recipeList.clear();
                        recipeAdapter.notifyDataSetChanged();
                        new ChickenTask().execute();
                    }
                })
                .setNegativeButton(R.string.lasagna, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        recipeList.clear();
                        recipeAdapter.notifyDataSetChanged();
                        new LasagnaTask().execute();
                    }
                }).setView(middle);
        builder.create().show();
    }

    public void help() {
        View middle = getLayoutInflater().inflate(R.layout.recipe_help, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).setView(middle);
        builder.create().show();
    }

    private class ChickenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Recipe.this);
            progressDialog.setMessage("Loading Recipes");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            Json json = new Json();
            String urlString = json.makeConnection(chickenUrl);
            if (urlString != null) {
                try {
                    JSONObject jsonObj = new JSONObject(urlString);
                    JSONArray recipeArray = jsonObj.getJSONArray("recipes");
                    for (int i = 0; i < recipeArray.length(); i++) {
                        JSONObject rowItem = recipeArray.getJSONObject(i);
                        String id = rowItem.getString("recipe_id");
                        String publisher = rowItem.getString("publisher");
                        String title = rowItem.getString("title");
                        String sourceUrl = rowItem.getString("source_url");
                        String imageUrl = rowItem.getString("image_url");
                        String f2fUrl = rowItem.getString("f2f_url");
                        String socialRank = rowItem.getString("social_rank");
                        String publisherUrl = rowItem.getString("publisher_url");

                        HashMap<String, String> items = new HashMap<>();
                        items.put("\n" + "Recipe ID: ", id + "\n");
                        items.put("\n" + "Publisher: ", publisher + "\n");
                        items.put("\n" + "Title: ", title + "\n");
                        items.put("\n" + "Source: ", sourceUrl + "\n");
                        items.put("\n" + "Image: ", imageUrl + "\n");
                        items.put("\n" + "F2F Url: ", f2fUrl + "\n");
                        items.put("\n" + "Social Rank: ", socialRank + "\n");
                        items.put("\n" + "Publisher Url: ", publisherUrl + "\n");
                        recipeList.add(String.valueOf(items));
                        System.out.println("Recipe List:    " + recipeArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            recipeAdapter.notifyDataSetChanged();
        }
    }

    private class LasagnaTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Recipe.this);
            progressDialog.setMessage("Loading Recipes");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            Json json = new Json();
            String urlString = json.makeConnection(lasagnaUrl);
            if (urlString != null) {
                try {
                    JSONObject jsonObj = new JSONObject(urlString);
                    JSONArray recipeArray = jsonObj.getJSONArray("recipes");
                    for (int i = 0; i < recipeArray.length(); i++) {
                        JSONObject rowItem = recipeArray.getJSONObject(i);
                        String id = rowItem.getString("recipe_id");
                        String publisher = rowItem.getString("publisher");
                        String title = rowItem.getString("title");
                        String sourceUrl = rowItem.getString("source_url");
                        String imageUrl = rowItem.getString("image_url");
                        String f2fUrl = rowItem.getString("f2f_url");
                        String socialRank = rowItem.getString("social_rank");
                        String publisherUrl = rowItem.getString("publisher_url");

                        HashMap<String, String> items = new HashMap<>();
                        items.put("\n" + "Recipe ID: ", id + "\n");
                        items.put("\n" + "Publisher: ", publisher + "\n");
                        items.put("\n" + "Title: ", title + "\n");
                        items.put("\n" + "Source: ", sourceUrl + "\n");
                        items.put("\n" + "Image: ", imageUrl + "\n");
                        items.put("\n" + "F2F Url: ", f2fUrl + "\n");
                        items.put("\n" + "Social Rank: ", socialRank + "\n");
                        items.put("\n" + "Publisher Url: ", publisherUrl + "\n");
                        recipeList.add(String.valueOf(items));
                        System.out.println("Recipe List:      " + recipeArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            recipeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if(item.getItemId() == R.id.goBack){
            Intent i = new Intent(Recipe.this, MainActivity.class);
            startActivity(i);
        }
        if(id == R.id.help){
            help();
        }
        return super.onOptionsItemSelected(item);
    }

    public class Json {
        public Json() { }
        public String makeConnection(String request) {
            String response = null;
            try {
                URL url = new URL(request);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                response = makeString(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        private String makeString(InputStream is) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    private class MyListAdapter extends BaseAdapter{
        public int getCount(){
            return recipeList.size();
        }
        public String getItem(int position) {
            return recipeList.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View v, ViewGroup parent) {
            View row = v;
            if (v == null)
                row = getLayoutInflater().inflate(R.layout.recipe_row, null);
            TextView titleString = row.findViewById(R.id.recipeRowTitle);
            TextView imageString = row.findViewById(R.id.recipeRowImage);
            TextView urlString = row.findViewById(R.id.recipeRowUrl);
            titleString.setText(getItem(position) + "\n");
            imageString.setText(getItem(position) + "\n");
            urlString.setText(getItem(position) + "\n");
            return row;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
