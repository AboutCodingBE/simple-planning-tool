package be.aboutcoding.simpleplanningtool.worker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public record WorkerResponse(
        @JsonProperty("id")
        Long id,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("date_of_creation")
        Timestamp dateOfCreation
) {
}
