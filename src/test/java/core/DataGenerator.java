package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy, H:mm");

    public static String getDateTimeFromNow(int daysFromNow) {
        return LocalDateTime.now().plusDays(daysFromNow).format(FORMATTER);
    }

    public static String getUniqueChar() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMHHmmss"));
    }
}
