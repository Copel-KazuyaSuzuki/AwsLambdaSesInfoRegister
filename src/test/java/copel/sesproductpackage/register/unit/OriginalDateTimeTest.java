package copel.sesproductpackage.register.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OriginalDateTimeTest {

    // コンストラクタのテスト
    @Test
    void testDefaultConstructor() {
        OriginalDateTime dateTime = new OriginalDateTime();
        assertNotNull(dateTime.toLocalDateTime());
    }

    @Test
    void testStringConstructor() {
        String dateStr = "2025-03-15 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        assertNotNull(dateTime.toLocalDateTime());
        assertEquals(dateStr, dateTime.toString());
    }

    @Test
    void testStringConstructorInvalidDate() {
        String invalidDateStr = "invalid-date";
        OriginalDateTime dateTime = new OriginalDateTime(invalidDateStr);
        assertNull(dateTime.toLocalDateTime());
    }

    @Test
    void testSqlDateConstructor() {
        java.sql.Date sqlDate = java.sql.Date.valueOf("2025-03-15");
        OriginalDateTime dateTime = new OriginalDateTime(sqlDate);
        assertNotNull(dateTime.toLocalDateTime());
    }

    @Test
    void testSqlTimestampConstructor() {
        java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf("2025-03-15 12:30:45");
        OriginalDateTime dateTime = new OriginalDateTime(sqlTimestamp);
        assertNotNull(dateTime.toLocalDateTime());
    }

    // メソッドのテスト
    @Test
    void testGet曜日() {
        String dateStr = "2025-03-15 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        String weekday = dateTime.get曜日();
        assertEquals("(土)", weekday);  // 2025年3月15日は土曜日
    }

    @Test
    void testGetMMdd() {
        String dateStr = "2025-03-15 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        String monthDay = dateTime.getMMdd();
        assertEquals("03/15", monthDay);
    }

    @Test
    void testGetHHmm() {
        String dateStr = "2025-03-15 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        String hourMinute = dateTime.getHHmm();
        assertEquals("12:30", hourMinute);
    }

    @Test
    void testGetHHmmss() {
        String dateStr = "2025-03-15 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        String hourMinuteSecond = dateTime.getHHmmss();
        assertEquals("12:30:45", hourMinuteSecond);
    }

    @Test
    void testGetYyyyMMdd() {
        String dateStr = "2025-03-15 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        String formattedDate = dateTime.getYyyyMMdd();
        assertEquals("03/15", formattedDate);
    }

    @Test
    void testGetYyyy_MM_dd() {
        String dateStr = "2025-03-15 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        String formattedDate = dateTime.getYyyy_MM_dd();
        assertEquals("2025-03-15", formattedDate);
    }

    // 日付差のテスト
    @Test
    void testBetweenDays() {
        String dateStr1 = "2025-03-10 12:30:45";
        String dateStr2 = "2025-03-15 12:30:45";
        OriginalDateTime dateTime1 = new OriginalDateTime(dateStr1);
        OriginalDateTime dateTime2 = new OriginalDateTime(dateStr2);

        int daysBetween = dateTime1.betweenDays(dateTime2);
        assertEquals(5, daysBetween);
    }

    @Test
    void testBetweenMonth() {
        String dateStr1 = "2025-01-10 12:30:45";
        String dateStr2 = "2025-03-10 12:30:45";
        OriginalDateTime dateTime1 = new OriginalDateTime(dateStr1);
        OriginalDateTime dateTime2 = new OriginalDateTime(dateStr2);

        int monthsBetween = dateTime1.betweenMonth(dateTime2);
        assertEquals(2, monthsBetween);
    }

    @Test
    void testBetweenYear() {
        String dateStr1 = "2020-03-10 12:30:45";
        String dateStr2 = "2025-03-10 12:30:45";
        OriginalDateTime dateTime1 = new OriginalDateTime(dateStr1);
        OriginalDateTime dateTime2 = new OriginalDateTime(dateStr2);

        int yearsBetween = dateTime1.betweenYear(dateTime2);
        assertEquals(5, yearsBetween);
    }

    // plusDays() と minusMinutes() のテスト
    @Test
    void testPlusDays() {
        String dateStr = "2025-03-10 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        dateTime.plusDays(5);

        String expectedDate = "2025-03-15 12:30:45";
        assertEquals(expectedDate, dateTime.toString());
    }

    @Test
    void testMinusMinutes() {
        String dateStr = "2025-03-10 12:30:45";
        OriginalDateTime dateTime = new OriginalDateTime(dateStr);
        dateTime.minusMinutes(30);

        String expectedDate = "2025-03-10 12:00:45";
        assertEquals(expectedDate, dateTime.toString());
    }

    // isEmpty() のテスト
    @Test
    void testIsEmpty() {
        OriginalDateTime emptyDateTime = new OriginalDateTime();
        emptyDateTime.dateTime = null;
        assertTrue(emptyDateTime.isEmpty());
        
        String dateStr = "2025-03-10 12:30:45";
        OriginalDateTime nonEmptyDateTime = new OriginalDateTime(dateStr);
        assertFalse(nonEmptyDateTime.isEmpty());
    }

    // compareTo() のテスト
    @Test
    void testCompareTo() {
        String dateStr1 = "2025-03-10 12:30:45";
        String dateStr2 = "2025-03-15 12:30:45";
        OriginalDateTime dateTime1 = new OriginalDateTime(dateStr1);
        OriginalDateTime dateTime2 = new OriginalDateTime(dateStr2);

        assertTrue(dateTime1.compareTo(dateTime2) < 0);
        assertTrue(dateTime2.compareTo(dateTime1) > 0);
        assertEquals(0, dateTime1.compareTo(dateTime1));
    }
}
