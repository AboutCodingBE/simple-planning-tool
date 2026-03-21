package be.aboutcoding.simpleplanningtool.planning.model;

import java.util.List;

public record MonthlyOverview(String month, List<WeekOverview> weeks) {
}
