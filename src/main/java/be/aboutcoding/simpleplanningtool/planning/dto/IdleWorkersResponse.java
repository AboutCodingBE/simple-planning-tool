package be.aboutcoding.simpleplanningtool.planning.dto;

import be.aboutcoding.simpleplanningtool.worker.Worker;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record IdleWorkersResponse(
        LocalDate date,
        @JsonProperty("idle_workers") List<IdleWorkerDto> idleWorkers
) {
    public static IdleWorkersResponse from(LocalDate date, List<Worker> idleWorkers) {
        List<IdleWorkerDto> idleWorkerDtos = idleWorkers.stream()
                .map(IdleWorkerDto::from)
                .toList();
        return new IdleWorkersResponse(date, idleWorkerDtos);
    }
}
