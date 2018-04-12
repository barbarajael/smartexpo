package cn.smartexpo.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.smartexpo.R;

public class NoticiasActivity extends ActionBarActivity
{
    private ListView listView;
    protected ArrayList<String> collectionNews = new ArrayList<>();
    protected String news;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_lists);

        // em falta: popular o array list
        if(news != null)
            if (!collectionNews.contains(news))
                collectionNews.add(news);

        if (collectionNews.isEmpty())
            Toast.makeText(this, getResources().getString(R.string.noticias_not_found), Toast.LENGTH_LONG).show();

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        // Defined Array values to show in ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, collectionNews);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }
}