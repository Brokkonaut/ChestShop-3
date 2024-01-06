package com.Acrobot.ChestShop.Tests;

import com.Acrobot.ChestShop.Listeners.PreShopCreation.ItemChecker;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ItemCheckerTest {

    @Test
    public void testNamingCutdown() {
        String sut = "Hello there! Didn't your tests fail?";

        String result = ItemChecker.shortenDisplayName(sut, 15);

        assertEquals("HelTheDidYoTeFa", result);
    }

    @Test
    public void testNamingCutdownWhenSingleWordsAreShorter() {
        String sut = "Hi i am your, testededed!";

        String result = ItemChecker.shortenDisplayName(sut, 15);

        assertEquals("HiIAmYour,Teste", result);
    }

    @Test
    public void testNamingCutdownOnShortEnough() {
        String sut = "Hi i am tested";

        String result = ItemChecker.shortenDisplayName(sut, 15);

        assertEquals(sut, result);
    }

    @Test
    public void testNamingOnlyRemoveWhitespaces() {
        String sut = "Hi i am all tested";

        String result = ItemChecker.shortenDisplayName(sut, 15);

        assertEquals("HiIAmAllTested", result);
    }
}
