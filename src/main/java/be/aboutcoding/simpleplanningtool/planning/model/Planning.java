package be.aboutcoding.simpleplanningtool.planning.model;

import java.time.LocalDate;
import java.util.List;

public record Planning(
        LocalDate from,
        LocalDate until,
        List<Workday> workdays
) {
}
