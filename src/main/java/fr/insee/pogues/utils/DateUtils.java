package fr.insee.pogues.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static ZoneId zoneId = ZoneId.systemDefault();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * This function is used to get the date in ISO 8601 format Date
     * @param instant (can be null)
     * @return if date (Instant) is provided, it returns formated Date, if not returns formated of now.
     */
    public static String getIsoDateFromInstant(Instant instant){
        if(instant != null) {
            return instant.atZone(zoneId).format(formatter);
        }
        ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(zoneId);
        return zonedDateTimeNow.format(formatter);
    }

    public static Timestamp convertZonedDateTimeToTimestamp(ZonedDateTime zonedDateTime){
        return Timestamp.from(zonedDateTime.toInstant());
    }

    public static ZonedDateTime convertTimestampToZonedDateTime(Timestamp timestamp){
        return timestamp.toInstant().atZone(ZoneId.systemDefault());
    }
}
