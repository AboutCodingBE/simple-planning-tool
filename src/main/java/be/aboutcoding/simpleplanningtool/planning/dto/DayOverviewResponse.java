package be.aboutcoding.simpleplanningtool.planning.dto;

import be.aboutcoding.simpleplanningtool.planning.model.DayOverview;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record DayOverviewResponse(
        LocalDate date,
        @JsonProperty("plannedSites") List<PlannedSiteResponse> plannedSites
) {
    public static DayOverviewResponse from(DayOverview dayOverview) {
        List<PlannedSiteResponse> plannedSites = dayOverview.plannedSites().stream()
                .map(PlannedSiteResponse::from)
                .toList();
        return new DayOverviewResponse(dayOverview.date(), plannedSites);
    }
}
