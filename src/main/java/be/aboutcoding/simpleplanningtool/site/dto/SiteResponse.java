package be.aboutcoding.simpleplanningtool.site.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SiteResponse(
        Long id,
        String name,
        CustomerResponse customer,
        @JsonProperty("desired_date") LocalDate desiredDate,
        @JsonProperty("planned_date") LocalDate plannedDate,
        @JsonProperty("creation_date") Instant creationDate,
        @JsonProperty("duration_in_days") Integer durationInDays,
        List<WorkerResponse> workers
) {
}
