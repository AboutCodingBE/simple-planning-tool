package be.aboutcoding.simpleplanningtool.worker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record UpdateWorkerRequest(
        @JsonProperty("first_name")
        @NotBlank(message = "First name is mandatory")
        String firstName,

        @JsonProperty("last_name")
        @NotBlank(message = "Last name is mandatory")
        String lastName
) {
}
