package be.aboutcoding.simpleplanningtool.planning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record WorkerDayOverviewResponse(
        LocalDate date,
        @JsonProperty("day_overview") List<WorkerDayDetailDto> dayOverview
) {
}
