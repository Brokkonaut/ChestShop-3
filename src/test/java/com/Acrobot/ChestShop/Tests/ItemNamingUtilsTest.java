package com.Acrobot.ChestShop.Tests;

import static org.junit.Assert.assertEquals;

import com.Acrobot.ChestShop.Utils.ItemNamingUtils;
import org.junit.Test;

public class ItemNamingUtilsTest {

    @Test(timeout = 1000)
    public void testNamingCutdown() {
        String sut = "Hello there! Didn't your tests fail?";

        String result = ItemNamingUtils.shortenDisplayName(sut, 15);

        assertEquals("HelTheDidYoTeFa", result);
        assertEquals(15, result.length());
    }

    @Test(timeout = 1000)
    public void testNamingCutdownWhenSingleWordsAreShorter() {
        String sut = "Hi i am your, testededed!";

        String result = ItemNamingUtils.shortenDisplayName(sut, 15);

        assertEquals("HiIAmYour,Teste", result);
        assertEquals(15, result.length());
    }

    @Test(timeout = 1000)
    public void testNamingCutdownOnShortEnough() {
        String sut = "Hi i am tested";

        String result = ItemNamingUtils.shortenDisplayName(sut, 15);

        assertEquals(sut, result);
    }

    @Test(timeout = 1000)
    public void testNamingOnlyRemoveWhitespaces() {
        String sut = "Hi i am all tested";

        String result = ItemNamingUtils.shortenDisplayName(sut, 15);

        assertEquals("HiIAmAllTested", result);
    }

    @Test(timeout = 1000)
    public void testNamingFirstWordLongerThanSecond() {
        String sut = "Regenbogenalphabet Case";

        String result = ItemNamingUtils.shortenDisplayName(sut, 15);

        assertEquals("RegenbogenaCase", result);
        assertEquals(15, result.length());
    }
}
