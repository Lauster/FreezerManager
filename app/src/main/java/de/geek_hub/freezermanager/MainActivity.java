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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ItemList freezedItems;

    static final int NEW_ITEM_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        freezedItems = new ItemList(this);
        showItems();

        ListView listView = (ListView) findViewById(R.id.itemList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent itemDetail = new Intent(getApplicationContext(), ItemDetailActivity.class);
                itemDetail.putExtra("position", i);
                startActivity(itemDetail);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_ITEM_REQUEST:
                Item newItem = new Item(data.getStringExtra("name"));
                newItem.setWeight(data.getFloatExtra("weight", -1));
                newItem.setCategory(data.getStringExtra("category"));

                freezedItems.addItem(newItem);

                this.showItems();
                break;
            default:
                Toast.makeText(this, requestCode + "\n" + resultCode, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void showItems() {
        final ArrayList<Item> allItems = freezedItems.getAllItems();
        ArrayAdapter<String> adapter;
        ListView lv = (ListView) findViewById(R.id.itemList);

        adapter = new ArrayAdapter (this, android.R.layout.simple_list_item_2, android.R.id.text1, allItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setText(allItems.get(position).getName());
                text2.setText(allItems.get(position).getCategory());
                return view;
            }
        };
        lv.setAdapter(adapter);
    }

    public void createItem(View view) {
        Intent intent = new Intent(this, NewItemActivity.class);
        startActivityForResult(intent, NEW_ITEM_REQUEST);
    }
}
