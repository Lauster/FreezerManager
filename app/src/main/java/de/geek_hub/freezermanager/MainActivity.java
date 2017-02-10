package de.geek_hub.freezermanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ItemList frozenItems;
    private ArrayAdapter<String> listAdapter;

    static final int NEW_ITEM_REQUEST = 10;
    static final int ITEM_DETAIL_REQUEST = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.frozenItems = new ItemList(this);
        showItems();

        ListView listView = (ListView) findViewById(R.id.itemList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent itemDetail = new Intent(getApplicationContext(), ItemDetailActivity.class);
                itemDetail.putExtra("item", frozenItems.getItem(i));
                itemDetail.putExtra("id", i);
                startActivityForResult(itemDetail, ITEM_DETAIL_REQUEST);
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
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                settings.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT,
                        SettingsActivity.NotificationPreferenceFragment.class.getName() );
                settings.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true );
                startActivity(settings);
            default:
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case NEW_ITEM_REQUEST:
                Item newItem = data.getParcelableExtra("newItem");

                frozenItems.addItem(newItem);

                this.showItems();
                break;
            case ITEM_DETAIL_REQUEST:
                switch (data.getStringExtra("action")) {
                    case "defrost":
                        final Item deletedItem = this.frozenItems.deleteItem(data.getIntExtra("id", -1));
                        this.listAdapter.notifyDataSetChanged();
                        Snackbar.make(findViewById(R.id.main_activity_coordinator_layout),
                                    deletedItem.getName() + getResources().getString(R.string.snackbar_defrost),
                                    Snackbar.LENGTH_LONG)
                                .setAction(R.string.snackbar_defrost_undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        frozenItems.addItem(deletedItem);
                                        listAdapter.notifyDataSetChanged();
                                    }
                                }).setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                                .show();
                        break;
                    case "edit":
                        Intent itemDetail = new Intent(getApplicationContext(), ItemDetailActivity.class);
                        itemDetail.putExtra("item", frozenItems.getItem(0));
                        itemDetail.putExtra("id", 0);
                        startActivityForResult(itemDetail, ITEM_DETAIL_REQUEST);
                        break;
                }
                break;
            default:
                Toast.makeText(this, requestCode + "\n" + resultCode, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void showItems() {
        final ArrayList<Item> allItems = frozenItems.getAllItems();
        ListView lv = (ListView) findViewById(R.id.itemList);

        this.listAdapter = new ArrayAdapter (this, android.R.layout.simple_list_item_2, android.R.id.text1, allItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setText(allItems.get(position).getName());
                text2.setText(allItems.get(position).getCategory());
                return view;
            }
        };
        lv.setAdapter(this.listAdapter);
    }

    public void createItem(View view) {
        Intent intent = new Intent(this, NewItemActivity.class);
        startActivityForResult(intent, NEW_ITEM_REQUEST);
    }
}
