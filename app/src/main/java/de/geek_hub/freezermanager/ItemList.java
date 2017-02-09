package de.geek_hub.freezermanager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    public Item getItem(int position) {
        return this.itemList.get(position);
    }

    public void addItem(Item item) {
        this.itemList.add(item);

        saveItems();
    }

    public Item deleteItem(int position) {
        Item deletedItem = this.itemList.remove(position);

        saveItems();

        return deletedItem;
    }

    private void loadItems() {
        SharedPreferences prefs = this.context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        Gson g = new Gson();
        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        this.itemList = (ArrayList<Item>) g.fromJson(prefs.getString("items", g.toJson(new ArrayList<Item>())), type);
    }

    private void saveItems() {
        SharedPreferences prefs = this.context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        Gson g = new Gson();

        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        prefs.edit().putString("items", g.toJson(this.itemList, type)).commit();
    }
}
