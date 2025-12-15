package be.aboutcoding.simpleplanningtool.planning.dto;

import java.time.LocalDate;
import java.util.List;

public record PlanningResponse(
        LocalDate from,
        LocalDate until,
        List<WeekResponse> weeks
) {
}
