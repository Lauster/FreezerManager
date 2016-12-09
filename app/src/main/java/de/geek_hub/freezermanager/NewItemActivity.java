package de.geek_hub.freezermanager;

import android.content.SyncStatusObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class NewItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        Spinner spinner = (Spinner) findViewById(R.id.categories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.categories, R.layout.categories_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
}
