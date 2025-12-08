package be.aboutcoding.simpleplanningtool.site.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WorkerResponse(
        Long id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {
}
