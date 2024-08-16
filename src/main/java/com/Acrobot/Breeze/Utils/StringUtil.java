package com.Acrobot.Breeze.Utils;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

/**
 * @author Acrobot
 */
public class StringUtil {
    public static String capitalizeFirstLetter(String string, char separator) {
        return capitalizeFirstLetter(string, separator, true);
    }

    /**
     * Capitalizes every first letter of a word
     *
     * @param string
     *            String to reformat
     * @param separator
     *            Word separator
     * @return Reformatted string
     */
    public static String capitalizeFirstLetter(String string, char separator, boolean lowerCaseOther) {
        if (string == null || string.length() == 0) {
            return string;
        }
        int length = string.length();
        StringBuilder builder = new StringBuilder(length);
        boolean capitalizeNext = true;
        for (int i = 0; i < length; i++) {
            char ch = string.charAt(i);
            if (ch == separator) {
                builder.append(' ');
                capitalizeNext = true;
            } else if (capitalizeNext) {
                builder.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                if (lowerCaseOther) {
                    builder.append(Character.toLowerCase(ch));
                } else {
                    builder.append(ch);
                }
            }
        }
        return builder.toString();
    }

    /**
     * Removes all whitespace characters from the given string. Also capitalizes every first character of a word.
     *
     * @param string
     *            The string to remove whitespace characters from.
     * @return The string with whitespace characters removed.
     */
    public static String stripWhitespaces(String string) {
        // String result = StringUtil.capitalizeFirstLetter(string, ' ', false);
        return string.replace(" ", "");
    }

    /**
     * Capitalizes every first letter of a word
     *
     * @param string
     *            String to reformat
     * @return Reformatted string
     * @see com.Acrobot.Breeze.Utils.StringUtil#capitalizeFirstLetter(String, char)
     */
    public static String capitalizeFirstLetter(String string) {
        return capitalizeFirstLetter(string, ' ');
    }

    /**
     * Joins a String array
     *
     * @param array
     *            array to join
     * @return Joined array
     */
    public static String joinArray(String[] array) {
        return Joiner.on(' ').join(array);
    }

    /**
     * Joins an iterable
     *
     * @param array
     *            Iterable
     * @return Joined iterable
     */
    public static String joinArray(Iterable<?> array) {
        return Joiner.on(' ').join(array);
    }

    /**
     * Strips colour codes from a string
     *
     * @param string
     *            String to strip
     * @return Stripped string
     */
    public static String stripColourCodes(String string) {
        return ChatColor.stripColor(string);
    }

    /**
     * Stips colour codes from an array of strings
     *
     * @param strings
     *            Strings to strip the codes from
     * @return Stripped strings
     */
    public static String[] stripColourCodes(String[] strings) {
        List<String> output = new ArrayList<String>();

        for (String string : strings) {
            output.add(stripColourCodes(string));
        }

        return output.toArray(new String[output.size()]);
    }
}
