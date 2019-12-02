package com.example.finalproject.news;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;

import static com.example.finalproject.news.NewsMainActivity.COL_ID;
import static com.example.finalproject.news.NewsMainActivity.KEY_DESCRIPTION;
import static com.example.finalproject.news.NewsMainActivity.KEY_POS;
import static com.example.finalproject.news.NewsMainActivity.KEY_SAVED;
import static com.example.finalproject.news.NewsMainActivity.KEY_TITLE;
import static com.example.finalproject.news.NewsMainActivity.KEY_URL;
import static com.example.finalproject.news.NewsMainActivity.KEY_URLTOIMAGE;

//This class shows the detailed information
public class NewsDetailsActivity extends AppCompatActivity
{
    TextView fragTitle ,fragDesc;
    ImageView fragImage;
    Button broswer,saveButton,deleteButton;
    String title, description, url, imageUrl;
    boolean isSaved = false;
    MyDatabaseOpenHelper database;
    WebView webView;
    ProgressBar loader;
    int position;
    long id;

    Article article;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_news);


        title =getIntent().getStringExtra(KEY_TITLE);

        description =getIntent().getStringExtra(KEY_DESCRIPTION);
        url = getIntent().getStringExtra(KEY_URL);
        imageUrl = getIntent().getStringExtra(KEY_URLTOIMAGE);
        isSaved = getIntent().getBooleanExtra(KEY_SAVED,false);
        position = getIntent().getIntExtra(KEY_POS,0);
        id = getIntent().getLongExtra(COL_ID,0);
        fragTitle =findViewById(R.id.fragTitle);
        fragDesc = findViewById(R.id.fragDesc);
        fragImage =findViewById(R.id.fragImage);
        broswer = findViewById(R.id.broswer);
        saveButton =findViewById(R.id.saveButton);
        deleteButton =findViewById(R.id.deleteButton);

        // check if the data is already stored or not
        if(isSaved)
        {
            saveButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }else{
            saveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        fragTitle.setText(title);
        fragDesc.setText(description);

        Intent intent =getIntent();

        Glide.with(fragImage.getContext()).load(imageUrl).into(fragImage);

        //Save the data to the dataBase
         saveButton.setOnClickListener(click -> {
             MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
             SQLiteDatabase db = dbOpener.getWritableDatabase();

             ContentValues newRowValues = new ContentValues();
             newRowValues.put(MyDatabaseOpenHelper.KEY_TITLE,title);
             newRowValues.put(MyDatabaseOpenHelper.KEY_DESCRIPTION,description);
             newRowValues.put(MyDatabaseOpenHelper.KEY_URL,url);
             newRowValues.put(MyDatabaseOpenHelper.KEY_URLTOIMAGE,imageUrl);

             long newId = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);

             if(newId > 0)
             {
                 saveButton.setVisibility(View.INVISIBLE);
                 deleteButton.setVisibility(View.INVISIBLE);
                 NewsMainActivity.updateList(position,newId);
             }else{
                 Toast.makeText(getApplicationContext(), "Unable to insert the record " + title, Toast.LENGTH_SHORT);
             }
         });
          //Deleting from data base
         deleteButton.setOnClickListener(click ->{
             MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
             SQLiteDatabase db = dbOpener.getWritableDatabase();
             int value = db.delete(MyDatabaseOpenHelper.TABLE_NAME, "id=?", new String[]{Long.toString(id)});
             if(value > 0) {
                 NewsMainActivity.updateList(position, (long) 0);
                 saveButton.setVisibility(View.INVISIBLE);
                 deleteButton.setVisibility(View.INVISIBLE);
             }else{
                 Toast.makeText(getApplicationContext(), "Unable to delete the record " + title, Toast.LENGTH_SHORT);
             }
         });

        broswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open URL in web browser
                Intent intent =new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

    }
}
