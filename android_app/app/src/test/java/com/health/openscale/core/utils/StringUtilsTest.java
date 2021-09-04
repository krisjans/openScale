package com.health.openscale.core.utils;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void isNullOrWhitespace() {
        String stringTest = new String("");
        assertTrue(StringUtils.isAnyBlank(stringTest));

        stringTest = new String("\n");
        assertTrue(StringUtils.isAnyBlank(stringTest));

        stringTest = new String("\r");
        assertTrue(StringUtils.isAnyBlank(stringTest));

        stringTest = new String("\t");
        assertTrue(StringUtils.isAnyBlank(stringTest));

        stringTest = new String(" ");
        assertTrue(StringUtils.isAnyBlank(stringTest));

        stringTest = new String(" \t\r\n");
        assertTrue(StringUtils.isAnyBlank(stringTest));

        stringTest = null;
        assertTrue(StringUtils.isAnyBlank(stringTest));

        stringTest = new String("\n\t\r    \t");
        assertTrue(StringUtils.isAnyBlank(stringTest));

        stringTest = new String("\n\t\r  d  \t");
        assertFalse(StringUtils.isAnyBlank(stringTest));

        stringTest = new String("r\n\t\r    \t");
        assertFalse(StringUtils.isAnyBlank(stringTest));

        stringTest = new String("\n\t\r    \t1");
        assertFalse(StringUtils.isAnyBlank(stringTest));
    }

    @Test
    public void split() {
        String stringTest = new String("\n\t\r    \t1 2\t\t\n3");
        String[] stringsSplitted = StringUtils.split(stringTest);
        String[] expected = new String[]{"1", "2", "3"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String("\n\t\r    \t1 2\t\t\n3 \t\n\r");
        stringsSplitted = StringUtils.split(stringTest);
        expected = new String[]{"1", "2", "3"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String("1 2\t\t\n3 \t\n\r");
        stringsSplitted = StringUtils.split(stringTest);
        expected = new String[]{"1", "2", "3"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String("adgg");
        stringsSplitted = StringUtils.split(stringTest);
        expected = new String[]{"adgg"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = null;
        stringsSplitted = StringUtils.split(stringTest);
        expected = null;
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String("\t\t\n \t\n\r");
        stringsSplitted = StringUtils.split(stringTest);
        assertEquals(0, stringsSplitted.length);
    }

    @Test
    public void repeat() {
        String testString = StringUtils.repeat(' ',-1);
        String expected = "";
        assertEquals(expected, testString);

        testString = StringUtils.repeat(' ',0);
        expected = "";
        assertEquals(expected, testString);

        testString = StringUtils.repeat(' ',6);
        expected = "      ";
        assertEquals(expected, testString);

        testString = StringUtils.repeat('c',4);
        expected = "cccc";
        assertEquals(expected, testString);
    }
}