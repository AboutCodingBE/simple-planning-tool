package be.aboutcoding.simpleplanningtool.planning.model;

import java.time.LocalDate;
import java.util.List;

public record DayOverview(
        LocalDate date,
        List<PlannedSite> plannedSites
) {
}
