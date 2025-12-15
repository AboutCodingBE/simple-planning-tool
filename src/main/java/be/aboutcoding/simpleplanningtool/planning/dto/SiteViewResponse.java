package be.aboutcoding.simpleplanningtool.planning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SiteViewResponse(
        Long id,
        String name,
        @JsonProperty("duration_in_days") Integer durationInDays,
        String status
) {
}
