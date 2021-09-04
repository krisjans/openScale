package com.health.openscale.core.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/***************************************************************************
 * Copyright (C) 2021  Diaconu Cosmin
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>
 ***************************************************************************/

public class StringUtils {
    private static final Set<Character> SET_OF_WHITESPACE_CHARS = new HashSet<>(Arrays.asList
            (' ', '\f', '\t', '\n', '\r', '\u00A0', '\u2007', '\u202F', '\u000B', '\u001C', '\u001D', '\u001E', '\u001F'));

    public static final String EMPTY_STRING = "";

    public static boolean isNullOrWhitespace(final String value) {
        if (null == value || value.isEmpty())
            return true;

        for (int i = 0; i < value.length(); i++) {
            if (!SET_OF_WHITESPACE_CHARS.contains(value.charAt(i)))
                return false;
        }

        return true;
    }

    public static String[] splitByWhitespace(final String value) {
        if (null == value)
            return null;

        Vector<String> words = new Vector<>();

        int startWord =0;

        while (startWord < value.length()) {
            startWord = getNextNonWhitespaceIndex(value, startWord);
            int endWord = getNextWhitespaceIndex(value, startWord);

            if(startWord < endWord)
                words.add(value.substring(startWord, endWord));

            startWord = endWord;
        }

        return words.isEmpty()
                ? null
                : words.toArray(new String[0]);
    }

    public static String generateStringWithRepeatingChar(final int count) {
        return generateStringWithRepeatingChar(count, ' ');
    }

    public static String generateStringWithRepeatingChar(final int count, final char value) {
        if (count < 1)
            return EMPTY_STRING;

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < count; i++)
            stringBuilder.append(value);

        return stringBuilder.toString();
    }

    private static int getNextWhitespaceIndex(String value, int startIndex) {
        while (startIndex < value.length() && !SET_OF_WHITESPACE_CHARS.contains(value.charAt(startIndex))) {
            startIndex++;
        }
        return startIndex;
    }

    private static int getNextNonWhitespaceIndex(String value, int startIndex) {
        while (startIndex < value.length() && SET_OF_WHITESPACE_CHARS.contains(value.charAt(startIndex))) {
            startIndex++;
        }
        return startIndex;
    }
}