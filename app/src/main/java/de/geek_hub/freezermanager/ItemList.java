package de.geek_hub.freezermanager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ItemList {
    private Context context;
    private ArrayList<Item> itemList;

    public ItemList(Context context) {
        this.context = context;
        loadItems();
    }

    public ArrayList getAllItems() {
        return this.itemList;
    }

    public void addItem(String name) {
        this.itemList.add(new Item(name));

        saveItems();
    }

    private void loadItems() {
        SharedPreferences prefs = this.context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        Gson g = new Gson();

        this.itemList = (ArrayList<Item>) g.fromJson(prefs.getString("items", g.toJson(new ArrayList<String>())), ArrayList.class);
    }

    private void saveItems() {
        SharedPreferences prefs = this.context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        Gson g = new Gson();

        prefs.edit().putString("items", g.toJson(this.itemList)).commit();
    }
}
