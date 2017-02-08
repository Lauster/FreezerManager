package de.geek_hub.freezermanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ItemList freezedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        freezedItems = new ItemList(this);
        showItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        paintIconWhite(menu.findItem(R.id.action_sort));
        paintIconWhite(menu.findItem(R.id.action_filter));

        return true;
    }

    private void paintIconWhite(MenuItem menuItem) {
        if (menuItem != null) {
            Drawable normalDrawable = menuItem.getIcon();
            Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, android.R.color.white));

            menuItem.setIcon(wrapDrawable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_item_detail:
                Intent itemDetail = new Intent(this, ItemDetailActivity.class);
                startActivity(itemDetail);
                break;
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showItems() {
        ArrayAdapter<String> adapter;
        ListView lv  = (ListView)findViewById(R.id.itemList);

        final ArrayList<String> items = new ArrayList<>();
        items.add("Kaninchen");
        items.add("Schweinelende");
        items.add("2x Putenschnitzel");
        items.add("15 Fischstäbchen");
        items.add("Himbeeren");
        items.add("Rotkohl");
        items.add("Vollkornbrot");

        final ArrayList<String> items_sub = new ArrayList<>();
        items_sub.add("23.09.2015 | 1,2 kg");
        items_sub.add("11.11.2015 | 0,5 kg");
        items_sub.add("05.10.2016 | 0,5 kg");
        items_sub.add("28.05.2016");
        items_sub.add("01.08.2015 | 0,5 kg");
        items_sub.add("20.06.2016 | 0,4 kg");
        items_sub.add("07.10.2016 | 1 kg");

        /*adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                freezedItems.getAllItems());*/
        adapter = new ArrayAdapter (this, android.R.layout.simple_list_item_2, android.R.id.text1, items) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setText(items.get(position));
                text2.setText(items_sub.get(position));
                return view;
            }
        };
        lv.setAdapter(adapter);
    }

    public void createItem(View view) {
        Intent intent = new Intent(this, NewItemActivity.class);
        startActivityForResult(intent, 10);
    }
}
