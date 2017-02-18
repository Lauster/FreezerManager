package de.geek_hub.freezermanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class ItemEditActivity extends AppCompatActivity {
    private String action;
    private Item item;
    private Calendar expDate = Calendar.getInstance();
    private Intent returnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        EditText newItemSize = (EditText) findViewById(R.id.item_edit_size);

        final char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        newItemSize.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator));
        newItemSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                boolean separatorDetected = false;
                if (input.length() > 10) {
                    s.delete(10, input.length());
                }
                for (int i = 0; i < input.length(); i++) {
                    if ((separator + ",.").contains(String.valueOf(input.charAt(i)))) {
                        if (!separatorDetected) {
                            separatorDetected = true;
                        } else {
                            s.delete(i, i + 1);
                            return;
                        }
                    }
                }
            }
        });

        Spinner unitSpinner = (Spinner) findViewById(R.id.item_edit_unit);
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this,
                R.array.units, R.layout.spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] unitLabels = getResources().getStringArray(R.array.unit_labels);
                ((EditText) findViewById(R.id.item_edit_size)).setHint(unitLabels[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayList<String> sections = new ArrayList<>();
        Integer layoutSections = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("layout_sections", "5"));
        for (int i = 1; i <= layoutSections; i++) {
            sections.add(String.format(getResources().getString(R.string.item_edit_section_label), i));
        }

        Spinner sectionSpinner = (Spinner) findViewById(R.id.item_edit_section);
        if (layoutSections > 1) {
            ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, sections);
            sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sectionSpinner.setAdapter(sectionAdapter);
        } else {
            sectionSpinner.setVisibility(View.GONE);
        }

        Spinner categorySpinner = (Spinner) findViewById(R.id.item_edit_category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
            R.array.categories, R.layout.spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        Intent i = getIntent();
        this.action = i.getStringExtra("action");
        this.returnIntent = new Intent();
        if (this.action.equals("edit")) {
            setTitle(getResources().getString(R.string.item_edit_title));
            this.item = i.getParcelableExtra("item");
            int id = i.getIntExtra("id", -1);
            fillFieldsWithData();

            this.returnIntent.putExtra("id", id);
            this.returnIntent.putExtra("action", "none");
            setResult(RESULT_OK, this.returnIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_edit, menu);

        MenuItem menuItem = menu.findItem(R.id.item_edit_done);

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
            case R.id.item_edit_done:
                saveItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillFieldsWithData() {
        EditText name = (EditText) findViewById(R.id.item_edit_name);
        name.setText(this.item.getName());
        name.setSelection(this.item.getName().length());

        if (this.item.getSize() != -1) {
            ((EditText) findViewById(R.id.item_edit_size))
                    .setText(new DecimalFormat("##########.##########").format(this.item.getSize()));

            String[] unitIds = getResources().getStringArray(R.array.unit_ids);
            ((Spinner) findViewById(R.id.item_edit_unit))
                    .setSelection(Arrays.asList(unitIds).indexOf(this.item.getUnit()));
        }

        if (this.item.getExpDate() != null) {
            this.expDate.setTime(this.item.getExpDate());

            java.text.DateFormat dateFormat = DateFormat.getDateFormat(getApplicationContext());
            ((EditText) findViewById(R.id.item_edit_exp_date)).setText(dateFormat.format(this.item.getExpDate()));
        }

        int sections = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("layout_sections", "1"));
        if (this.item.getSection() + 1 > sections) {
            ((Spinner) findViewById(R.id.item_edit_section)).setSelection(1);
        } else {
            ((Spinner) findViewById(R.id.item_edit_section)).setSelection(this.item.getSection());
        }

        String[] categoryIds = getResources().getStringArray(R.array.category_ids);
        ((Spinner) findViewById(R.id.item_edit_category))
                .setSelection(Arrays.asList(categoryIds).indexOf(this.item.getCategory()));
    }

    public void openDatePicker(View view) {
        new DatePickerDialog(this,
                date,
                expDate.get(Calendar.YEAR),
                expDate.get(Calendar.MONTH),
                expDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
        expDate.set(year, month, dayOfMonth);

        java.text.DateFormat dateFormat = DateFormat.getDateFormat(getApplicationContext());
        ((EditText) findViewById(R.id.item_edit_exp_date)).setText(dateFormat.format(expDate.getTime()));
    };

    private void saveItem() {
        String name = ((EditText) findViewById(R.id.item_edit_name)).getText().toString().trim();

        if (name.isEmpty()) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
            Snackbar.make(findViewById(R.id.activity_item_edit), R.string.item_edit_insert_name, Snackbar.LENGTH_SHORT).show();
        } else {
            Item newItem = new Item(name);

            String sizeStr = ((EditText) findViewById(R.id.item_edit_size)).getText().toString();
            if (sizeStr.isEmpty()) {
                newItem.setSize(-1);
            } else {
                newItem.setSize(Double.parseDouble(sizeStr.replace(',', '.')));
            }

            String[] units = getResources().getStringArray(R.array.unit_ids);
            newItem.setUnit(units[((Spinner) findViewById(R.id.item_edit_unit)).getSelectedItemPosition()]);

            if (!((EditText) findViewById(R.id.item_edit_exp_date)).getText().toString().isEmpty()) {
                newItem.setExpDate(expDate.getTime());
            }

            int section = ((Spinner) findViewById(R.id.item_edit_section)).getSelectedItemPosition();
            if (section == -1) {
                newItem.setSection(0);
            } else {
                newItem.setSection(section);
            }

            String[] categories = getResources().getStringArray(R.array.category_ids);
            newItem.setCategory(categories[((Spinner) findViewById(R.id.item_edit_category)).getSelectedItemPosition()]);

            if (this.action.equals("edit")) {
                newItem.setFreezeDate(this.item.getFreezeDate());
                this.returnIntent.putExtra("item", newItem);
                this.returnIntent.putExtra("action", "edit");
            } else {
                this.returnIntent.putExtra("newItem", newItem);
            }
            setResult(RESULT_OK, this.returnIntent);
            super.finish();
        }
    }
}
