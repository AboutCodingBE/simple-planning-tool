package be.aboutcoding.simpleplanningtool.planning;

import java.time.LocalDate;

public interface PlanningDataProjection {
    LocalDate getWorkdayDate();
    Integer getWeekNumber();
    String getDayOfWeek();
    Long getSiteId();
    String getSiteName();
    Integer getDurationInDays();
    String getSiteStatus();
}
