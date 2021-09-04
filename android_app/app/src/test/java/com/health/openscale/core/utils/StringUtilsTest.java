package com.health.openscale.core.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void isNullOrWhitespace() {
        String stringTest = new String("");
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String("\n");
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String("\r");
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String("\t");
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String(" ");
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String(" \t\r\n");
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = null;
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String("\n\t\r    \t");
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String(" \f\t\n\r\u00A0\u2007\u202F\u000B\u001C\u001D\u001E\u001F");
        assertTrue(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String(" \f\t\n\r\u00A0\u2007\u202F\u000B\u001C\u001D\u001E\u001Fz");
        assertFalse(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String(" \f\t\n\r\u00A0M\u2007\u202F\u000B\u001C\u001D\u001E\u001F");
        assertFalse(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String("6 \f\t\n\r\u00A0\u2007\u202F\u000B\u001C\u001D\u001E\u001F");
        assertFalse(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String(" \f\t\n\r\u00A0s46\u2007\u202Fdafad\u000B\u001C\u001D\u001E\u001F");
        assertFalse(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String("\n\t\r  d  \t");
        assertFalse(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String("r\n\t\r    \t");
        assertFalse(StringUtils.isNullOrWhitespace(stringTest));

        stringTest = new String("\n\t\r    \t1");
        assertFalse(StringUtils.isNullOrWhitespace(stringTest));
    }

    @Test
    public void splitByWhitespace() {
        String stringTest = new String("\n\t\r    \t1 2\t\t\n3");
        String[] stringsSplitted = StringUtils.splitByWhitespace(stringTest);
        String[] expected = new String[]{"1", "2", "3"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String("\n\t\r    \t1 2\t\t\n3 \t\n\r");
        stringsSplitted = StringUtils.splitByWhitespace(stringTest);
        expected = new String[]{"1", "2", "3"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String("1 2\t\t\n3 \t\n\r");
        stringsSplitted = StringUtils.splitByWhitespace(stringTest);
        expected = new String[]{"1", "2", "3"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String(" \f\t\n\r\u00A0\u2007\u202F\u000B\u001C\u001D\u001E\u001F1 \f\t\n\r\u00A0\u2007\u202F\u000B\u001C\u001D\u001E\u001F2 \f\t\n\r\u00A0\u2007\u202F\u000B\u001C\u001D\u001E\u001F");
        stringsSplitted = StringUtils.splitByWhitespace(stringTest);
        expected = new String[]{"1", "2"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String("adgg");
        stringsSplitted = StringUtils.splitByWhitespace(stringTest);
        expected = new String[]{"adgg"};
        assertEquals(expected.length, stringsSplitted.length);
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String(" \f\t\n\r\u00A0\u2007\u202F\u000B\u001C\u001D\u001E\u001F");
        stringsSplitted = StringUtils.splitByWhitespace(stringTest);
        expected = null;
        assertArrayEquals(expected, stringsSplitted);

        stringTest = null;
        stringsSplitted = StringUtils.splitByWhitespace(stringTest);
        expected = null;
        assertArrayEquals(expected, stringsSplitted);

        stringTest = new String("\t\t\n \t\n\r");
        stringsSplitted = StringUtils.splitByWhitespace(stringTest);
        expected = null;
        assertArrayEquals(expected, stringsSplitted);
    }

    @Test
    public void generateStringWithRepeatingChar() {
        String testString = StringUtils.generateStringWithRepeatingChar(-1);
        String expected = "";
        assertEquals(expected, testString);

        testString = StringUtils.generateStringWithRepeatingChar(0);
        expected = "";
        assertEquals(expected, testString);

        testString = StringUtils.generateStringWithRepeatingChar(6);
        expected = "      ";
        assertEquals(expected, testString);

        testString = StringUtils.generateStringWithRepeatingChar(4, 'c');
        expected = "cccc";
        assertEquals(expected, testString);
    }
}