package be.aboutcoding.simpleplanningtool.planning.model;

import java.time.LocalDate;
import java.util.List;

public record Workday(
        LocalDate date,
        Integer week,
        String dayOfWeek,
        List<SiteView> sites
) {
}
