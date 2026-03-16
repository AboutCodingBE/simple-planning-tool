package be.aboutcoding.simpleplanningtool.planning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record PlanSiteForWeekRequest(
        @NotNull(message = "week is mandatory")
        Integer week,

        @NotNull(message = "year is mandatory")
        Integer year,

        @NotNull(message = "site_id is mandatory")
        @JsonProperty("site_id")
        Long siteId
) {
}
