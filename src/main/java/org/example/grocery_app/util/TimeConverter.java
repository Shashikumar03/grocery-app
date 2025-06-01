package org.example.grocery_app.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeConverter {

    // Convert UTC LocalDateTime to IST LocalDateTime
    public LocalDateTime convertUTCtoIST(LocalDateTime utcTime) {
        // Create ZonedDateTime in UTC
        ZonedDateTime utcZoned = utcTime.atZone(ZoneId.of("UTC"));

        // Convert to IST
        ZonedDateTime istZoned = utcZoned.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

        // Return only the LocalDateTime part
        return istZoned.toLocalDateTime();
    }


}
