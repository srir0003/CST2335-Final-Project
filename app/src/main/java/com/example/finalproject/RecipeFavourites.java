package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecipeFavourites extends AppCompatActivity {

    ArrayList<String> favourites = new ArrayList<>();
    ListView favList;
    BaseAdapter favAdapter;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_favourites);

        delete = findViewById(R.id.delete);
        favList = findViewById(R.id.favLV);
        favList.setAdapter(favAdapter = new MyListAdapter());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favourites.clear();
                favAdapter.notifyDataSetChanged();
                Toast toast=Toast.makeText(getApplicationContext(),R.string.toast,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.BOTTOM, 0, 0);
                toast.setMargin(50,50);
                toast.show();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> fav = new ArrayList<>();
        fav = data.getStringArrayListExtra("Favourites");
        favourites.add(fav.toString());
        favAdapter.notifyDataSetChanged();
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
            Intent i = new Intent(RecipeFavourites.this, Recipe.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyListAdapter extends BaseAdapter {
        public int getCount(){
            return favourites.size();
        }
        public String getItem(int position) {
            return favourites.get(position);
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
}
