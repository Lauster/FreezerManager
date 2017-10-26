package de.geek_hub.freezermanager;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemTest {
    @Test
    public void testConstructorWithName() {
        String expected = "Ice Cream";
        Item classUnderTest = new Item(expected);
        assertEquals("Name does not match", expected, classUnderTest.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithoutName() {
        Item classUnderTest = new Item(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() {
        Item classUnderTest = new Item("");
    }

    @Test
    public void testConstructorNotified() {
        Item classUnderTest = new Item("Ice Cream");
        Date date = new Date();
        assertTrue("Notified should be true on a new item", classUnderTest.notifiedAboutExpire());
    }

    @Test
    public void testSetExpDate() {
        Item classUnderTest = new Item("Ice Cream");
        Date date = new Date();
        classUnderTest.setExpDate(date);
        assertFalse("Notified should be false after setting a expDate", classUnderTest.notifiedAboutExpire());
    }
}