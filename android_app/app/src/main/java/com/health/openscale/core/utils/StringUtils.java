package com.health.openscale.core.utils;

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
    private static final String WHITESPACE_CHARS = " \f\t\n\r\u00A0\u2007\u202F\u000B\u001C\u001D\u001E\u001F";

    public static final String EMPTY_STRING = "";

    public static boolean isNullOrWhitespace(final String value) {
        //TODO: replace .chars() as requires at least sdk level 24
        return null == value || value.isEmpty() || value.chars().allMatch(Character::isWhitespace);
    }

    public static String[] splitByWhitespace(final String value) {
        return value.trim().split(String.format("[%s]+", WHITESPACE_CHARS));
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
//        return new String(new char[count]).replace('\0', value);
    }
}