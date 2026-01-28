package be.aboutcoding.simpleplanningtool.planning.dto;

import be.aboutcoding.simpleplanningtool.worker.Worker;
import com.fasterxml.jackson.annotation.JsonProperty;

public record IdleWorkerDto(
        Long id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {
    public static IdleWorkerDto from(Worker worker) {
        return new IdleWorkerDto(
                worker.getId(),
                worker.getFirstName(),
                worker.getLastName()
        );
    }
}
