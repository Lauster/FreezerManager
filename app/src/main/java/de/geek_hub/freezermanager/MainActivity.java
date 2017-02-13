package de.geek_hub.freezermanager;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/* TODO:
- implement notifications
- (add additional settings)
- (add grouping: none, sections, categories)
- (add photos)
- (sort desc)
 */

public class MainActivity extends AppCompatActivity implements SortDialogFragment.SortDialogListener {
    private ItemList frozenItems;
    private RecyclerView itemList;
    private RecyclerView.Adapter itemListAdapter;

    static final int ITEM_CREATE_REQUEST = 10;
    static final int ITEM_EDIT_REQUEST = 11;
    static final int ITEM_DETAIL_REQUEST = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.frozenItems = new ItemList(this);
        this.itemList = (RecyclerView) findViewById(R.id.item_list);

        SharedPreferences prefs = getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        this.frozenItems.sortList(prefs.getString("sort", "name"));

        showItems();

        ItemClickSupport.addTo(itemList).setOnItemClickListener((recyclerView, position, v) -> {
            Intent itemDetail = new Intent(getApplicationContext(), ItemDetailActivity.class);
            itemDetail.putExtra("item", frozenItems.getItem(position));
            itemDetail.putExtra("id", position);
            startActivityForResult(itemDetail, ITEM_DETAIL_REQUEST);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        paintIconWhite(menu.findItem(R.id.action_sort));
        //paintIconWhite(menu.findItem(R.id.action_filter));

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
                return true;
            case R.id.action_sort:
                SortDialogFragment sortDialog = new SortDialogFragment();
                sortDialog.show(getFragmentManager(), "sort");
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        SharedPreferences prefs = getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        switch (requestCode) {
            case ITEM_CREATE_REQUEST:
                Item newItem = data.getParcelableExtra("newItem");

                this.frozenItems.addItem(newItem);

                this.frozenItems.sortList(prefs.getString("sort", "name"));
                this.itemListAdapter.notifyDataSetChanged();
                break;
            case ITEM_EDIT_REQUEST:
                Item editedItem = data.getParcelableExtra("item");
                int id = data.getIntExtra("id", -1);

                this.frozenItems.deleteItem(id);
                int newId = this.frozenItems.addItem(editedItem);

                this.frozenItems.sortList(prefs.getString("sort", "name"));
                this.itemListAdapter.notifyDataSetChanged();

                Intent itemDetail = new Intent(getApplicationContext(), ItemDetailActivity.class);
                itemDetail.putExtra("item", frozenItems.getItem(newId));
                itemDetail.putExtra("id", newId);
                startActivityForResult(itemDetail, ITEM_DETAIL_REQUEST);
                break;
            case ITEM_DETAIL_REQUEST:
                switch (data.getStringExtra("action")) {
                    case "defrost":
                        final Item deletedItem = this.frozenItems.deleteItem(data.getIntExtra("id", -1));

                        this.frozenItems.sortList(prefs.getString("sort", "name"));
                        this.itemListAdapter.notifyDataSetChanged();

                        Snackbar.make(findViewById(R.id.main_activity_inner_coordinator_layout),
                                    deletedItem.getName() + getResources().getString(R.string.snackbar_defrost),
                                    Snackbar.LENGTH_LONG)
                                .setAction(R.string.snackbar_defrost_undo, view -> {
                                    frozenItems.addItem(deletedItem);
                                    itemListAdapter.notifyDataSetChanged();
                                }).setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                                .show();
                        break;
                    case "edit":
                        Intent itemEdit = new Intent(getApplicationContext(), ItemEditActivity.class);
                        itemEdit.putExtra("action", "edit");
                        itemEdit.putExtra("item", frozenItems.getItem(data.getIntExtra("id", -1)));
                        itemEdit.putExtra("id", data.getIntExtra("id", -1));
                        startActivityForResult(itemEdit, ITEM_EDIT_REQUEST);
                        break;
                }
                break;
        }
    }

    public void showItems() {
        this.itemList.setHasFixedSize(true);

        LinearLayoutManager itemListLayoutManager = new LinearLayoutManager(this);
        this.itemList.setLayoutManager(itemListLayoutManager);

        this.itemListAdapter = new ItemListAdapter(this.frozenItems, this);

        /* //This is the code to provide a sectioned list
        List<SimpleSectionedRecyclerViewAdapter.Section> sections = new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        //Sections
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Section 1"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(5,"Section 2"));

        //Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(this,R.layout.section,R.id.section_text, this.itemListAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        itemList.setAdapter(mSectionedAdapter);*/
        itemList.setAdapter(this.itemListAdapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(itemList.getContext(), itemListLayoutManager.getOrientation());
        itemList.addItemDecoration(itemDecoration);
    }

    public void createItem(View view) {
        Intent intent = new Intent(this, ItemEditActivity.class);
        intent.putExtra("action", "create");
        startActivityForResult(intent, ITEM_CREATE_REQUEST);
    }

    @Override
    public void onSortSelect(DialogFragment dialog, int position) {
        String sort = "name";
        switch (position) { // TODO: save sorting preference
            case 0:
                sort = "name";
                break;
            case 1:
                sort = "size";
                break;
            case 2:
                sort = "freezeDate";
                break;
            case 3:
                sort = "expDate";
                break;
        }
        this.frozenItems.sortList(sort);

        SharedPreferences prefs = getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        prefs.edit().putString("sort", sort).apply();

        this.itemListAdapter.notifyDataSetChanged();
    }
}
