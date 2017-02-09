package de.geek_hub.freezermanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class NewItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        Spinner unitSpinner = (Spinner) findViewById(R.id.new_item_unit);
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this,
                R.array.units, R.layout.spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] unitLabels = getResources().getStringArray(R.array.unit_labels);
                ((EditText) findViewById(R.id.new_item_size)).setHint(unitLabels[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner categorySpinner = (Spinner) findViewById(R.id.new_item_category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
            R.array.categories, R.layout.spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);

        MenuItem menuItem = menu.findItem(R.id.new_item_done);

        if (menuItem != null) {
            Drawable normalDrawable = menuItem.getIcon();
            Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, android.R.color.white));

            menuItem.setIcon(wrapDrawable);
        }

        return true;
    }

    public void saveItem (MenuItem item) {
        String name = ((EditText) findViewById(R.id.new_item_name)).getText().toString().trim();

        if (name.isEmpty()) {
            Snackbar.make(findViewById(R.id.activity_new_item), R.string.insert_name, Snackbar.LENGTH_SHORT).show();
        } else {
            Item newItem = new Item(name);

            String sizeStr = ((EditText) findViewById(R.id.new_item_size)).getText().toString().trim();
            if (sizeStr.isEmpty()) {
                newItem.setSize(-1);
            } else {
                newItem.setSize(Float.parseFloat(sizeStr)); // TODO: localisation
            }

            String[] units = getResources().getStringArray(R.array.unit_ids);
            newItem.setUnit(units[((Spinner) findViewById(R.id.new_item_unit)).getSelectedItemPosition()]);

            String[] categories = getResources().getStringArray(R.array.category_ids);
            newItem.setCategory(categories[((Spinner) findViewById(R.id.new_item_category)).getSelectedItemPosition()]);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("newItem", newItem);
            setResult(RESULT_OK, returnIntent);
            super.finish();
        }
    }
}
