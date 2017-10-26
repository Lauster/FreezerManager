package de.geek_hub.freezermanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ItemListInstrumentedTest {
    @Before
    @After
    public void clearPreferences() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences prefs = context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        prefs.edit().remove("items").apply();
        prefs.edit().remove("nextNotificationItemId").apply();
    }

    @Test
    public void testConstructor() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);
        assertEquals("New ItemList has to be empty", 0, classUnderTest.length());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullContext() {
        ItemList classUnderTest = new ItemList(null);
    }

    @Test
    public void testAddAndGetItem() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);
        Item testItem = new Item("Ice Cream");
        int position = classUnderTest.addItem(testItem);
        assertTrue("Original item and item from list do not match",
                EqualsBuilder.reflectionEquals(testItem, classUnderTest.getItem(position)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetItemFromUndefinedPosition() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);
        Item testItem = new Item("Ice Cream");
        int position = classUnderTest.addItem(testItem);
        classUnderTest.getItem(position + 1);
    }

    @Test
    public void testLength() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);
        assertEquals(0, classUnderTest.length());

        classUnderTest.addItem(new Item("Ice Cream"));
        assertEquals(1, classUnderTest.length());

        int position = classUnderTest.addItem(new Item("Marshmallow"));
        assertEquals(2, classUnderTest.length());

        classUnderTest.deleteItem(position);
        assertEquals(1, classUnderTest.length());
    }

    @Test
    public void testDeleteItem() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);
        Item testItem = new Item("Ice Cream");
        int position = classUnderTest.addItem(testItem);

        Item deletedItem = classUnderTest.deleteItem(position);
        assertEquals(0, classUnderTest.length());
        assertTrue("Original item and deleted item do not match",
                EqualsBuilder.reflectionEquals(testItem, deletedItem));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteItemFromUndefinedPosition() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);
        classUnderTest.deleteItem(0);
    }

    @Test
    public void testSortListByName() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);

        classUnderTest.addItem(new Item("Marshmallow"));
        classUnderTest.addItem(new Item("ice cream"));
        classUnderTest.addItem(new Item("Donut"));

        classUnderTest.sortList("name");

        boolean isCorrectlySorted = classUnderTest.getItem(0).getName().equals("Donut") &&
                classUnderTest.getItem(1).getName().equals("ice cream") &&
                classUnderTest.getItem(2).getName().equals("Marshmallow");

        assertTrue("ItemList is not sorted by name", isCorrectlySorted);
    }

    @Test
    public void testSortListBySize() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);

        Item testItem = new Item("Marshmallow");
        testItem.setSize(-2.4);
        classUnderTest.addItem(testItem);

        testItem = new Item("Donut");
        testItem.setSize(5.5);
        classUnderTest.addItem(testItem);

        testItem = new Item("ice cream");
        testItem.setSize(0);
        classUnderTest.addItem(testItem);

        classUnderTest.sortList("size");

        boolean isCorrectlySorted = classUnderTest.getItem(0).getName().equals("Donut") &&
                classUnderTest.getItem(1).getName().equals("ice cream") &&
                classUnderTest.getItem(2).getName().equals("Marshmallow");

        assertTrue("ItemList is not sorted by size", isCorrectlySorted);
    }

    @Test
    public void testSortListByFreezeDate() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);

        Item testItem = new Item("Marshmallow");
        testItem.setFreezeDate(new Date(10000));
        classUnderTest.addItem(testItem);

        testItem = new Item("Donut");
        testItem.setFreezeDate(new Date(500));
        classUnderTest.addItem(testItem);

        testItem = new Item("ice cream");
        testItem.setFreezeDate(new Date(1000));
        classUnderTest.addItem(testItem);

        classUnderTest.sortList("freezeDate");

        boolean isCorrectlySorted = classUnderTest.getItem(0).getName().equals("Donut") &&
                classUnderTest.getItem(1).getName().equals("ice cream") &&
                classUnderTest.getItem(2).getName().equals("Marshmallow");

        assertTrue("ItemList is not sorted by freezeDate", isCorrectlySorted);
    }

    @Test
    public void testSortListByExpDate() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);

        Item testItem = new Item("Marshmallow");
        testItem.setExpDate(new Date(10000));
        classUnderTest.addItem(testItem);

        testItem = new Item("Donut");
        testItem.setExpDate(new Date(500));
        classUnderTest.addItem(testItem);

        testItem = new Item("ice cream");
        testItem.setExpDate(new Date(1000));
        classUnderTest.addItem(testItem);

        classUnderTest.sortList("expDate");

        boolean isCorrectlySorted = classUnderTest.getItem(0).getName().equals("Donut") &&
                classUnderTest.getItem(1).getName().equals("ice cream") &&
                classUnderTest.getItem(2).getName().equals("Marshmallow");

        assertTrue("ItemList is not sorted by expDate", isCorrectlySorted);
    }

    /*@Test
    public void testNotifications() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);

        Item testItem = new Item("Ice Cream");
        testItem.setExpDate(new Date(new Date().getTime() + 10000));
        classUnderTest.addItem(testItem);

        testItem = new Item("Donut1");
        long oneMonth = 30L * 24L * 60L * 60L * 1000L;
        testItem.setExpDate(new Date(new Date().getTime() + oneMonth + 5000));
        classUnderTest.addItem(testItem);

        assertTrue("Ice Cream scheduled no notification",
                classUnderTest.getItem(0).notifiedAboutExpire());
        assertTrue("Donut scheduled no notification",
                classUnderTest.getItem(1).notifiedAboutExpire());


        testItem = new Item("Marshmallow");
        testItem.setExpDate(new Date(new Date().getTime() + oneMonth));
        classUnderTest.addItem(testItem);

        assertTrue("Ice Cream scheduled no notification",
                classUnderTest.getItem(0).notifiedAboutExpire());
        assertFalse("Donut scheduled a notification",
                classUnderTest.getItem(1).notifiedAboutExpire());
        assertTrue("Marshmallow scheduled no notification",
                classUnderTest.getItem(2).notifiedAboutExpire());


        classUnderTest.deleteItem(2);

        assertTrue("Ice Cream scheduled no notification",
                classUnderTest.getItem(0).notifiedAboutExpire());
        assertTrue("Donut scheduled no notification",
                classUnderTest.getItem(1).notifiedAboutExpire());
    }*/

    @Test
    public void testLoadItems() {
        Context context = InstrumentationRegistry.getTargetContext();

        String data = "[{\"category\":\"meat\",\"freezeDate\":\"2017-10-05 18:30:15.562\",\"name\":\"Ice Cream\",\"notifiedAboutExpire\":true,\"section\":0,\"size\":-1.0,\"unit\":\"mass\"}]";
        SharedPreferences prefs = context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        prefs.edit().putString("items", data).apply();

        ItemList classUnderTest = new ItemList(context);
        assertEquals("ItemList should have loaded 1 item", 1, classUnderTest.length());
    }

    @Test
    public void testSaveItems() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);
        Item testItem = new Item("Ice Cream");
        testItem.setCategory("meat");
        testItem.setFreezeDate(new Date(1500));
        testItem.setSection(0);
        testItem.setSize(-1.0);
        testItem.setUnit("mass");
        classUnderTest.addItem(testItem);

        String expected = "[{\"category\":\"meat\",\"freezeDate\":\"1970-01-01 01:00:01.500\",\"name\":\"Ice Cream\",\"notifiedAboutExpire\":true,\"section\":0,\"size\":-1.0,\"unit\":\"mass\"}]";

        SharedPreferences prefs = context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        String actual = prefs.getString("items", null);

        assertEquals("ItemLists do not match", expected, actual);
    }

    @Test
    public void testSaveNextNotification() {
        Context context = InstrumentationRegistry.getTargetContext();
        ItemList classUnderTest = new ItemList(context);
        Item testItem = new Item("Ice Cream");
        testItem.setExpDate(new Date(new Date().getTime() + 5000));
        classUnderTest.addItem(testItem);

        SharedPreferences prefs = context.getSharedPreferences("de.geek-hub.freezermanager.data", Context.MODE_PRIVATE);
        int actual = prefs.getInt("nextNotificationItemId", -1);

        assertEquals("ItemId does not match", 0, actual);
    }
}
