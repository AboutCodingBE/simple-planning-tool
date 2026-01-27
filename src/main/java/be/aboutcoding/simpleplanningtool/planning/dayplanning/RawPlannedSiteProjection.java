package be.aboutcoding.simpleplanningtool.planning.dayplanning;

import java.time.LocalDate;

public interface RawPlannedSiteProjection {
    Long getSiteId();
    String getSiteName();
    LocalDate getExecutionDate();
    Integer getDurationInDays();
    LocalDate getEndDate();
    Integer getDaysRemaining();
    String getSiteStatus();
    Long getWorkerId();
    String getWorkerFirstName();
    String getWorkerLastName();
}
