package com.Acrobot.ChestShop.Tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.Acrobot.ChestShop.Listeners.PreShopCreation.ItemChecker;

public class ItemCheckerTest {

    @Test
    public void testNamingCutdown() {
        String sut = "Hello there! Didn't your daughter dance?";

        String result = ItemChecker.shortenDisplayName(sut, 15);

        assertEquals("HelTheDidYoDaDa", result);
    }

    @Test
    public void testNamingShorting() {
        String sut = "Netherite Chestplate";

        String result = ItemChecker.shortenDisplayName(sut, 12);

        assertEquals("Nether Chest", result);
    }

    @Test
    public void testNamingCutdownWhenSingleWordsAreShorter() {
        String sut = "Hi i am yours daughter!";

        String result = ItemChecker.shortenDisplayName(sut, 15);

        assertEquals("HiIAmYoursDaugh", result);
    }

    @Test
    public void testNamingCutdownOnShortEnough() {
        String sut = "Hi i am yours!";

        String result = ItemChecker.shortenDisplayName(sut, 15);

        assertEquals(sut, result);
    }

    @Test
    public void testNamingOnlyRemoveWhitespaces() {
        String sut = "Hi i am all yours";

        String result = ItemChecker.shortenDisplayName(sut, 15);

        assertEquals("HiIAmAllYours", result);
    }
}
