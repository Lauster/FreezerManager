package de.geek_hub.freezermanager;

import android.content.Intent;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ItemDetailActivity extends AppCompatActivity {
    private Item item;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        this.item = i.getParcelableExtra("item");
        this.id = i.getIntExtra("id", -1);

        fillWithItemData();
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
                    .setText(unitLabels[Arrays.asList(unitIds).indexOf(this.item.getUnit())] + ":");
            ((TextView) findViewById(R.id.item_detail_size))
                    .setText(String.format("%s %s",
                            new DecimalFormat("0.######").format(this.item.getSize()),
                            units[Arrays.asList(unitIds).indexOf(this.item.getUnit())]));
        }

        java.text.DateFormat dateFormat = DateFormat.getDateFormat(getApplicationContext());
        if (this.item.getFreezeDate() == null) {
            findViewById(R.id.item_detail_freeze_date_label).setVisibility(View.GONE);
            findViewById(R.id.item_detail_freeze_date).setVisibility(View.GONE);
        } else {
            findViewById(R.id.item_detail_freeze_date_label).setVisibility(View.VISIBLE);
            findViewById(R.id.item_detail_freeze_date).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.item_detail_freeze_date)).setText(dateFormat.format(this.item.getFreezeDate()));
        }
        if (this.item.getExpDate() == null) {
            findViewById(R.id.item_detail_exp_date_label).setVisibility(View.GONE);
            findViewById(R.id.item_detail_exp_date).setVisibility(View.GONE);
        } else {
            findViewById(R.id.item_detail_exp_date_label).setVisibility(View.VISIBLE);
            findViewById(R.id.item_detail_exp_date).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.item_detail_exp_date)).setText(dateFormat.format(this.item.getExpDate()));
        }

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
