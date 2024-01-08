package com.Acrobot.ChestShop.ItemNaming;

import com.Acrobot.Breeze.Utils.StringUtil;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class PriorityItemDisplayNameShortener implements ItemDisplayNameShortener {

    private final Map<Integer, StringShortenerPairHolder> pairHolderMap;

    public PriorityItemDisplayNameShortener() {
        pairHolderMap = new TreeMap<>();
    }

    /**
     * Adds a mapping from a 'from' string to a 'to' string with a specified priority.
     * If the priority already exists in the map, the mapping is added to the existing priority level.
     * The lowest priority will be mapped first
     * 
     * @param priority
     *            the priority level of the mapping
     * @param from
     *            the string to be replaced
     * @param to
     *            the replacement string
     */
    public void addMapping(Integer priority, String from, String to) {

        StringShortenerPairHolder pairHolder;
        if (!pairHolderMap.containsKey(priority)) {
            pairHolder = new StringShortenerPairHolder();
            pairHolderMap.put(priority, pairHolder);
        } else
            pairHolder = pairHolderMap.get(priority);

        pairHolder.addShortenerPair(from.toLowerCase(), to.toLowerCase());
    }

    @Override
    public String shorten(String string, int length) {

        if (string.length() <= length)
            return string;

        String result = string;
        String strippedResult = StringUtil.stripWhitespaces(result);
        if (strippedResult.length() <= length)
            return strippedResult;

        for (Map.Entry<Integer, StringShortenerPairHolder> integerStringShortenerPairHolderEntry : pairHolderMap.entrySet()) {
            StringShortenerPairHolder value = integerStringShortenerPairHolderEntry.getValue();
            result = value.apply(result);
            if (result.length() <= length)
                return StringUtil.capitalizeFirstLetter(result).strip();

            strippedResult = StringUtil.stripWhitespaces(result);
            if (strippedResult.length() <= length)
                return strippedResult;
        }

        return StringUtil.capitalizeFirstLetter(result).strip();
    }

    private static class StringShortenerPairHolder {

        private final Set<StringShortenerPair> shortenerPairs;

        public StringShortenerPairHolder() {
            this.shortenerPairs = new TreeSet<>(StringShortenerPair::compareTo);
        }

        public void addShortenerPair(String from, String to) {
            shortenerPairs.add(new StringShortenerPair(from, to));
        }

        public String apply(String string) {

            String result = string;
            for (StringShortenerPair pair : shortenerPairs) {
                result = pair.apply(result);
            }
            return result;
        }
    }

    private record StringShortenerPair(String from, String to) implements Comparable<StringShortenerPair> {
        public String apply(String string) {
            return string.replaceAll("(?<=( |^))" + Pattern.quote(from) + "(?=( |$))", to);
        }

        @Override
        public int compareTo(StringShortenerPair o) {
            if (equals(o))
                return 0;

            if (o.to.contains(from))
                return -1;
            return 1;
        }
    }
}
