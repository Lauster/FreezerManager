package de.geek_hub.freezermanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity {
    private Item item;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        this.item = i.getParcelableExtra("item");
        this.id = i.getIntExtra("id", -1);

        fillWithItemData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.item_detail_edit);

        if (menuItem != null) {
            Drawable normalDrawable = menuItem.getIcon();
            Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, android.R.color.white));

            menuItem.setIcon(wrapDrawable);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.item_detail_edit:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", "edit");
                returnIntent.putExtra("id", this.id);
                setResult(RESULT_OK, returnIntent);
                super.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillWithItemData() {
        setTitle(this.item.getName());

        if (this.item.getSize() == -1) {
            findViewById(R.id.item_detail_size_label).setVisibility(View.GONE);
            findViewById(R.id.item_detail_size).setVisibility(View.GONE);
        } else {
            findViewById(R.id.item_detail_size_label).setVisibility(View.VISIBLE);
            findViewById(R.id.item_detail_size).setVisibility(View.VISIBLE);

            String[] unitIds = getResources().getStringArray(R.array.unit_ids);
            String[] units = getResources().getStringArray(R.array.units);
            String[] unitLabels = getResources().getStringArray(R.array.unit_labels);

            ((TextView) findViewById(R.id.item_detail_size_label))
                    .setText(String.format(getResources().getString(R.string.item_detail_size),
                            unitLabels[Arrays.asList(unitIds).indexOf(this.item.getUnit())]));
            ((TextView) findViewById(R.id.item_detail_size))
                    .setText(String.format("%s %s",
                            new DecimalFormat("##########.##########").format(this.item.getSize()),
                            units[Arrays.asList(unitIds).indexOf(this.item.getUnit())]));
        }

        java.text.DateFormat dateFormat = DateFormat.getDateFormat(getApplicationContext());
        ((TextView) findViewById(R.id.item_detail_freeze_date)).setText(dateFormat.format(this.item.getFreezeDate()));

        if (this.item.getExpDate() == null) {
            findViewById(R.id.item_detail_exp_date_label).setVisibility(View.GONE);
            findViewById(R.id.item_detail_exp_date).setVisibility(View.GONE);
        } else {
            findViewById(R.id.item_detail_exp_date_label).setVisibility(View.VISIBLE);
            findViewById(R.id.item_detail_exp_date).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.item_detail_exp_date)).setText(dateFormat.format(this.item.getExpDate()));
        }

        ((TextView) findViewById(R.id.item_detail_section))
                .setText(String.format(Locale.getDefault(), "%1$d", this.item.getSection() + 1));

        String[] categoryIds = getResources().getStringArray(R.array.category_ids);
        String[] categories = getResources().getStringArray(R.array.categories);
        ((TextView) findViewById(R.id.item_detail_category))
                .setText(categories[Arrays.asList(categoryIds).indexOf(this.item.getCategory())]);
    }

    public void defrostItem (View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("action", "defrost");
        returnIntent.putExtra("id", this.id);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }
}
