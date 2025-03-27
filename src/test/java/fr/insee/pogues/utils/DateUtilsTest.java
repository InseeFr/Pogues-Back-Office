package fr.insee.pogues.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilsTest {

    @BeforeEach
    void setup(){
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Paris")));
    }

    @Test
    void testConversionOfChristmasDate(){
        ZonedDateTime zonedDateTime = ZonedDateTime.of(
                2024,12,25,
                20,45,50,0,
                ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        assertEquals("2024-12-25T20:45:50.000+0100", DateUtils.getIsoDateFromInstant(instant));
    }

    @Test
    void timestampToZonedDateTime(){
        String dateString = "2024-12-25 20:45:50";
        Timestamp timestamp = Timestamp.valueOf(dateString);
        ZonedDateTime zonedDateTimeConverted = DateUtils.convertTimestampToZonedDateTime(timestamp);
        assertEquals(2024,zonedDateTimeConverted.getYear());
        assertEquals(12,zonedDateTimeConverted.getMonthValue());
        assertEquals(25,zonedDateTimeConverted.getDayOfMonth());
        assertEquals(20, zonedDateTimeConverted.getHour());
        assertEquals(45, zonedDateTimeConverted.getMinute());
        assertEquals(50, zonedDateTimeConverted.getSecond());
    }

    @Test
    void zonedDateTimeToTimestamp(){
        ZonedDateTime zonedDateTime = ZonedDateTime.of(
                2024,12,25,
                20,45,50,0,
                ZoneId.systemDefault());
        long longZone = zonedDateTime.toInstant().toEpochMilli();
        Timestamp timestamp = DateUtils.convertZonedDateTimeToTimestamp(zonedDateTime);
        long longTimestamp = timestamp.getTime();
        assertEquals(longZone, longTimestamp);
    }
}
