package de.geek_hub.freezermanager;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ItemInstrumentedTest {

    @Test
    public void testParcelEqual() {
        Item classUnderTest = new Item("Ice Cream");

        Parcel parcel = Parcel.obtain();
        classUnderTest.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Item createdFromParcel = Item.CREATOR.createFromParcel(parcel);

        assertTrue("Original item and item from parcel do not match",
                EqualsBuilder.reflectionEquals(classUnderTest, createdFromParcel));
    }

    @Test
    public void testParcelNotEqual() {
        Item classUnderTest = new Item("Ice Cream");

        Parcel parcel = Parcel.obtain();
        classUnderTest.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Item createdFromParcel = Item.CREATOR.createFromParcel(parcel);
        Item differentItem = new Item("Marshmallow");

        assertFalse("Original item and item from parcel do match",
                EqualsBuilder.reflectionEquals(differentItem, createdFromParcel));
    }
}
