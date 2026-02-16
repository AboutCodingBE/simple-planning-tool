package be.aboutcoding.simpleplanningtool.planning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WorkerDayDetailDto(
        Long id,
        String firstname,
        String lastname,
        @JsonProperty("current_site") CurrentSiteDto currentSite
) {
}
