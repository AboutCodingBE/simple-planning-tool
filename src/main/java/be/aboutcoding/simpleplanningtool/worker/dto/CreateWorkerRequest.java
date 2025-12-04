package be.aboutcoding.simpleplanningtool.worker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateWorkerRequest(@JsonProperty("first_name")String firstName,
        @JsonProperty("last_name") String lastName) {
}