package de.geek_hub.freezermanager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

class ItemList {
    private Context context;
    private ArrayList<Item> itemList;
    private int nextNotificationItemId;

    ItemList(Context context) {
        this.context = context;
        loadItems();
        loadNextNotification();
    }

    Item getItem(int position) {
        return this.itemList.get(position);
    }

    int length() {
        return itemList.size();
    }

    int addItem(Item item) {
        this.itemList.add(item);

        this.checkNotifications();

        this.saveItems();

        return this.itemList.size() -1;
    }

    Item deleteItem(int position) {
        if (position == this.nextNotificationItemId) {
            this.removeNotification();
        }

        Item deletedItem = this.itemList.remove(position);

        if (position == this.nextNotificationItemId) {
            this.checkNotifications();
        }

        this.saveItems();

        return deletedItem;
    }

    void sortList(String attribute) {
        this.removeNotification();

        switch (attribute) {
            case "name":
            default:
                Collections.sort(this.itemList, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
                break;
            case "size":
                Collections.sort(this.itemList, (o1, o2) -> Double.compare(o2.getSize(), o1.getSize()));
                break;
            case "freezeDate":
                Collections.sort(this.itemList, (o1, o2) -> o1.getFreezeDate().compareTo(o2.getFreezeDate()));
                break;
            case "expDate":
                Collections.sort(this.itemList, (o1, o2) -> o1.getExpDate() == null ? 1 :
                        o2.getExpDate() == null ? -1 :
                        o1.getExpDate().compareTo(o2.getExpDate()));
                break;
        }

        this.checkNotifications();
    }

    private void checkNotifications() {
        int itemId = getNextExpiringItem();

        if (itemId != this.nextNotificationItemId) {
            if (this.nextNotificationItemId != -1) {
                this.itemList.get(this.nextNotificationItemId).setNotifiedAboutExpire(false);
                this.nextNotificationItemId = -1;
                NotificationHandler.deleteNextNotification();
            }

            this.itemList.get(itemId).setNotifiedAboutExpire(true);
            this.nextNotificationItemId = itemId;
            NotificationHandler.setNextNotification(this.context, this.itemList.get(itemId));

            this.saveItems();
            this.saveNextNotification();
        }
    }

    private void removeNotification() {
        if (this.nextNotificationItemId != -1) {
            this.itemList.get(this.nextNotificationItemId).setNotifiedAboutExpire(false);
            this.nextNotificationItemId = -1;
            NotificationHandler.deleteNextNotification();
        }
    }

    private int getNextExpiringItem() {
        Item item;
        Date lowestExpDate = new Date(Long.MAX_VALUE);
        int itemId = -1;
        for (int id = 0; id < this.itemList.size(); id++) {
            item = this.itemList.get(id);
            if ((item.getExpDate() != null && !item.notifiedAboutExpire()) || id == this.nextNotificationItemId) {
                if (item.getExpDate().compareTo(lowestExpDate) < 0) {
                    lowestExpDate = item.getExpDate();
                    itemId = id;
                }
            }
        }
        return itemId;
    }

    private void loadItems() {
        SharedPreferences prefs = this.context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        Gson g = new Gson();
        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        this.itemList = g.fromJson(prefs.getString("items", g.toJson(new ArrayList<Item>())), type);
    }

    private void saveItems() {
        SharedPreferences prefs = this.context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        Gson g = new Gson();

        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        prefs.edit().putString("items", g.toJson(this.itemList, type)).apply();
    }

    private void loadNextNotification() {
        this.nextNotificationItemId = this.context
                .getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE)
                .getInt("nextNotificationItemId", -1);
    }

    private void saveNextNotification() {
        this.context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE)
                .edit().putInt("nextNotificationItemId", this.nextNotificationItemId).apply();
    }
}
