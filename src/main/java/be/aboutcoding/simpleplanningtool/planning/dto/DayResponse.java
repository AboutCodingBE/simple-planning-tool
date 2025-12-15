package be.aboutcoding.simpleplanningtool.planning.dto;

import java.time.LocalDate;
import java.util.List;

public record DayResponse(
        LocalDate date,
        List<SiteViewResponse> sites
) {
}
