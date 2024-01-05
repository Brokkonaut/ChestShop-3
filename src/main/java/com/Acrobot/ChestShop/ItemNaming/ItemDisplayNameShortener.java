package com.Acrobot.ChestShop.ItemNaming;

public interface ItemDisplayNameShortener {

    /**
     * Shorten the given string, however that may be.
     *
     * @param string
     *            the String to be shortened.
     * @param length
     *            the intended length of the new string
     * @return shortened string. When possible its shortened to the given length, but it may
     *         be longer if the {@link ItemDisplayNameShortener} implementations couldn't
     *         shorten the string to the defined length.
     */
    String shorten(String string, int length);
}
