package be.aboutcoding.simpleplanningtool.planning.dto;

public record WeekResponse(
        Integer week,
        DayResponse monday,
        DayResponse tuesday,
        DayResponse wednesday,
        DayResponse thursday,
        DayResponse friday,
        DayResponse saturday,
        DayResponse sunday
) {
}
