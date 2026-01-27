package be.aboutcoding.simpleplanningtool.planning.dto;

import be.aboutcoding.simpleplanningtool.planning.model.WorkerView;
import com.fasterxml.jackson.annotation.JsonProperty;

public record WorkerResponse(
        @JsonProperty("worker_id") Long workerId,
        @JsonProperty("worker_firstname") String workerFirstname,
        @JsonProperty("worker_lastname") String workerLastname
) {
    public static WorkerResponse from(WorkerView worker) {
        return new WorkerResponse(
                worker.workerId(),
                worker.workerFirstname(),
                worker.workerLastname()
        );
    }
}
