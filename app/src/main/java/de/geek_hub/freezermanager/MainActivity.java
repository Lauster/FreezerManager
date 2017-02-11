package de.geek_hub.freezermanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ItemList frozenItems;
    private RecyclerView itemList;
    private RecyclerView.Adapter itemListAdapter;

    static final int NEW_ITEM_REQUEST = 10;
    static final int ITEM_DETAIL_REQUEST = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.frozenItems = new ItemList(this);
        this.itemList = (RecyclerView) findViewById(R.id.item_list);

        showItems();

        ItemClickSupport.addTo(itemList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent itemDetail = new Intent(getApplicationContext(), ItemDetailActivity.class);
                itemDetail.putExtra("item", frozenItems.getItem(position));
                itemDetail.putExtra("id", position);
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
                        this.itemListAdapter.notifyDataSetChanged();
                        Snackbar.make(findViewById(R.id.main_activity_inner_coordinator_layout),
                                    deletedItem.getName() + getResources().getString(R.string.snackbar_defrost),
                                    Snackbar.LENGTH_LONG)
                                .setAction(R.string.snackbar_defrost_undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        frozenItems.addItem(deletedItem);
                                        itemListAdapter.notifyDataSetChanged();
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
        this.itemList.setHasFixedSize(true);

        LinearLayoutManager itemListLayoutManager = new LinearLayoutManager(this);
        this.itemList.setLayoutManager(itemListLayoutManager);

        this.itemListAdapter = new ItemListAdapter(this.frozenItems, this);
        itemList.setAdapter(itemListAdapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(itemList.getContext(), itemListLayoutManager.getOrientation());
        itemList.addItemDecoration(itemDecoration);
    }

    public void createItem(View view) {
        Intent intent = new Intent(this, NewItemActivity.class);
        startActivityForResult(intent, NEW_ITEM_REQUEST);
    }
}
